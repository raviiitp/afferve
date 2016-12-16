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
import static com.shoptell.backoffice.BackofficeConstants.MAX_EMPTY_QUERY_COUNT;

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

@Named(value = "FlipkartShoppingApi")
public class FlipkartShoppingApi extends ProductInfo {

	private static final Logger log = LoggerFactory.getLogger(FlipkartShoppingApi.class);

	@Inject
	private FlipkartURLCall flipkartURLCall;
	@Inject
	private FlipkartShoppingApiUtil flipkartShoppingApiUtil;
	@Inject
	private FlipkartCategoryInfo categoryInfo;

	private List<HomeProductInfoDTO> productInfoList;

	private List<CategoryNodeDTO> categoryNodeList;
	
	public static Map<CategoryEnum, Set<String>> flipkartPopularMap = new HashMap<CategoryEnum, Set<String>>();

	@PostConstruct
	public void start() {
		home = HomeEnum.FLIPKART;
		//constructMap();
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
		preprocess();
		try {
			priceUpdater();
		} catch (InterruptedException e) {
			log.error("INIT EXCEPTION", e);
		}
	}

	@Override
	protected void postprocess() {
		batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
		productInfoList.clear();
	}

	@Override
	protected void execute() throws InterruptedException {
		log.info("execute() Enter");
		for (CategoryNodeDTO category : categoryNodeList) {
			log.info("execute() Category - "+category.getCategoryName());
			String queryUrl = category.getCategoryUrl()+"&inStock=true";
			try {
				int queryCount = 0;
				while (StringUtils.isNotBlank(queryUrl) && !queryUrl.equals("null")) {

					// Stopping running Thread
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("KILL THREAD");
					}

					String jsonData = flipkartURLCall.queryService(queryUrl, false);
					
					if (jsonData == null){
						return;
					}

					JSONObject obj = new JSONObject(jsonData);
					JSONArray productArray = obj.getJSONArray("productInfoList");

					for (int i = 0; i < productArray.length(); i++) {
						HomeProductInfoDTO productInfo = flipkartShoppingApiUtil.ProcessJSONObject(productArray.getJSONObject(i), category.getCategoryId(),
								category.getCategoryName());
						if (productInfoList != null && productInfo != null) {
							productInfoList.add(productInfo);
							if (productInfoList.size() > BATCHSIZE) {
								// persist
								batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
								productInfoList.clear();
							}
							queryCount = 0;
						}
					}
					queryUrl = obj.optString("nextUrl", "");
					if (++queryCount > MAX_EMPTY_QUERY_COUNT){
						log.info("Queries Exceed For Category "+category.getCategoryName());
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
	
	@Override
	protected void priceUpdater() throws InterruptedException {
		log.info("priceUpdater() Enter");
		for (CategoryEnum category : CategoryEnum.values()) {
			if (CategoryEnum.ALL.equals(category)) {
				continue;
			}
			if (flipkartPopularMap.containsKey(category)) {
				Set<String> set = flipkartPopularMap.get(category);
				if (set != null && set.size() > 0) {
					for (String tmp : set) {
						priceCall(tmp, category.name());
					}
				}
			}
		}
		log.info("priceUpdater() Exit");
	}
	
	public HomeProductInfoDTO priceCall(String id, String category) throws InterruptedException {
		//log.info("priceCall() Enter");
		try {
			String jsonData = flipkartURLCall.queryService("https://affiliate-api.flipkart.net/affiliate/1.0/product.json?id=" + id, true);
			if (jsonData == null){
				return null;
			}
			JSONObject obj = new JSONObject(jsonData);
				HomeProductInfoDTO productInfo = flipkartShoppingApiUtil
						.ProcessJSONObject(obj, null, category);
				if (productInfo != null && StringUtils.isNotBlank(productInfo.getId()) && productInfo.getSellingPrice() > 0) {
					return productInfo;
			}
		} catch (ProductException e){
			log.info("Error Price Call For Flipkart - {},{}",id,category);
			msgLog.addError(e);
		}
		catch (InterruptedException e) {
			throw new InterruptedException("KILL THREAD");
		} catch (Exception e) {
			//msgLog.addError(e);
			//msgLog.add(MessageEnum.ERROR, e.getMessage(), "id : "+id);
		}
		finally{
			//log.info("priceCall() Exit");
		}
		return null;
	}
	
	protected void priceGrabber() throws InterruptedException{
		log.info("priceGrabber() Enter");
		for (CategoryNodeDTO category : categoryNodeList) {
			String queryUrl = category.getCategoryUrl();
			try {
				while (queryUrl != null && !queryUrl.isEmpty() && !queryUrl.equals("null")) {

					// Stopping running Thread
					if (Thread.currentThread().isInterrupted()) {
						throw new InterruptedException("KILL THREAD");
					}

					String jsonData = flipkartURLCall.queryService(queryUrl, false);
					if (jsonData == null){
						return;
					}
					JSONObject obj = new JSONObject(jsonData);
					JSONArray productArray = obj.getJSONArray("productInfoList");
					for (int i = 0; i < productArray.length(); i++) {
						HomeProductInfoDTO productInfo = flipkartShoppingApiUtil.ProcessJSONObject(productArray.getJSONObject(i), category.getCategoryId(),
								category.getCategoryName());
						if (productInfo != null && StringUtils.isNotBlank(productInfo.getId()) && productInfo.getSellingPrice() > 0){
							//batchRepository.updatePrice(home,productInfo.getSubCategoryName(),productInfo.getId(),productInfo.getSellingPrice());
						}
					}
					queryUrl = obj.optString("nextUrl", "");
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
		log.info("priceGrabber() Exit");
	}

	@Override
	protected void preprocess() {
		// categoryNodeRepository.findAllLeaves(HomeEnum.FLIPKART.name());
		// categoryNodeList = categoryNodeRepository.getLeaf("015","018","056");
		categoryNodeList = categoryInfo.findLeaf();
		productInfoList = new ArrayList<HomeProductInfoDTO>();
	}

	@SuppressWarnings("unused")
	private void dealOfTheDay() {
		try {
			String jsonData = flipkartURLCall.queryService("https://affiliate-api.flipkart.net/affiliate/offers/v1/dotd/json",false);
			if (jsonData != null){
				log.info("dealOfTheDay - {}", jsonData);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@SuppressWarnings("unused")
	private void topOffers() {
		try {
			String jsonData = flipkartURLCall.queryService("https://affiliate-api.flipkart.net/affiliate/offers/v1/top/json", false);
			if (jsonData != null){
				log.info("topOffers - {}", jsonData);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
