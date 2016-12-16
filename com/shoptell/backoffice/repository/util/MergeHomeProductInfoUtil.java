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

import static com.shoptell.backoffice.BackofficeConstants.HOMES;
import static com.shoptell.backoffice.BackofficeConstants.SCORE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.MergeProductPropertiesDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.score.MatchScore;
import com.shoptell.config.elasticsearch.ElasticSearchUtil;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;
import com.shoptell.db.processlog.ProcessLogUtil;

/**
 * @author abhishekagarwal
 *
 *         Algo: Read all competitorFields rows for which now() - createdOn <
 *         5hrs from home_product_info (order by categoryName). Store the list
 *         in competitorFieldsList. Each row's competitorFields has a barcode
 *         which uniquely identifies all competitive characteristics for that
 *         row For each row's barcode, compare it with barcode of each row in
 *         mergedProductInfoList mergedProductInfoList is list per categoryName
 *         wise update list/DB accordingly based on score
 */

@Named(value = "MergeHomeProductInfoUtil")
public class MergeHomeProductInfoUtil {
	@Inject
	private ProcessLogUtil processUtil;
	@Inject
	private ElasticSearchUtil esRepository;
	@Inject
	private BatchRepository batchRepository;
	@Inject
	private MatchScore matchScore;
	@Inject
	private SelectQuery selectQuery;
	private ProcessLog process;
	@Inject
	private MessageLogUtil msgLog;

	private static final Logger log = LoggerFactory.getLogger(MergeHomeProductInfoUtil.class);

	/*
	 * for each CategoryNameEnum e get all competitorFields that are added in
	 * last BackofficeConstants.TIME_DIFFERENCE hrs.
	 */
	@Async
	public void init() throws InterruptedException {
		log.info("init() Enter");
		/*
		 * batchRepository.flipIsMerge(dataTable, dataTableClass, null, true);
		 * log.info("flipIsMerge done");
		 */
		for (String home : HOMES) {
			forHome(home, false);
		}
		esRepository.save();
		log.info("init() Exit");
	}

	public void checkout(List<ProcessLog> list, HomeEnum homeE) throws InterruptedException {
		process = processUtil.start(homeE.name(), ProcessLogJobEnum.MERGE.name());
		list.add(process);
		forHome(homeE.name(), false);
		list.remove(process);
		processUtil.end(process);
	}

	private void forHome(String home, boolean allReview) throws InterruptedException {
		log.info("forHome() Enter");
		for (CategoryEnum category : CategoryEnum.values()) {
			if (CategoryEnum.ALL.equals(category))
				continue;
			preprocess(home, category.name(), allReview);

			// Stopping running Thread
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("KILL THREAD");
			}
		}
		log.info("forHome() Exit");
	}

	@Async
	public void mergeAll() {
		log.info("mergeAll() Enter");
		try {
			for (String home : HOMES) {
				forHome(home, true);
			}
		} catch (InterruptedException e) {
			log.error("", e);
		}
		log.info("mergeAll() Exit");
	}

	/**
	 * get List<CompetitorFields> in size of
	 * 
	 * @param allReview
	 */
	private void preprocess(String home, String subCategoryName, boolean allReview) {
		while (true) {
			Iterator<ReviewedProductInfoDTO> rows = batchRepository.getDataForReview(home, subCategoryName, allReview);
			if (rows != null && rows.hasNext()) {
				while (rows.hasNext()) {
					ReviewedProductInfoDTO field = rows.next();
					execute(field);
				}
			}
			else {
				break;
			}
		}
	}

	private void execute(ReviewedProductInfoDTO field) {
		if (field != null) {
			if (StringUtils.isBlank(field.getProductBrand()) || StringUtils.isBlank(field.getSubCategoryName())) {
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("subCategoryName", field.getSubCategoryName());
			map.put("productbrand", field.getProductBrand());

			ResultSet rs = selectQuery.selectAllWithFiltering(TableEnum.merged_product_info, map);
			Iterator<MergedProductInfoDTO> mergedRows = QueryMapper.mergedProductInfoDTO().map(rs).iterator();
			processCompetitorFields(field, mergedRows);
			field.setMerged(true);
			batchRepository.save(field);
		}
	}

	private void processCompetitorFields(ReviewedProductInfoDTO prod, Iterator<MergedProductInfoDTO> mergedRows) {
		boolean matched = false;
		float score = 0.0f;
		matched = false;
		float highScore = 0.0f;
		MergedProductInfoDTO mergedToProduct = null;

		if (mergedRows != null && mergedRows.hasNext()) {
			while (mergedRows.hasNext()) {
				MergedProductInfoDTO mergedProductInfo = mergedRows.next();
				score = matchScore.compare(prod, mergedProductInfo);
				if (score > SCORE && score > highScore) {
					highScore = score;
					mergedToProduct = mergedProductInfo;
				}
			}
			if (mergedToProduct != null) {
				matched = true;
				UUID id = batchRepository.getMergePropRow(mergedToProduct.getId(), prod.getSubCategoryName(), prod.getProperties());
				prod.setMergeProdInfoId(id);
			}
		}
		if (matched == false) {
			convertReviewDataToMergedRow(prod);
		}
	}

	private void convertReviewDataToMergedRow(ReviewedProductInfoDTO prod) {
		UUID uuid = UUIDs.random();

		prod.setMergeProdInfoId(uuid); // to add uuid to reviewed product

		// New MergeProductInfo Row
		MergedProductInfoDTO __tmpMergedProductInfo = new MergedProductInfoDTO(prod);
		try {
			esRepository.saveRequest(__tmpMergedProductInfo); // Creating ES
																// Index
		} catch (Exception e) {
			log.error("", e);
			msgLog.addError(e);
		}
		batchRepository.save(__tmpMergedProductInfo);

		MergeProductPropertiesDTO mergeProperties = new MergeProductPropertiesDTO(uuid, __tmpMergedProductInfo.getId(), prod.getSubCategoryName(),
				prod.getProperties());
		batchRepository.save(mergeProperties);
	}
}
