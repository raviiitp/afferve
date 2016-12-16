/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.enums.HomeEnum.AMAZON;
import static com.shoptell.db.messagelog.MessageEnum.DEBUG;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import AWSECommerce.BrowseNode;
import AWSECommerce.BrowseNode.Ancestors;
import AWSECommerce.BrowseNode.Children;
import AWSECommerce.BrowseNodeLookupRequest;
import AWSECommerce.BrowseNodes;

import com.shoptell.backoffice.home.CategoryInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;

/**
 * 
 * @author abhishekagarwal
 *
 */
@Named(value = "AmazonCategoryInfo")
public class AmazonCategoryInfo extends CategoryInfo{
	private static final Logger log = LoggerFactory.getLogger(AmazonCategoryInfo.class);
	private static final String amazon = AMAZON.name();

	@Inject
	private AmazonApi amazonApi;
	private List<CategoryNodeDTO> rootNodeList;
	private List<CategoryNodeDTO> nodeList;
	private StringBuilder errorNodes;
	
	@Async
	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	@Override
	protected void postprocess() {
		msgLog.add(DEBUG, "Error BrowseNodes at Time: " + new Date(System.currentTimeMillis()), errorNodes.toString());
		batchRepository.batchSave(nodeList);
		nodeList.clear();
	}

	@Override
	protected void execute() {
		Map<String, String> map = new HashMap<String, String>();
		for (CategoryNodeDTO category : rootNodeList) {
			
			//Stopping running Thread
			if (Thread.currentThread().isInterrupted()){
				return;
			}

			map.clear();
			map.put(category.getCategoryId(), category.getSearchIndex());

			List<CategoryNodeDTO> categoryNodeList = browseNodeLookupApi(map);

			if (categoryNodeList != null && categoryNodeList.size() > 0) {
				nodeList.addAll(categoryNodeList);
			}
			
			if (nodeList.size() > BATCHSIZE) {
				batchRepository.batchSave(nodeList);
				nodeList.clear();
			}
		}
	}

	@Override
	protected void preprocess() {
		nodeList = new LinkedList<CategoryNodeDTO>();
		errorNodes = new StringBuilder();
		rootNodeList = categoryNodeRepository.findAllRoot(amazon);
	}

	/*
	 * nodes is a map of BrowseNodeId vs Search index
	 */
	public List<CategoryNodeDTO> browseNodeLookupApi(Map<String, String> nodes) {
		log.info("BrowseNodeLookupApi.browseNodeLookupApi() Start - ");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			log.error(e1.getMessage());
			e1.printStackTrace();
		}

		List<CategoryNodeDTO> categoryNodeList = new LinkedList<CategoryNodeDTO>();

		List<BrowseNodeLookupRequest> requests = new LinkedList<BrowseNodeLookupRequest>();
		BrowseNodeLookupRequest request = new BrowseNodeLookupRequest();

		Set<Set<String>> keySet = new HashSet<Set<String>>();

		Set<String> keys = nodes.keySet();

		if (nodes.keySet().size() > 10) {
			keys = new HashSet<String>();
			int index = 1;
			for (String ky : nodes.keySet()) {
				keys.add(ky);
				index++;
				if (index % 10 == 0) {
					keySet.add(keys);
					keys = new HashSet<String>();
				}
			}
		}
		keySet.add(keys);
		for (Set<String> keysLoop : keySet) {
			request.getBrowseNodeId().clear();
			request.getBrowseNodeId().addAll(keysLoop);
			/*request.getResponseGroup().addAll(Arrays.asList(new String[] { "TopSellers", "NewReleases" }));*/
			requests.add(request);

			List<BrowseNodes> response = null;
			try {
				response = amazonApi.browseNodeLookupApiCall(requests);
			} catch (Exception e) {
				for (String errKey : keys)
					errorNodes.append(errKey).append(" ,");
			}

			if (response != null) {
				for (BrowseNodes resp : response) {
					List<BrowseNode> nodeList = resp.getBrowseNode();
					if (nodeList != null) {
						Iterator<BrowseNode> itr = nodeList.iterator();
						while (itr.hasNext()) {
							BrowseNode node = itr.next();
							String index = nodes.get(node.getBrowseNodeId());

							populateCategory(index, node, categoryNodeList);
						}
					}
				}
			}
		}

		log.info("BrowseNodeLookupApi.browseNodeLookupApi() End.");
		return categoryNodeList;
	}

	private void browseChildNodes(List<BrowseNode> childs, String index, List<CategoryNodeDTO> categoryNodeList) {
		if (childs == null)
			return;

		Map<String, String> map = new HashMap<String, String>();
		for (BrowseNode node : childs) {
			map.put(node.getBrowseNodeId(), index);
		}

		categoryNodeList.addAll(browseNodeLookupApi(map));
	}

	private void populateCategory(String index, BrowseNode node, List<CategoryNodeDTO> categoryNodeList) {
		CategoryNodeDTO categoryNode = new CategoryNodeDTO(node.getBrowseNodeId(), node.getName(), null, index, false, false);
		categoryNode.setHome(amazon);
		Children children = node.getChildren();
		if (children != null) {
			List<BrowseNode> childNodes = children.getBrowseNode();
			if (childNodes != null) {
				browseChildNodes(childNodes, index, categoryNodeList);
			}
		}
		else {
			categoryNode.setLeaf(true);
		}

		Ancestors ancestor = node.getAncestors();
		if (ancestor != null) {
			categoryNode.setParentId(ancestor.getBrowseNode().get(0).getBrowseNodeId());
		}
		else {
			categoryNode.setRoot(true);
		}
		categoryNodeList.add(categoryNode);
	}

	/**
	 * @return the errorNodes
	 */
	public StringBuilder getErrorNodes() {
		return errorNodes;
	}

	/**
	 * @param errorNodes
	 *            the errorNodes to set
	 */
	public void setErrorNodes(StringBuilder errorNodes) {
		this.errorNodes = errorNodes;
	}

	public List<CategoryNodeDTO> findLeaf() {
		List<CategoryNodeDTO> categoryNodeList = new LinkedList<CategoryNodeDTO>();
		
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Speakers"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Headphones"));
		
		//pendrive
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Pen Drives"));
		
		//watches
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Watches"));
		
		//eyewear
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Spectacle Frames"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Sunglasses"));
		
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Smartphones"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Tablets"));
		
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Air Conditioners & Coolers"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Televisions"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Laptops"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Desktops"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Refrigerators"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Washing Machines & Dryers"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Microwave Ovens"));
		
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Point & Shoot Digital Cameras"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Digital SLRs"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "SLRs"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Spy Cameras"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Instant Cameras"));
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Mirrorless System Cameras"));
		
		//luggage bags
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "luggage"));
		
		//Gaming Console
		categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Consoles"));
		
		//categoryNodeList.addAll(categoryNodeRepository.findAllLeaves(amazon));
		return categoryNodeList;
	}
	
	public List<CategoryNodeDTO> findLeafBySearchIndex() {
		List<CategoryNodeDTO> categoryNodeList = new LinkedList<CategoryNodeDTO>();
		//categoryNodeList.addAll(categoryNodeRepository.findLeaf(amazon, "Electronics"););
		categoryNodeList.addAll(categoryNodeRepository.findLeafBySearchIndex(amazon, "Books"));
		return categoryNodeList;
	}

	public String getNameFromId(String parentId) {
		return categoryNodeRepository.getNameFromId(parentId);
	}
}