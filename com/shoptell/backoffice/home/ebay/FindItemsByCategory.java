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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import EbayFindingService.AckValue;
import EbayFindingService.FindItemsByCategoryRequest;
import EbayFindingService.FindItemsByCategoryResponse;
import EbayFindingService.OutputSelectorType;
import EbayFindingService.PaginationInput;
import EbayFindingService.SortOrderType;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;

@Named(value = "FindItemsByCategory")
public class FindItemsByCategory extends ProductInfo {

	private static final Logger log = LoggerFactory.getLogger(FindItemsByCategory.class);

	private static final String findItemsByCategoryCallName = "findItemsByCategory";

	@Inject
	private FindItemsByCategoryUtil findItemsByCategoryUtil;
	@Inject
	private EbayCategoryInfo categoryInfo;
	@Inject
	private EbayFindingApi ebayFindingApi;

	private FindItemsByCategoryRequest findItemsByCatReq;

	private List<CategoryNodeDTO> categoryNodeList;

	private Map<String, String> categoryNameMap = new HashMap<String, String>();

	private int itemRank;

	@PostConstruct
	public void start() {
		home = HomeEnum.EBAY;
		ebayFindingApi.initEbayFindingServiceApi(findItemsByCategoryCallName);
		findItemsByCatReq = new FindItemsByCategoryRequest();
		findItemsByCatReq.setSortOrder(SortOrderType.BEST_MATCH);
		findItemsByCatReq.getOutputSelector().add(OutputSelectorType.PICTURE_URL_LARGE);
	}

	@Async
	// @Scheduled(cron = "${ebay.cron.exp}")
	public void init() {
		preprocess();
		try {
			execute();
		} catch (InterruptedException e) {
			log.error("INIT EXCEPTION", e);
		}
		postprocess();
	}

	@Override
	protected void postprocess() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void execute() throws InterruptedException {
		resetItemRank();
		for (CategoryNodeDTO category : categoryNodeList) {
			String parentName = null;
			if (categoryNameMap.containsKey(category.getParentId())) {
				parentName = categoryNameMap.get(category.getParentId());
			}
			else {
				parentName = categoryInfo.getNameFromId(category.getParentId());
				categoryNameMap.put(category.getParentId(), parentName);
			}
			findItemsByCategory(category.getCategoryId(), parentName);
		}
		// findItemsByCategory("15032"); // Mobile Phones
		// findItemsByCategory("51071");// Pen Drives 51071
	}

	@Override
	protected void preprocess() {
		// categoryNodeList = (List<CategoryNode>)
		// categoryNodeRepository.findAllLeaves(HomeEnum.EBAY.name());
		categoryNodeList = categoryInfo.findLeaf();
		// categoryNodeList =
		// categoryNodeRepository.getLeaf("174987","176253","176271"); //men
		// shoes
	}

	public void findItemsByCategory(String catID, String parentName) throws InterruptedException {
		int currentPageNum = 1, totalPages = 1;

		FindItemsByCategoryResponse findItemsByCatRes = null;

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(100);
		findItemsByCatReq.getCategoryId().add(catID);

		for (currentPageNum = 1; currentPageNum <= totalPages; ++currentPageNum) {
			paginationInput.setPageNumber(currentPageNum);
			findItemsByCatReq.setPaginationInput(paginationInput);
			// log.debug("calling page number: " + currentPageNum);

			findItemsByCatRes = ebayFindingApi.callFindItemsByCategory(findItemsByCatReq);

			if (findItemsByCatRes.getAck().equals(AckValue.SUCCESS)) {
				if (currentPageNum == 1) {
					totalPages = findItemsByCatRes.getPaginationOutput().getTotalPages();
				}
				findItemsByCategoryUtil.__saveProductInfoUtil(findItemsByCatRes, parentName, getIncrItemRank());
			}
			else {
				// log.debug("findItemsByCatRes ACK is " +
				// findItemsByCatRes.getAck() + " for page number " +
				// currentPageNum);
				// TODO Audit Log into DB
				// break;
			}
			// Stopping running Thread
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("KILL THREAD");
			}
		}
	}

	/**
	 * @return the itemRank
	 */
	public int getItemRank() {
		return itemRank;
	}

	/**
	 * @param itemRank
	 *            the itemRank to set
	 */
	public void setItemRank(int itemRank) {
		this.itemRank = itemRank;
	}

	public void resetItemRank() {
		this.itemRank = 0;
	}

	public int getIncrItemRank() {
		return ++itemRank;
	}

	@Override
	protected void priceUpdater() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
}
