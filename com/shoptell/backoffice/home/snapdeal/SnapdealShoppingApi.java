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
import static com.shoptell.backoffice.BackofficeConstants.MAX_REQUEST_COUNT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.home.ProductException;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;

@Named(value = "SnapdealShoppingApi")
public class SnapdealShoppingApi extends ProductInfo {
	private static final Logger log = LoggerFactory.getLogger(SnapdealShoppingApi.class);
	@Inject
	private SnapdealURLCall SnapdealURLCall;
	@Inject
	private SnapdealShoppingApiUtil SnapdealShoppingApiUtil;
	@Inject
	private SnapdealCategoryInfo categoryInfo;

	private List<HomeProductInfoDTO> productInfoList;

	private String queryUrl;

	private List<CategoryNodeDTO> categoryNodeList;
	
	public static Map<CategoryEnum, Set<String>> snapdealPopularMap = new HashMap<CategoryEnum, Set<String>>();

	@PostConstruct
	public void start() {
		home = HomeEnum.SNAPDEAL;
	}
	
	@Async
	public void init() {
		log.info("init() Enter");
		preprocess();
		try {
			execute();
		} catch (InterruptedException e) {
			log.error("INIT EXCEPTION", e);
		}
		postprocess();
		log.info("init() Exit");
	}

	@Async
	public void update() {
		log.info("update() Enter");
		preprocess();
		try {
			priceUpdater();
		} catch (InterruptedException e) {
			log.error("INIT EXCEPTION", e);
		}
		log.info("update() Exit");
	}

	@Override
	protected void postprocess() {
		log.info("postprocess() Enter");
		batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
		productInfoList.clear();
		log.info("postprocess() Exit");
	}

	@Override
	protected void execute() throws InterruptedException {
		log.info("execute() Enter");
		for (CategoryNodeDTO category : categoryNodeList) {
			setQueryUrl(category.getCategoryUrl());
			int requestCount = 0;
			try {
				while (queryUrl != null && !queryUrl.isEmpty() && !queryUrl.equals("null")) {

					// Stopping running Thread
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("KILL THREAD");
					}

					String jsonData = SnapdealURLCall.queryService(queryUrl, false);
					
					if (jsonData == null){
						return;
					}

					JSONObject obj = new JSONObject(jsonData);
					JSONArray productArray = obj.getJSONArray("products");

					for (int i = 0; i < productArray.length(); i++) {

						HomeProductInfoDTO productInfo = SnapdealShoppingApiUtil.ProcessJSONObject(productArray.getJSONObject(i), category.getCategoryId(),
								category.getCategoryName(), false);
						if (productInfo != null) {
							productInfoList.add(productInfo);
							if (productInfoList.size() > BATCHSIZE) {
								batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
								productInfoList.clear();
							}
						}
					}

					queryUrl = obj.optString("nextUrl", "");
					if (++requestCount > MAX_REQUEST_COUNT){
						if (productInfoList.size() > 0) {
							batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
							productInfoList.clear();
						}
						break;
					}
				}
			} catch (InterruptedException e) {
				throw new InterruptedException("KILL THREAD");
			} catch (Exception e) {
				msgLog.addError(e);
				log.error("EXECUTION ERROR", e);
				if ("URL expired".equalsIgnoreCase(e.getMessage())){
					categoryInfo.init();
				}
			}
		}
		log.info("execute() Exit");
	}

	/**
	 * @return the queryUrl
	 */
	public String getQueryUrl() {
		return queryUrl;
	}

	/**
	 * @param queryUrl
	 *            the queryUrl to set
	 */
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	@Override
	protected void preprocess() {
		log.info("preprocess() Enter");
		// categoryNodeRepository.findAllLeaves(HomeEnum.SNAPDEAL.name());
		categoryNodeList = categoryInfo.findLeaf();
		productInfoList = new ArrayList<HomeProductInfoDTO>();
		log.info("preprocess() Exit");
	}
	
	public HomeProductInfoDTO priceCall(String id, String category) throws InterruptedException {
		//log.info("priceCall() Enter");
		try {
			String jsonData = SnapdealURLCall.queryService("http://affiliate-feeds.snapdeal.com/feed/product?id=" + id, true);
			if (jsonData == null){
				return null;
			}
			JSONObject obj = new JSONObject(jsonData);
			HomeProductInfoDTO productInfo = SnapdealShoppingApiUtil.ProcessJSONObject(obj, null, category, true);
			if (productInfo != null && StringUtils.isNotBlank(productInfo.getId()) && productInfo.getSellingPrice() > 0) {
				return productInfo;
			}
		} catch (ProductException e){
			log.info("Error Price Call For Snapdeal - {},{}",id,category);
			msgLog.addError(e);
		}
		catch (InterruptedException e) {
			throw new InterruptedException("KILL THREAD");
		} catch (Exception e) {
			msgLog.addError(e);
			log.error("EXECUTION ERROR", e);
		} finally {
		//	log.info("priceCall() Exit");
		}
		return null;
	}

	@Override
	protected void priceUpdater() throws InterruptedException {
//		log.info("priceUpdater() Enter");
//		for (CategoryNodeDTO category : categoryNodeList) {
//			setQueryUrl(category.getCategoryUrl());
//			try {
//				int requestCount = 0;
//				while (queryUrl != null && !queryUrl.isEmpty() && !queryUrl.equals("null")) {
//					// Stopping running Thread
//					if (Thread.currentThread().isInterrupted()) {
//						throw new InterruptedException("KILL THREAD");
//					}
//					String jsonData = SnapdealURLCall.queryService(queryUrl, false);
//					if (jsonData == null){
//						return;
//					}
//					JSONObject obj = new JSONObject(jsonData);
//					if (!obj.isNull("products")) {
//						JSONArray productArray = obj.getJSONArray("products");
//						for (int i = 0; i < productArray.length(); i++) {
//							JSONObject element = productArray.getJSONObject(i);
//							if (element != null) {
//								String id = String.valueOf(element.getLong("id"));
//								String category_tmp = element.getString("subCategoryName");
//								double sellingPrice = element.getInt("offerPrice");
//
//								String subCategory = SnapdealShoppingApiUtil.getSubCategory(category_tmp);
//								if (StringUtils.isBlank(subCategory)) {
//									continue;
//								}
//								if (StringUtils.isNotBlank(id) && sellingPrice > 0) {
//									String available = element.getString("availability");
//									batchRepository.updatePrice(home, subCategory, id, sellingPrice, "in stock".equalsIgnoreCase(available));
//								}
//							}
//						}
//					}
//					queryUrl = obj.optString("nextUrl", "");
//					if (++requestCount > BackofficeConstants.MAX_REQUEST_COUNT) {
//						break;
//					}
//				}
//			} catch (InterruptedException e) {
//				throw new InterruptedException("KILL THREAD");
//			} catch (Exception e) {
//				msgLog.addError(e);
//				log.error("EXECUTION ERROR", e);
//				if ("URL expired".equalsIgnoreCase(e.getMessage())){
//					categoryInfo.init();
//				}
//			}
//		}
//		log.info("priceUpdater() Exit");
	}
}
