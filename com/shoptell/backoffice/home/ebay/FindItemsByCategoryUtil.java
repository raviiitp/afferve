/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.ebay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import EbayFindingService.Condition;
import EbayFindingService.FindItemsByCategoryResponse;
import EbayFindingService.GalleryInfoContainer;
import EbayFindingService.GalleryURL;
import EbayFindingService.ItemAttribute;
import EbayFindingService.SearchItem;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.util.HomeProductInfoUtil;

@Named
public class FindItemsByCategoryUtil {

	private final static Logger log = LoggerFactory.getLogger(FindItemsByCategoryUtil.class);

	@Inject
	private BatchRepository batchRepository;
	
	@Inject
	private HomeProductInfoUtil homeProductInfoUtil;
	
	public void __saveProductInfoUtil(FindItemsByCategoryResponse findItemsByCatRes, String parentName, long salesRank) {

		List<HomeProductInfoDTO> productInfoList = new LinkedList<HomeProductInfoDTO>();
		int totalItems = findItemsByCatRes.getSearchResult().getCount();
		for (int i = 0; i < totalItems; i++) {
			SearchItem currItem = findItemsByCatRes.getSearchResult().getItem().get(i);
			Condition condn = currItem.getCondition();
			
			if (condn == null ||condn.getConditionId() == null || condn.getConditionId() != 1000){
				continue;
				/*
				 * 1000New
				 * 2000Manufacturer refurbished
				 * 2500Seller refurbished
				 * 3000Used
				 */
			}
			HomeProductInfoDTO pInfo = new HomeProductInfoDTO();
			
			pInfo.setSalesRank(salesRank);

			pInfo.setHome(HomeEnum.EBAY.name());
			
			if (StringUtils.isNotBlank(parentName)){
				pInfo.setCategoryPaths(new HashSet<String>());
				pInfo.getCategoryPaths().add(parentName);
			}

			pInfo.setId(currItem.getItemId());
			if (currItem.getPrimaryCategory() != null) {
				pInfo.setCategoryId(currItem.getPrimaryCategory().getCategoryId());
				pInfo.setCategoryName(currItem.getPrimaryCategory().getCategoryName());
				pInfo.setSubCategoryName(pInfo.getCategoryName());
			}

			pInfo.setTitle(currItem.getTitle());
			pInfo.setOriginalTitle(pInfo.getTitle());
			pInfo.setSubTitle(currItem.getSubtitle());

			if (currItem.getSellingStatus() != null) {
				pInfo.setMrp(new Double(currItem.getSellingStatus().getCurrentPrice().getValue()));
				pInfo.setSellingPrice(new Double(currItem.getSellingStatus().getConvertedCurrentPrice().getValue()));
			}
			
			// Setting Images
			if (StringUtils.isNotBlank(currItem.getPictureURLLarge())){
				pInfo.setImageUrl(currItem.getPictureURLLarge());
			}
			else if (pInfo.getGalleryPlusPictureUrl() != null && pInfo.getGalleryPlusPictureUrl().size() > 0) {
				for (String img : pInfo.getGalleryPlusPictureUrl()){
					if (StringUtils.isNotBlank(img)){
						pInfo.setImageUrl(img);
						break;
					}
				}
			}
			else if (StringUtils.isNotBlank(currItem.getPictureURLSuperSize())){
				pInfo.setImageUrl(currItem.getPictureURLSuperSize());
			}
			else if (StringUtils.isNotBlank(currItem.getGalleryURL())){
				pInfo.setImageUrl(currItem.getGalleryURL());
			}
			else if (currItem.getGalleryInfoContainer() != null){
				GalleryInfoContainer info = currItem.getGalleryInfoContainer();
				List<GalleryURL> list = info.getGalleryURL();
				if (list != null && list.size() > 0){
					for (GalleryURL url : list){
						if (StringUtils.isNotBlank(url.getValue())){
							pInfo.setImageUrl(url.getValue());
							break;
						}
					}
				}
			}

			pInfo.setProductUrl(currItem.getViewItemURL());
			
			pInfo.setGalleryPlusPictureUrl(currItem.getGalleryPlusPictureURL());
			pInfo.setImageUrlLarge(currItem.getPictureURLLarge());
			pInfo.setImageUrlXL(currItem.getPictureURLSuperSize());
			pInfo.setImageUrlMedium(pInfo.getImageUrl());
			
			pInfo.setPaymentMethods(currItem.getPaymentMethod());
			if (currItem.getPaymentMethod() != null) {
				pInfo.setCodAvailable(currItem.getPaymentMethod().contains("COD"));
				pInfo.setEmiAvailable(currItem.getPaymentMethod().contains("PaisaPayEMI"));
			}
			if (currItem.getProductId() != null) {
				pInfo.setProductIdType(currItem.getProductId().getType());
				pInfo.setProductIdValue(currItem.getProductId().getValue());
			}
			pInfo.setAttributeMap(getAttribute_asMap(currItem.getAttribute()));
			pInfo.setAutoPay(currItem.isAutoPay());
			pInfo.setPostalCode(currItem.getPostalCode());
			pInfo.setLocation(currItem.getLocation());
			pInfo.setCountry(currItem.getCountry());
			if (currItem.getSellingStatus() != null) {
				pInfo.setSellingState(currItem.getSellingStatus().getSellingState());
				pInfo.setTimeLeft(currItem.getSellingStatus().getTimeLeft().toString());
			}
			if (currItem.getCondition() != null) {
				pInfo.setCondition(currItem.getCondition().getConditionDisplayName());
			}
			pInfo.setMultiVariationListing(currItem.isIsMultiVariationListing());
			if (currItem.isReturnsAccepted() != null) {
				pInfo.setReturnAccepted(currItem.isReturnsAccepted());
			}
			pInfo.setTopRatedListing(currItem.isTopRatedListing());
			if (currItem.getShippingInfo() != null) {
				pInfo.setShippingType(currItem.getShippingInfo().getShippingType());
				pInfo.setShippingServiceCost(new Double(currItem.getShippingInfo().getShippingServiceCost().getValue()).toString());
				pInfo.setShipToLocations(currItem.getShippingInfo().getShipToLocations());
			}
			if (currItem.getGalleryInfoContainer() != null) {
				pInfo.setGalleryInfoGalleryUrl(getGalleryInfoGalleryUrl_asList(currItem.getGalleryInfoContainer().getGalleryURL()));
			}
			
			pInfo.setSubCategoryName(getSubCategory(pInfo.getCategoryName()));
			
			/*if (!StringUtils.isEmpty(pInfo.getTitle())) {
				CompetitorFieldsDTO compField = homeProductInfoUtil.setCompetitorFields(pInfo);
				if(compField != null){
					pInfo.setCompetitorFields(compField);
					pInfo.setTitle(compField.getName());
					pInfo.setColor(compField.getProperties().get("COLOR"));
					pInfo.setSize(compField.getProperties().get("SIZE"));
				}
			}*/
			
			pInfo.setTags(homeProductInfoUtil.getTag_asSet(currItem.getTitle(), null, null));
			if (pInfo.getSellingPrice() > 0){
				pInfo.setInStock(true);
			}
			if (!CategoryEnum.SMARTPHONES.name().equalsIgnoreCase(pInfo.getSubCategoryName())){
				pInfo=null;
			}
			if (pInfo != null){
				productInfoList.add(pInfo);
			}
		}
		
		batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), productInfoList);
		
		//log.debug("List Size is {}", productInfoList.size());
		
		productInfoList.clear();
	}

	private String getSubCategory(String category) {
		if (StringUtils.isNotBlank(category)) {
			CategoryEnum subCategory = CategoryEnum.getCategory(category);
			if (subCategory != null) {
				return subCategory.name();
			}
		}
		return null;
	}

	private Map<String, String> getAttribute_asMap(List<ItemAttribute> attribute) {
		Map<String, String> attribute_asMap = null;
		if (attribute != null) {
			attribute_asMap = new HashMap<String, String>();
			for (int i = 0; i < attribute.size(); i++) {
				attribute_asMap.put(attribute.get(i).getName(), attribute.get(i).getValue());
			}
		}
		return attribute_asMap;
	}

	private List<String> getGalleryInfoGalleryUrl_asList(List<GalleryURL> galleryUrl) {
		List<String> galleryUrl_asList = null;
		if (galleryUrl != null) {
			galleryUrl_asList = new ArrayList<String>();
			for (int i = 0; i < galleryUrl.size(); i++) {
				galleryUrl_asList.add(galleryUrl.get(i).getValue());
			}
		}
		return galleryUrl_asList;
	}
}
