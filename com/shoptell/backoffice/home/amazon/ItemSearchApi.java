/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import static com.shoptell.backoffice.BackofficeConstants.AMAZON_SORT_VALUES;
import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import AWSECommerce.ItemSearchRequest;
import AWSECommerce.Items;
import AWSECommerce.OfferSummary;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.home.ProductInfo;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;

@Named(value = "ItemSearchApi")
public class ItemSearchApi extends ProductInfo {
	private static final Logger log = LoggerFactory.getLogger(ItemSearchApi.class);

	@Inject
	private AmazonApi amazonApi;
	@Inject
	private ItemSearchUtil itemSearchUtil;
	@Inject
	private AmazonCategoryInfo categoryInfo;

	private ItemSearchRequest itemSearchRequest;
	private String sort;
	private List<CategoryNodeDTO> categoryNodeList;

	private Map<String, String> categoryNameMap = new HashMap<String, String>();
	
	@PostConstruct
	public void start(){
		home = HomeEnum.AMAZON;
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
	protected void preprocess() {
		log.info("preprocess() Enter");
		categoryNodeList = categoryInfo.findLeaf();
		log.info("preprocess() Exit");
	}

	@Override
	protected void postprocess() {}

	@Override
	protected void execute() throws InterruptedException {
		log.info("execute() Enter");
		for (CategoryNodeDTO category : categoryNodeList) {

			String parentName = null;
			if (categoryNameMap.containsKey(category.getParentId())) {
				parentName = categoryNameMap.get(category.getParentId());
			}
			else {
				parentName = categoryInfo.getNameFromId(category.getParentId());
				categoryNameMap.put(category.getParentId(), parentName);
			}

			itemSearchRequest = new ItemSearchRequest();

			itemSearchRequest.setBrowseNode(category.getCategoryId());
			itemSearchRequest.setSearchIndex(category.getSearchIndex());

			List<HomeProductInfoDTO> list = new LinkedList<HomeProductInfoDTO>();
			List<ItemSearchRequest> requests = new LinkedList<ItemSearchRequest>();
			List<String> sortValues = null;
			if (!StringUtils.isEmpty(sort)) {
				sortValues = Arrays.asList(sort.split(","));
			}
			else {
				sortValues = Arrays.asList(AMAZON_SORT_VALUES);
			}

			Set<String> set = new HashSet<String>();
			set.add(category.getCategoryName());
			if ("Smartphones".equalsIgnoreCase(category.getCategoryName())) {
				set.addAll(BackofficeUtil.mobileBrandSet);
			}
			for (String brand : set) {
				BigInteger totalPages = BigInteger.TEN.add(BigInteger.ONE);
				boolean totalPagesSet = false;
				for (String val : sortValues) {
					// Loop over all sort values
					itemSearchRequest.setSort(val);
					BigInteger currentPageNum = BigInteger.ONE;

					for (int index = 0; index < 5 && currentPageNum.compareTo(totalPages) < 0; index++) {
						// 10 pages can be retrieved in the sets of 2s.
						requests.clear();
						for (int i = 0; i < 2; i++) {
							ItemSearchRequest req = getClone(itemSearchRequest);
							req.setItemPage(currentPageNum);
							if (!category.getCategoryName().equalsIgnoreCase(brand)) {
								req.setKeywords(brand);
							}
							requests.add(req);
							currentPageNum = currentPageNum.add(BigInteger.valueOf(1L));
							if (currentPageNum.compareTo(totalPages) == 0) {
								break;
							}
						}

						List<Items> response = null;
						HomeProductInfoDTO prod = null;
						try {
							// Stopping running Thread
							if (Thread.currentThread().isInterrupted()) {
								throw new InterruptedException("KILL THREAD");
							}
							response = amazonApi.itemSearchApiCall(requests);
							if (response != null) {
								for (Items items : response) {
									if (!totalPagesSet) {
										totalPages = items.getTotalPages();
										totalPagesSet = true;
									}
									for (AWSECommerce.Item item : items.getItem()) {
										prod = itemSearchUtil.processItem(item, parentName, false, category.getCategoryName());
										if (prod != null) {
											list.add(prod);
											if (list.size() > BATCHSIZE) {
												batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), list);
												list.clear();
											}
										}
									}
								}
							}
						} catch (InterruptedException e) {
							throw new InterruptedException("KILL THREAD");
						}
						catch (Exception e) {
							log.error("EXECUTION ERROR",e);
							msgLog.addError(e);
						}
						finally {
							batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), list);
							list.clear();
						}
					}
				}
				batchRepository.batchSaveAndUpdate(TableEnum.home_product_info.name(), list);
				list.clear();
			}
		}
		log.info("execute() Exit");
	}

	private ItemSearchRequest getClone(ItemSearchRequest request) {
		//log.info("getClone() Enter");
		ItemSearchRequest req = new ItemSearchRequest();
		req.setBrowseNode(request.getBrowseNode());
		req.setSearchIndex(request.getSearchIndex());
		req.setSort(request.getSort());
		req.getResponseGroup().addAll(Arrays.asList(new String[] { "Large", "OfferFull", "Variations", "SalesRank" }));
		//log.info("getClone() Exit");
		return req;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	protected void priceUpdater() throws InterruptedException {
		log.info("priceUpdater() Enter");
		for (CategoryNodeDTO category : categoryNodeList) {
			itemSearchRequest = new ItemSearchRequest();

			itemSearchRequest.setBrowseNode(category.getCategoryId());
			itemSearchRequest.setSearchIndex(category.getSearchIndex());

			List<ItemSearchRequest> requests = new LinkedList<ItemSearchRequest>();
			List<String> sortValues = null;
			if (!StringUtils.isEmpty(sort)) {
				sortValues = Arrays.asList(sort.split(","));
			}
			else {
				sortValues = Arrays.asList(AMAZON_SORT_VALUES);
			}

			Set<String> set = new HashSet<String>();
			set.add(category.getCategoryName());
			if ("Smartphones".equalsIgnoreCase(category.getCategoryName())) {
				set.addAll(BackofficeUtil.mobileBrandSet);
			}
			for (String brand : set) {
				BigInteger totalPages = BigInteger.TEN.add(BigInteger.ONE);
				boolean totalPagesSet = false;
				for (String val : sortValues) {
					// Loop over all sort values
					itemSearchRequest.setSort(val);
					BigInteger currentPageNum = BigInteger.ONE;

					for (int index = 0; index < 5 && currentPageNum.compareTo(totalPages) < 0; index++) {
						// 10 pages can be retrieved in the sets of 2s.
						requests.clear();
						for (int i = 0; i < 2; i++) {
							ItemSearchRequest req = getClone(itemSearchRequest);
							req.setItemPage(currentPageNum);
							if (!category.getCategoryName().equalsIgnoreCase(brand)) {
								req.setKeywords(brand);
							}
							requests.add(req);
							currentPageNum = currentPageNum.add(BigInteger.valueOf(1L));
							if (currentPageNum.compareTo(totalPages) == 0) {
								break;
							}
						}

						List<Items> response = null;
						try {
							// Stopping running Thread
							if (Thread.currentThread().isInterrupted()) {
								throw new InterruptedException("KILL THREAD");
							}
							response = amazonApi.itemSearchApiCall(requests);
							if (response != null) {
								for (Items items : response) {
									if (!totalPagesSet) {
										totalPages = items.getTotalPages();
										totalPagesSet = true;
									}
									for (AWSECommerce.Item responsedItem : items.getItem()) {
										if (responsedItem != null) {
											String id = responsedItem.getASIN();
											double sellingPrice = 0;
											if (responsedItem.getOfferSummary() != null) {
												OfferSummary offerSummary = responsedItem.getOfferSummary();
												if (offerSummary.getLowestNewPrice() != null) {
													// TODO if does not work
													// properly refer
													// itemSearchUtil
													sellingPrice = itemSearchUtil.formatPrice(offerSummary.getLowestNewPrice().getFormattedPrice());
												}
											}
											String subCategory = itemSearchUtil.getSubCategory(category.getCategoryName(),null);
											if (StringUtils.isBlank(subCategory)) {
												continue;
											}
											if (StringUtils.isNotBlank(id) && sellingPrice > 0) {
												//batchRepository.updatePrice(home, subCategory, id, sellingPrice, sellingPrice > 0);
											}
										}
									}
								}
							}
						} catch (InterruptedException e) {
							throw new InterruptedException("KILL THREAD");
						} catch (Exception e) {
							log.error("EXECUTION ERROR", e);
							msgLog.addError(e);
						}
					}
				}
			}
		}
		log.info("priceUpdater() Exit");
	}
}
