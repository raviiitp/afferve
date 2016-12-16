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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ShippingOptionsDTO;
import com.shoptell.backoffice.repository.util.HomeProductInfoUtil;
import com.shoptell.db.messagelog.MessageLogUtil;

@Named
public class FlipkartShoppingApiUtil implements Flipkart{
	@Inject
	private HomeProductInfoUtil homeProductInfoUtil;
	@Inject
	private MessageLogUtil msgLog;
	
	private static final Logger log = LoggerFactory.getLogger(FlipkartShoppingApiUtil.class);

	/***
	 *
	 * @param JSONArray
	 *            productArray.getJSONObject(i)
	 * @return an instance of productInfo
	 * @throws AffiliateAPIException
	 * @throws JSONException
	 */
	public HomeProductInfoDTO ProcessJSONObject(JSONObject productArrayElement, String categoryId, String categoryName) {
		try {
			HomeProductInfoDTO pInfo = new HomeProductInfoDTO();

			pInfo.setSalesRank(0); // TODO Set Rank When Available

			JSONObject productBaseInfo = productArrayElement.getJSONObject("productBaseInfoV1");
			
			if (productBaseInfo.isNull("title")) {
				return null;
			}
			
			String categoryPath = null;
			if (!productBaseInfo.isNull("categoryPath")){
				categoryPath = productBaseInfo.getString("categoryPath");
			}

			pInfo.setHome(HomeEnum.FLIPKART.name());
			if (StringUtils.isNotBlank(categoryId)){
				pInfo.setCategoryId(categoryId);
			}
			pInfo.setCategoryName(categoryName);
			pInfo.setSubCategoryName(categoryName);// it will be updated in
													// processor part
			pInfo.setId(productBaseInfo.getString("productId"));
			// JSONArray categoryPaths =
			// productBaseInfo.getJSONArray("categoryPath");
			// pInfo.setCategoryPaths(getCategoryPaths_asSet(categoryPaths));

			pInfo.setTitle(productBaseInfo.getString("title"));
			if (StringUtils.isNotBlank(pInfo.getTitle())) {
				pInfo.setTitle(pInfo.getTitle().toUpperCase());
			}
			pInfo.setOriginalTitle(pInfo.getTitle());
			pInfo.setDescription(productBaseInfo.optString("productDescription", ""));
			if (StringUtils.isNotBlank(pInfo.getDescription())) {
				pInfo.setDescription(pInfo.getDescription().toUpperCase());
			}

			/*if (StringUtils.isNotBlank(pInfo.getDescription())) {
				pInfo.setFeatures(populateFeatures(pInfo.getDescription()));
			}*/

			pInfo.setImageUrls(getImageUrls_asMap(productBaseInfo.getJSONObject("imageUrls")));
			// Image Url
			if (pInfo.getImageUrls() != null) {
				if (pInfo.getImageUrls().containsKey("200x200")) {
					pInfo.setImageUrl(pInfo.getImageUrls().get("200x200"));
					pInfo.setImageUrlMedium(pInfo.getImageUrls().get("200x200"));
				}
				if (pInfo.getImageUrls().containsKey("100x100")) {
					pInfo.setImageUrlSmall(pInfo.getImageUrls().get("100x100"));
				}
				if (pInfo.getImageUrls().containsKey("400x400")) {
					pInfo.setImageUrlLarge(pInfo.getImageUrls().get("400x400"));
				}
			}
			if (!productBaseInfo.isNull("maximumRetailPrice")) {
				JSONObject mrp = productBaseInfo.getJSONObject("maximumRetailPrice");
				if (mrp != null) {
					pInfo.setMrp(new Double(mrp.getDouble("amount")));
				}
			}
			if (!productBaseInfo.isNull("flipkartSellingPrice")) {
				JSONObject fsp = productBaseInfo.getJSONObject("flipkartSellingPrice");
				if (fsp != null) {
					pInfo.setSellingPrice(new Double(fsp.getDouble("amount")));
				}
			}
			if (!productBaseInfo.isNull("flipkartSpecialPrice")) {
				JSONObject fsp = productBaseInfo.getJSONObject("flipkartSpecialPrice");
				if (fsp != null) {
					Double spclPrice = new Double(fsp.getDouble("amount"));
					if (pInfo.getSellingPrice() == 0 || pInfo.getSellingPrice() > spclPrice){
						pInfo.setSellingPrice(spclPrice);
					}
				}
			}
			pInfo.setProductUrl(productBaseInfo.getString("productUrl"));
			pInfo.setProductBrand(productBaseInfo.getString("productBrand"));
			if (!productBaseInfo.isNull("inStock")) {
				pInfo.setInStock(productBaseInfo.getBoolean("inStock"));
			}
			pInfo.setCodAvailable(productBaseInfo.optBoolean("codAvailable", false));
			if (!productBaseInfo.isNull("discountPercentage")) {
				pInfo.setDiscountPercentage(new Double(productBaseInfo.getDouble("discountPercentage")).toString());
			}
			pInfo.setOffers(getOffers_asSet(productBaseInfo.optJSONArray("offers")));
			JSONObject attributes = productBaseInfo.getJSONObject("attributes");
			pInfo.setSize(attributes.optString("size", null));
			if (StringUtils.isNotBlank(pInfo.getSize())) {
				pInfo.setSize(pInfo.getSize().toUpperCase());
			}
			// log.info("{}-{}",pInfo.getSize(),pInfo.getTitle());
			pInfo.setColor(attributes.optString("color", null));
			if (pInfo.getColor() != null) {
				pInfo.setColor(pInfo.getColor().toUpperCase());
			}
			pInfo.setSizeUnit(attributes.optString("sizeUnit", null));
			
			if (CategoryEnum.TELEVISION.name().equals(pInfo.getSubCategoryName())){
				String sze = attributes.optString("displaySize",null);
				if (StringUtils.isNotBlank(sze)){
					pInfo.setSize(sze+"\"");
				}
			}
			// pInfo.setSizeVariants(attributes.optString("sizeVariants",
			// null));
			// pInfo.setColorVariants(attributes.optString("colorVariants",
			// null));
			// pInfo.setStyleCode(attributes.optString("styleCode", null));

			// JSONObject productShippingBaseInfo =
			// productArrayElement.getJSONObject("productShippingInfoV1");
			// JSONArray shippingOptions =
			// productShippingBaseInfo.optJSONArray("shippingOptions");
			// pInfo.setShippingOptions(getShippingOptions_asShippingOptions(shippingOptions));

			// pinfo.setOffset(productArray.getJSONObject(i).getString("offset"));
			// TODO check offset value
			pInfo.setOffset(null);
			
			pInfo.setSubCategoryName(getSubCategory(categoryName, categoryPath, pInfo));

			if (StringUtils.isBlank(pInfo.getSubCategoryName())) {
				return null;
			}
			
			pInfo.setTags(homeProductInfoUtil.getTag_asSet(pInfo.getTitle(), pInfo.getColor(), pInfo.getProductBrand()));
			
			JSONObject categoryBaseInfo = productArrayElement.getJSONObject("categorySpecificInfoV1");
			
			if (!categoryBaseInfo.isNull("detailedSpecs")) {
				List<String> features = new LinkedList<String>();
				JSONArray specs = categoryBaseInfo.getJSONArray("detailedSpecs");
				if (specs != null && specs.length() > 0) {
					for (int i = 0; i < specs.length(); i++) {
						String tmp = specs.get(i).toString().toUpperCase();
						features.add(tmp);
					}
					if (features.size() > 0) {
						pInfo.setFeatures(features);
					}
				}
			}
			
			if (!categoryBaseInfo.isNull("specificationList")) {
				JSONArray array = categoryBaseInfo.getJSONArray("specificationList");
				pInfo.setSpecificationJson(array.toString());
			}
			return pInfo;
		} catch (Exception e) {
			msgLog.addError(e);
		}
		return null;
	}

	public String getSubCategory(String category, String categoryPath, HomeProductInfoDTO pInfo) {
		// System.out.println(category +" | "+categoryPath);
		String subCategoryFinal = null;
		String[] paths = null;
		if (StringUtils.isNotBlank(categoryPath)) {
			paths = categoryPath.trim().split(">");
			String tmp = paths[paths.length - 1];
			pInfo.setType(tmp);
		}
		if (StringUtils.isNotBlank(category)) {
			CategoryEnum subCategory = CategoryEnum.getCategory(category);
			if (subCategory != null) {
				subCategoryFinal = subCategory.name();
			}
		}
		if (subCategoryFinal == null && StringUtils.isNotBlank(pInfo.getType())) {
			for (int i = paths.length - 1; i >= 0; i--) {
				CategoryEnum subCategory = CategoryEnum.getCategory(paths[i]);
				if (subCategory != null) {
					subCategoryFinal = subCategory.name();
				}
			}
		}
		
		if (subCategoryFinal != null && subCategoryFinal.equalsIgnoreCase(category)) {
			return subCategoryFinal;
		}
		
		if (isMatch(category, subCategoryFinal)) {
			return subCategoryFinal;
		}
		return null;
	}

	public Set<String> getCategoryPaths_asSet(JSONArray categoryPaths2) {
		Set<String> categoryPaths = null;
		try {
			if (categoryPaths2 == null || categoryPaths2.length() == 0) {
				categoryPaths = null;
			}
			else {
				categoryPaths = new HashSet<String>();
				for (int i = 0; i < categoryPaths2.getJSONArray(0).length(); i++) {
					categoryPaths.add(categoryPaths2.getJSONArray(0).getJSONObject(i).getString("title"));
				}
			}
		} catch (JSONException e) {
			log.error("", e);
		}
		return categoryPaths;
	}

	public Map<String, String> getImageUrls_asMap(JSONObject imageUrls2) throws JSONException {
		Map<String, String> imageUrls = null;
		if (imageUrls2 == null || imageUrls2.length() == 0) {
			imageUrls = null;
		}
		else {
			Iterator<?> imgUrls_iterator = imageUrls2.keys();
			if (imgUrls_iterator.hasNext()) {
				imageUrls = new HashMap<String, String>();
			}
			while (imgUrls_iterator.hasNext()) {
				String imgUrl_key = (String) imgUrls_iterator.next();
				imageUrls.put(imgUrl_key, imageUrls2.getString(imgUrl_key));
			}
		}
		return imageUrls;
	}

	public Set<String> getOffers_asSet(JSONArray offers2) {
		Set<String> offers = null;
		try {
			if (offers2 == null || offers2.length() == 0) {
				offers = null;
			}
			else {
				offers = new HashSet<String>();
				for (int i = 0; i < offers2.length(); i++) {
					offers.add(offers2.getString(i));
				}
			}
		} catch (JSONException e) {
			log.error("", e);
		}
		return offers;
	}

	public ShippingOptionsDTO getShippingOptions_asShippingOptions(JSONArray shippingOptions2) {
		ShippingOptionsDTO shippingOptions = null;
		try {
			if (shippingOptions2 == null) {
				shippingOptions = null;
			}
			else {
				shippingOptions = new ShippingOptionsDTO();
				shippingOptions.setEstimatedDelivery(shippingOptions2.getJSONObject(0).getDouble("estimatedDelivery"));
				shippingOptions.setDeliveryTimeUnits(shippingOptions2.getJSONObject(0).getString("deliveryTimeUnits"));
				shippingOptions.setShippingType(shippingOptions2.getJSONObject(0).getString("shippingType"));
			}

		} catch (JSONException e) {
			log.error("", e);
		}
		return shippingOptions;
	}
	
	public boolean isMatch(String categoryName, String category) {
		if (StringUtils.isBlank(categoryName) || StringUtils.isBlank(category)) {
			return false;
		}
		FlipkartCategoryEnum catEnum = FlipkartCategoryEnum.getCategory(categoryName);
		if (catEnum != null) {
			CategoryEnum[] categories = catEnum.getSubCategory();
			if (categories == null || categories.length == 0) {
				return false;
			}
			for (CategoryEnum cat : categories) {
				if (cat.name().equalsIgnoreCase(category)) {
					return true;
				}
			}
		}
		return false;
	}

}
