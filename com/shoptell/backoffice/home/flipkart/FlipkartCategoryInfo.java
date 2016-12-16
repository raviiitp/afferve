/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.flipkart;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.home.CategoryInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.db.processlog.ProcessLog;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "FlipkartCategoryInfo")
public class FlipkartCategoryInfo extends CategoryInfo {

	private static final Logger log = LoggerFactory.getLogger(FlipkartCategoryInfo.class);

	private String affiliateId;
	private String affiliateBaseUrl;

	private List<CategoryNodeDTO> categoryNodeList;

	@Inject
	private FlipkartProperties flipkartProperties;
	@Inject
	private FlipkartURLCall flipkartURLCall;

	@PostConstruct
	public void start() {
		home = HomeEnum.FLIPKART;
		this.affiliateId = flipkartProperties.getTrackingID();
		this.affiliateBaseUrl = "https://affiliate-api.flipkart.net/affiliate/api/" + affiliateId + ".json";
	}

	@Async
	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	@Override
	protected void postprocess() {
		batchRepository.batchSave(categoryNodeList);
		categoryNodeList.clear();
	}

	@Override
	protected void execute() {
		log.info("execute() Enter");
		try {
			// Query the API service and get back the result.
			String jsonData = flipkartURLCall.queryService(affiliateBaseUrl, false);
			if (jsonData == null) {
				return;
			}
			// Bookkeep the retrieved data in a local productDirectory Map.
			JSONObject obj = new JSONObject(jsonData);
			JSONObject listing = obj.getJSONObject("apiGroups").getJSONObject("affiliate").getJSONObject("apiListings");
			Iterator<?> keys = listing.keys();
			int i = 0;
			while (keys.hasNext()) {
				String category_name = (String) keys.next();
				JSONObject variants = listing.getJSONObject(category_name).getJSONObject("availableVariants");

				// Sort the variants and get the latest version
				Iterator<?> v_iterator = variants.keys();
				List<String> variant_keys = new ArrayList<String>();
				while (v_iterator.hasNext()) {
					variant_keys.add((String) v_iterator.next());
				}
				Collections.sort(variant_keys, Collections.reverseOrder());

				String category_url = variants.getJSONObject(variant_keys.get(0)).getString("get");

				CategoryNodeDTO tmpCategoryNode = new CategoryNodeDTO();
				tmpCategoryNode.setCategoryId("0" + i++);
				tmpCategoryNode.setCategoryName(category_name);
				tmpCategoryNode.setCategoryUrl(category_url);
				tmpCategoryNode.setHome(HomeEnum.FLIPKART.name());
				tmpCategoryNode.setLeaf(true);
				tmpCategoryNode.setParentId(null);
				tmpCategoryNode.setRoot(true);
				tmpCategoryNode.setSearchIndex(null);
				categoryNodeList.add(tmpCategoryNode);
				if (categoryNodeList.size() > BATCHSIZE) {
					batchRepository.batchSave(categoryNodeList);
					categoryNodeList.clear();
				}
			}

		} catch (Exception e) {
			msgLog.addError(e);
			log.error("EXECUTION ERROR", e);
		}
		log.info("execute() Exit");
	}

	@Override
	protected void preprocess() {
		categoryNodeList = new ArrayList<CategoryNodeDTO>();
	}

	/**
	 * @return the list
	 */
	public List<CategoryNodeDTO> getList() {
		return categoryNodeList;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<CategoryNodeDTO> list) {
		this.categoryNodeList = list;
	}

	public boolean getStatus(List<ProcessLog> processList) {
		if (processList.contains(process)) {
			return false;
		}
		return true;
	}

	public List<CategoryNodeDTO> findLeaf() {
		List<CategoryNodeDTO> categoryNodeList = new LinkedList<CategoryNodeDTO>();
		for (FlipkartCategoryEnum category : FlipkartCategoryEnum.values()) {
			CategoryEnum[] subCategory = category.getSubCategory();
			if (subCategory != null && subCategory.length > 0) {
				categoryNodeList.addAll(categoryNodeRepository.findLeaf(HomeEnum.FLIPKART.name(), category.name()));
			}
		}
		return categoryNodeList;
	}

}
