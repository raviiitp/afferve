/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.snapdeal;

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
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.db.processlog.ProcessLog;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "SnapdealCategoryInfo")
public class SnapdealCategoryInfo extends CategoryInfo {
	private static final Logger log = LoggerFactory.getLogger(SnapdealCategoryInfo.class);

	private String affiliateId;

	private String affiliateBaseUrl;

	private List<CategoryNodeDTO> categoryNodeList;

	@Inject
	private SnapdealURLCall call;
	@Inject
	private SnapdealProperties sdProp;

	@PostConstruct
	public void start() {
		home = HomeEnum.SNAPDEAL;
		this.setAffiliateId(sdProp.getTrackingID());
		this.affiliateBaseUrl = "http://affiliate-feeds.snapdeal.com/feed/" + getAffiliateId() + ".json";
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
			String jsonData = call.queryService(affiliateBaseUrl, false);

			if (jsonData == null) {
				return;
			}

			JSONObject obj = new JSONObject(jsonData);
			JSONObject listing = obj.getJSONObject("apiGroups").getJSONObject("Affiliate").getJSONObject("listingsAvailable");
			Iterator<?> keys = listing.keys();
			int i = 0;
			while (keys.hasNext()) {
				String category_name = (String) keys.next();
				JSONObject variants = listing.getJSONObject(category_name).getJSONObject("listingVersions");
				Iterator<?> v_iterator = variants.keys();
				List<String> variant_keys = new ArrayList<String>();
				while (v_iterator.hasNext()) {
					variant_keys.add((String) v_iterator.next());
				}
				Collections.sort(variant_keys, Collections.reverseOrder());

				String category_url = variants.getJSONObject(variant_keys.get(0)).getString("get");

				CategoryNodeDTO tmpCategoryNode = new CategoryNodeDTO();
				tmpCategoryNode.setCategoryId("00" + i++);
				tmpCategoryNode.setCategoryName(category_name);
				tmpCategoryNode.setCategoryUrl(category_url);
				tmpCategoryNode.setHome(HomeEnum.SNAPDEAL.name());
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
		categoryNodeList = new LinkedList<CategoryNodeDTO>();
	}

	/**
	 * @return the affiliateId
	 */
	public String getAffiliateId() {
		return affiliateId;
	}

	/**
	 * @param affiliateId
	 *            the affiliateId to set
	 */
	public void setAffiliateId(String affiliateId) {
		this.affiliateId = affiliateId;
	}

	/**
	 * @return the categoryNodeList
	 */
	public List<CategoryNodeDTO> getCategoryNodeList() {
		return categoryNodeList;
	}

	/**
	 * @param categoryNodeList
	 *            the categoryNodeList to set
	 */
	public void setCategoryNodeList(List<CategoryNodeDTO> categoryNodeList) {
		this.categoryNodeList = categoryNodeList;
	}

	/**
	 * @return the batchRepository
	 */
	public BatchRepository getBatchRepository() {
		return batchRepository;
	}

	/**
	 * @param batchRepository
	 *            the batchRepository to set
	 */
	public void setBatchRepository(BatchRepository batchRepository) {
		this.batchRepository = batchRepository;
	}

	public boolean getStatus(List<ProcessLog> processList) {
		if (processList.contains(process)) {
			return false;
		}
		return true;
	}

	public List<CategoryNodeDTO> findLeaf() {
		List<CategoryNodeDTO> categoryNodes = new LinkedList<CategoryNodeDTO>();
		for (SnapdealCategoryEnum category : SnapdealCategoryEnum.values()) {
			CategoryEnum[] subCategory = category.getSubCategory();
			if (subCategory != null && subCategory.length > 0) {
				categoryNodes.addAll(categoryNodeRepository.findLeaf(HomeEnum.SNAPDEAL.name(), category.name()));
			}
		}
		return categoryNodes;
	}
}
