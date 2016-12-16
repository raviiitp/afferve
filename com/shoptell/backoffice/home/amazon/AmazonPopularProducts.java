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
import static com.shoptell.db.messagelog.MessageEnum.INFO;

import java.util.Arrays;
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
import AWSECommerce.BrowseNodeLookupRequest;
import AWSECommerce.BrowseNodes;
import AWSECommerce.NewReleases;
import AWSECommerce.NewReleases.NewRelease;
import AWSECommerce.TopItemSet;
import AWSECommerce.TopItemSet.TopItem;

import com.shoptell.backoffice.enums.PopularProductEnum;
import com.shoptell.backoffice.home.Info;
import com.shoptell.backoffice.repository.CategoryNodeRepository;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.backoffice.repository.dto.PopularProductDTO;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "AmazonPopularProducts")
public class AmazonPopularProducts extends Info{
	private static final String home = AMAZON.name();
	private static final Logger log = LoggerFactory.getLogger(AmazonPopularProducts.class);

	@Inject
	private ItemSearchUtil util;
	@Inject
	private AmazonApi amazonApi;
	@Inject
	private CategoryNodeRepository categoryNodeRepository;
	private List<CategoryNodeDTO> nodeList;
	private List<PopularProductDTO> prodList;

	@Async
	// @Scheduled(fixedDelayString="${amazon.category.delay}")
	public void init() {
		preprocess();
		try {
			execute();
		} catch (InterruptedException e) {}
		postprocess();
	}

	private void preprocess() {
		prodList = new LinkedList<PopularProductDTO>();
		nodeList = categoryNodeRepository.findAllByHome(home);
	}

	private void postprocess() {
		batchRepository.batchSave(prodList);
		prodList.clear();
	}

	private void execute() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		for (CategoryNodeDTO category : nodeList) {
			map.put(category.getCategoryId(), category.getSearchIndex());
		}
		browseNodeLookupApi(map);
	}

	/*
	 * nodes is a map of BrowseNodeId vs Search index
	 */
	public void browseNodeLookupApi(Map<String, String> nodes) throws InterruptedException {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			log.error("",e1);
		}

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
			
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("KILL THREAD");
			}
			
			request.getBrowseNodeId().clear();
			request.getBrowseNodeId().addAll(keysLoop);
			request.getResponseGroup().addAll(Arrays.asList(new String[] { "TopSellers", "NewReleases" }));
			requests.add(request);

			List<BrowseNodes> response = null;
			try {
				response = amazonApi.browseNodeLookupApiCall(requests);
			} catch (Exception e) {
				log.error("", e);
			}

			if (response != null) {
				for (BrowseNodes resp : response) {
					List<BrowseNode> nodeList = resp.getBrowseNode();
					if (nodeList != null) {
						Iterator<BrowseNode> itr = nodeList.iterator();
						while (itr.hasNext()) {
							BrowseNode node = itr.next();
							populateCategory(node);
						}
					}
				}
			}
		}
	}

	private void populateCategory(BrowseNode node) {
		String category = util.getSubCategory(node.getName(),null);
		if (category != null) {
			List<TopItemSet> topItems = node.getTopItemSet();
			if (topItems != null) {
				int rank = 0;
				for (TopItemSet topItem : topItems) {
					List<TopItem> products = topItem.getTopItem();
					if (products != null) {
						for (TopItem prod : products) {
							String id = prod.getASIN();
							String title = prod.getTitle();
							prodList.add(new PopularProductDTO(category, home, id, title, PopularProductEnum.TOP.name(),++rank));
						}
					}
				}
			}
			NewReleases releases = node.getNewReleases();
			if (releases != null) {
				List<NewRelease> newItems = releases.getNewRelease();
				if (newItems != null) {
					int rank = 0;
					for (NewRelease newItem : newItems) {
						String id = newItem.getASIN();
						String title = newItem.getTitle();
						prodList.add(new PopularProductDTO(category, home, id, title, PopularProductEnum.NEW.name(), ++rank));
					}
				}
			}

			if (prodList.size() > BATCHSIZE) {
				batchRepository.batchSave(prodList);
				prodList.clear();
			}
		}
	}
	
	public void checkout(List<ProcessLog> list) {
		process = processUtil.start(home, ProcessLogJobEnum.POPULAR_PRODUCT.name());
		list.add(process);
		preprocess();
		try {
			execute();
		} catch (Exception e) {
			msgLog.add(INFO, "KILLED DATA THREAD", "KILLED DATA THREAD");
		}
		postprocess();
		list.remove(process);
		processUtil.end(process);
	}
}
