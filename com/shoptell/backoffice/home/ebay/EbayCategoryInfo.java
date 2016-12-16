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

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import EbayShoppingService.CategoryType;
import EbayShoppingService.GetCategoryInfoRequestType;
import EbayShoppingService.GetCategoryInfoResponseType;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.home.CategoryInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;

@Named (value = "EbayCategoryInfo")
public class EbayCategoryInfo extends CategoryInfo{

	private static final Logger log = LoggerFactory.getLogger(EbayCategoryInfo.class);

	private static final String GetCategoryInfoCALLNAME = "GetCategoryInfo";

	@Inject
	private EbayShoppingAPI ebayShoppingApi;
	
	private GetCategoryInfoRequestType getCatInfoReq;
	private List<CategoryNodeDTO> categoryNodeList;

	@PostConstruct
	public void Init() {
		home = HomeEnum.EBAY;
		ebayShoppingApi.initEbayShoppingServiceApi(GetCategoryInfoCALLNAME);
		getCatInfoReq = new GetCategoryInfoRequestType();
		getCatInfoReq.setIncludeSelector("ChildCategories");
	}

	@Async
	//@Scheduled(fixedDelayString="${ebay.category.delay}")
	public void init(){
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
		getCategoryInfo("-1","root", 0);
	}

	@Override
	protected void preprocess() {
		categoryNodeList = new LinkedList<CategoryNodeDTO>();
	}

	public void getCategoryInfo(String catID, String index, int numOfTry) {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			log.error("",e1);
		}
		getCatInfoReq.setCategoryID(catID);
		GetCategoryInfoResponseType getCatInfoRes = null;
		try{
			getCatInfoRes = ebayShoppingApi.callGetCategoryInfo(getCatInfoReq);
		}
		catch (Exception e){
			msgLog.addError(e);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ep) {
				log.error("",ep);
			}
			try{
				getCatInfoRes = ebayShoppingApi.callGetCategoryInfo(getCatInfoReq);
			}
			catch (Exception ex){
				msgLog.addError(ex);
				log.error("EXECUTION ERROR",ex);
			}
		}
		
		if (getCatInfoRes == null) return;
		
		int categoryItemsCount = getCatInfoRes.getCategoryCount();
		for (int i = 1; i < categoryItemsCount; i++) {
			
			//Stopping running Thread
			if (Thread.currentThread().isInterrupted()){
				return;
			}
			
			CategoryType category = getCatInfoRes.getCategoryArray().getCategory().get(i);
			if (category.getCategoryLevel() == 1){
				index = category.getCategoryName();
			}
			CategoryNodeDTO cat = new CategoryNodeDTO(category.getCategoryID(), category.getCategoryName(), 
					category.getCategoryParentID(), index, category.isLeafCategory(), 
					category.getCategoryParentID().equalsIgnoreCase("0")?true:false);
			cat.setHome(HomeEnum.EBAY.name());
			categoryNodeList.add(cat);
			
			if(categoryNodeList.size() > BATCHSIZE){
				batchRepository.batchSave(categoryNodeList);
				categoryNodeList.clear();
			}
			
			if (!category.isLeafCategory()) {
				getCategoryInfo(category.getCategoryID(), index, 0);
			} 
		}
	}

	public String getNameFromId(String parentId) {
		return categoryNodeRepository.getNameFromId(parentId);
	}

	public List<CategoryNodeDTO> findLeaf() {
		return categoryNodeRepository.findLeaf(HomeEnum.EBAY.name(), "Mobile Phones");
	}
}
