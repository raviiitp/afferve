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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import EbayFindingService.AckValue;
import EbayFindingService.FindItemsAdvancedRequest;
import EbayFindingService.FindItemsAdvancedResponse;
import EbayFindingService.PaginationInput;
import EbayFindingService.SortOrderType;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.CategoryNodeRepository;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;

@Named(value = "FindItemsAdvanced")
public class FindItemsAdvanced {

	private static final String findItemsAdvancedCallName = "findItemsAdvanced";

	@Inject
	private FindItemsAdvancedUtil findItemsAdvancedUtil;
	@Inject
	private EbayFindingApi ebayFindingApi;
	@Inject
	private CategoryNodeRepository categoryNodeRepository;

	private FindItemsAdvancedRequest findItemsAdvancedReq;

	private List<CategoryNodeDTO> categoryNodeList;

	@PostConstruct
	public void start() {
		ebayFindingApi.initEbayFindingServiceApi(findItemsAdvancedCallName);
		findItemsAdvancedReq = new FindItemsAdvancedRequest();
		findItemsAdvancedReq.setSortOrder(SortOrderType.BEST_MATCH);
	}

	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	private void postprocess() {
		// TODO Auto-generated method stub
	}

	private void execute() {
		for (CategoryNodeDTO category : categoryNodeList) {
			findItemsAdvanced(category.getCategoryId(), "");
		}
	}

	private void preprocess() {
		categoryNodeList = categoryNodeRepository.findAllLeaves(HomeEnum.EBAY.name());
	}

	public void findItemsAdvanced(String catID, String keyword) {

		int currentPageNum = 1, totalPages = 1;

		FindItemsAdvancedResponse findItemsAdvancedRes = null;

		PaginationInput paginationInput = new PaginationInput();
		paginationInput.setEntriesPerPage(100);
		findItemsAdvancedReq.getCategoryId().add(catID);

		for (currentPageNum = 1; currentPageNum <= totalPages; ++currentPageNum) {

			paginationInput.setPageNumber(currentPageNum);
			findItemsAdvancedReq.setPaginationInput(paginationInput);
			//log.debug("calling page number: " + currentPageNum);
			findItemsAdvancedRes = ebayFindingApi.callFindItemsAdvanced(findItemsAdvancedReq);

			if (findItemsAdvancedRes.getAck().equals(AckValue.SUCCESS)) {
				if (currentPageNum == 1) {
					totalPages = findItemsAdvancedRes.getPaginationOutput().getTotalPages();
				}
				findItemsAdvancedUtil.__saveProductInfoUtil(findItemsAdvancedRes);
			}
			else {
				//log.debug("findItemsByCatRes ACK is " + findItemsAdvancedRes.getAck() + " for page number " + currentPageNum);
				// break;
			}
		}
	}
}
