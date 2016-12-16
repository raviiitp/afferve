/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.util;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.processor.HomeDataProcessor;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.frontoffice.service.Service;

@Named(value = "ReviewedProductInfoDTOUtil")
public class ReviewedProductInfoDTOUtil extends Service {
	private static final Logger log = LoggerFactory.getLogger(ReviewedProductInfoDTOUtil.class);
	@Inject
	private BatchRepository repo;
	@Inject
	private ProcessLogUtil processUtil;
	@Inject
	private HomeDataProcessor dataProcessor;
	@Inject
	private SelectQuery selectQuery;

	private boolean reviewAll = false;

	public void checkout(List<ProcessLog> list, HomeEnum homenum) throws InterruptedException {
		if (homenum.equals(HomeEnum.AMAZON) || homenum.equals(HomeEnum.FLIPKART) || homenum.equals(HomeEnum.SNAPDEAL)) {
			log.info("checkout() Enter");
			process = processUtil.start(homenum.name(), ProcessLogJobEnum.REVIEW.name());
			list.add(process);
			execute(homenum.name());
			list.remove(process);
			processUtil.end(process);
			log.info("checkout() Exit");
		}
	}

	@Async
	public void init() throws InterruptedException {
		log.info("init() Enter");
		for (HomeEnum homenum : HomeEnum.values()) {
			if (homenum.equals(HomeEnum.AMAZON)|| homenum.equals(HomeEnum.FLIPKART) || homenum.equals(HomeEnum.SNAPDEAL)) {
				execute(homenum.name());
			}
		}
		log.info("init() Exit");
	}

	@Async
	public void reviewAll() throws InterruptedException {
		reviewAllAmazon();
		reviewAllFlipkart();
		reviewAllSnapdeal();
	}

	public void reviewAllAmazon() throws InterruptedException {
		reviewAll = true;
		forHome(HomeEnum.AMAZON.name());
		reviewAll = false;
	}

	public void reviewAllSnapdeal() throws InterruptedException {
		reviewAll = true;
		forHome(HomeEnum.SNAPDEAL.name());
		reviewAll = false;
	}

	public void reviewAllFlipkart() throws InterruptedException {
		reviewAll = true;
		forHome(HomeEnum.FLIPKART.name());
		reviewAll = false;
	}

	private void execute(String home) throws InterruptedException {
		log.info("execute() Enter");
		for (CategoryEnum category : CategoryEnum.values()) {
			if (CategoryEnum.ALL.equals(category))
				continue;

			try {
				String subCategoryName = category.name();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ismerged", false);
				map.put("subCategoryName", subCategoryName);
				if (StringUtils.isNotBlank(home)) {
					map.put("home", home);
				}
				while (true) {
					log.info("execute() - home:{} subcategory:{}", home, subCategoryName);
					ResultSet rs = selectQuery.selectAll(TableEnum.home_product_info, map, FETCHSIZE);
					if (rs == null || rs.isExhausted()){
						break;
					}
					
					Iterator<HomeProductInfoDTO> rows = QueryMapper.homeProductInfoDTO().map(rs).iterator();
					List<HomeProductInfoDTO> selected = new LinkedList<HomeProductInfoDTO>();
					BatchStatement batch = new BatchStatement();
					while (rows.hasNext()) {
						HomeProductInfoDTO prod = rows.next();
						String title = prod.getTitle();
						if (StringUtils.isNotBlank(title) && !StringUtils.equalsIgnoreCase(title, "null") && prod.isInStock()) {
							selected.add(prod);
						}
						batch.add(QueryBuilder.update(keyspace, TableEnum.home_product_info.name()).with(QueryBuilder.set("ismerged", true))
								.where(QueryBuilder.eq("id", prod.getId())).and(QueryBuilder.eq("subcategoryname", prod.getSubCategoryName()))
								.and(QueryBuilder.eq("home", prod.getHome())));

						if (batch.size() > BATCHSIZE) {
							if (selected.size() > 0) {
								reviewProduct(selected);
								selected.clear();
							}
							session.execute(batch);
							batch.clear();
						}
					}
					if (selected.size() > 0) {
						reviewProduct(selected);
						selected.clear();
					}
					if (batch.size() > 0) {
						session.execute(batch);
						batch.clear();
					}
				}
			} catch (Exception e) {
				msgLog.addError(e);
				log.error("", e);
			}
		}

		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("KILL THREAD");
		}
		log.info("execute() Exit");
	}

	@SuppressWarnings("unchecked")
	public void forHome(String home) throws InterruptedException {
		log.info("forHome() Enter");
		for (CategoryEnum category : CategoryEnum.values()) {
			if (CategoryEnum.ALL.equals(category))
				continue;
			String subCategoryName = category.name();
			while (true) {
				Iterator<HomeProductInfoDTO> products = (Iterator<HomeProductInfoDTO>) repo.selectBySubCategoryName(TableEnum.home_product_info.name(),
						HomeProductInfoDTO.class, home, subCategoryName, reviewAll);
				if (products == null || !products.hasNext()) {
					break;
				} else {
					List<HomeProductInfoDTO> allProducts = new LinkedList<HomeProductInfoDTO>();
					List<HomeProductInfoDTO> selected = new LinkedList<HomeProductInfoDTO>();
					while (products.hasNext()) {
						HomeProductInfoDTO prod = products.next();
						String title = prod.getTitle();
						if (StringUtils.isNotBlank(title) && !StringUtils.equalsIgnoreCase(title, "null") && prod.isInStock()) {
							selected.add(prod);
						}
						prod.setMerged(true);
						allProducts.add(prod);
						if (allProducts.size() > BATCHSIZE){
							if (selected.size() > 0) {
								reviewProduct(selected);
								selected.clear();
							}
							repo.batchSave(allProducts);
							allProducts.clear();
						}
					}
					if (selected.size() > 0) {
						reviewProduct(selected);
					}
					repo.batchSave(allProducts);
				}
				if (reviewAll){
					//TODO Enhance our system to properly do looping all elements
					break;
				}
			}
		}
		// Stopping running Thread
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("KILL THREAD");
		}
		log.info("forHome() Exit");
	}

	public void reviewProduct(List<HomeProductInfoDTO> list) {
		if (list != null && list.size() > 0) {
			List<ReviewedProductInfoDTO> items = new LinkedList<ReviewedProductInfoDTO>();
			for (HomeProductInfoDTO prod : list) {
				ReviewedProductInfoDTO dto = dataProcessor.process((HomeProductInfoDTO)SerializationUtils.clone(prod));
				if (dto != null) {
					dto.setTags(getTags(dto));
					items.add(dto);
				}
			}
			if (items.size() > 0) {
				repo.batchSave(items);
			}
		}
	}

	/*
	 * create tags from title, color and productBrand.
	 */
	private Set<String> getTags(ReviewedProductInfoDTO dto) {
		String title = dto.getTitle();
		String productBrand = dto.getProductBrand();

		Set<String> tag_asSet = null;
		String regex = "[\\[\\](),]";

		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll(regex, "");
			tag_asSet = new HashSet<String>();
			String[] tokens = title.split(" ");
			for (String token : tokens) {
				if (!token.isEmpty()) {
					tag_asSet.add(token.toLowerCase());
				}
			}
		}
		if (StringUtils.isNotBlank(productBrand) && !productBrand.equals("null")) {
			productBrand = productBrand.replaceAll(regex, "");
			if (tag_asSet == null) {
				tag_asSet = new HashSet<String>();
				tag_asSet.add(productBrand.toLowerCase());
			} else if (!tag_asSet.contains(productBrand)) {
				tag_asSet.add(productBrand.toLowerCase());
			}
		}
		return tag_asSet;
	}

	/**
	 * @return the reviewAll
	 */
	public boolean isReviewAll() {
		return reviewAll;
	}

	/**
	 * @param reviewAll
	 *            the reviewAll to set
	 */
	public void setReviewAll(boolean reviewAll) {
		this.reviewAll = reviewAll;
	}
}
