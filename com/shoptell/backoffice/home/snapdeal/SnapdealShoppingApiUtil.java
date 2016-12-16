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

import static com.shoptell.backoffice.BackofficeConstants.COLOR_REGEX;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * @author abhishekagarwal
 *
 */

@Named
public class SnapdealShoppingApiUtil implements Snapdeal{
	
	private static final Logger log = LoggerFactory.getLogger(SnapdealShoppingApiUtil.class);
	
	@Inject
	private HomeProductInfoUtil homeProductInfoUtil;
	
	@Inject
	private MessageLogUtil msgLog;
	
	/***
	 *
	 * @param JSONArray
	 *            productArray.getJSONObject(i)
	 * @return an instance of productInfo
	 * @throws AffiliateAPIException
	 * @throws JSONException
	 */
	public HomeProductInfoDTO ProcessJSONObject(JSONObject element, String categoryId, String categoryName, boolean isUpdate) {
		try {
			HomeProductInfoDTO pInfo = new HomeProductInfoDTO();

			pInfo.setSalesRank(0); // TODO Set Rank When Available

			pInfo.setHome(HomeEnum.SNAPDEAL.name());
			pInfo.setCategoryId(categoryId);
			pInfo.setCategoryName(categoryName);
			pInfo.setSubCategoryName(element.getString("subCategoryName"));
			pInfo.setId(String.valueOf(element.getLong("id")));

			/*
			 * Set<String> path = new HashSet<String>();
			 * path.add(element.getString("subCategoryName"));
			 * pInfo.setCategoryPaths(path);
			 */

			pInfo.setTitle(element.getString("title"));
			if (StringUtils.isNotBlank(pInfo.getTitle())) {
				pInfo.setTitle(pInfo.getTitle().toUpperCase());
			}
			pInfo.setOriginalTitle(pInfo.getTitle());
			pInfo.setDescription(element.getString("description"));
			if (StringUtils.isNotBlank(pInfo.getDescription())) {
				pInfo.setDescription(pInfo.getDescription().toUpperCase());
			}

			pInfo.setImageUrl(element.getString("imageLink"));

			pInfo.setMrp(element.getInt("mrp"));
			pInfo.setSellingPrice(element.getInt("offerPrice"));
			pInfo.setProductUrl(element.getString("link"));
			pInfo.setProductBrand(element.getString("brand"));
			String available = element.getString("availability");
			pInfo.setInStock("in stock".equalsIgnoreCase(available));

			if (StringUtils.isNotBlank(pInfo.getSubCategoryName())) {
				pInfo.setType(pInfo.getSubCategoryName().toUpperCase());
			}

			// pinfo.setOffset(productArray.getJSONObject(i).getString("offset"));
			// TODO check offset value
			pInfo.setOffset(null);
			// TODO set competitor fields
			/*
			 * if (!StringUtils.isEmpty(pInfo.getTitle())) { CompetitorFieldsDTO
			 * compField = homeProductInfoUtil.setCompetitorFields(pInfo); if
			 * (compField != null) { pInfo.setCompetitorFields(compField);
			 * pInfo.setTitle(compField.getName());
			 * pInfo.setColor(compField.getProperties().get("COLOR"));
			 * pInfo.setSize(compField.getProperties().get("SIZE")); } }
			 */

			pInfo.setSubCategoryName(getSubCategory(categoryName, pInfo.getSubCategoryName()));
			if (StringUtils.isBlank(pInfo.getSubCategoryName())) {
				return null;
			}

			// comment this "if case" in production
			/*
			 * if(!StringUtils.equalsIgnoreCase(pInfo.getSubCategoryName(),
			 * CategoryEnum.MONITORS.name())){ return null; }
			 */

			if (!isUpdate) {
				pInfo.setTags(homeProductInfoUtil.getTag_asSet(element.getString("title"), null, element.optString("brand", null)));

				if (StringUtils.isEmpty(pInfo.getSize())) {
					String title = pInfo.getTitle();
					title = title.replaceAll("\\sGB", "GB");
					Pattern pattern = Pattern.compile("\\d+GB", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(title);
					while (matcher.find()) {
						if (!StringUtils.isEmpty(matcher.group(0))) {
							String size = matcher.group(0).trim();
							pInfo.setSize(size);
							// TODO size unit
							break;
						}
					}
				}

				if (StringUtils.isEmpty(pInfo.getColor())) {
					Pattern pattern = Pattern.compile(COLOR_REGEX, Pattern.CASE_INSENSITIVE);
					String title = pInfo.getTitle();
					Matcher matcher = pattern.matcher(title);
					while (matcher.find()) {
						if (!StringUtils.isEmpty(matcher.group(0))) {
							String color = matcher.group(0).trim();
							pInfo.setColor(color);
							break;
						}
					}
				}
			}
			return pInfo;
		} catch (Exception e) {
			msgLog.addError(e);
		}
		return null;
	}

	public String getSubCategory(String categoryName, String category) {
		//System.out.println(category);
		if (StringUtils.isNotBlank(category)) {
			CategoryEnum subCategory = CategoryEnum.getCategory(category);
			if (subCategory != null) {
				//System.out.println(subCategory.name());
				if (subCategory.name().equalsIgnoreCase(categoryName)){
					return subCategory.name();
				}
				if (isMatch(categoryName, subCategory.name())){
					return subCategory.name();
				}
			}
		}
		return null;
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
				for (int i = 0; i < offers2.getJSONArray(0).length(); i++) {
					offers.add(offers2.getJSONArray(0).getJSONObject(i).getString("title"));
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
		SnapdealCategoryEnum catEnum = SnapdealCategoryEnum.getCategory(categoryName);
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
