/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.BackofficeConstants.MAX_TRANSACTION_LIST;
import static com.shoptell.backoffice.repository.QueryMapper.cBReportDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.shoptell.backoffice.enums.CBStatusEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.UpdateQuery;
import com.shoptell.backoffice.repository.dto.CBReportDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "WalletService")
public class WalletService {
	private static final Logger log = LoggerFactory.getLogger(WalletService.class);
	
	private static final TableEnum tableName = TableEnum.cbreport;

	@Inject
	private Session session;

	@Inject
	private BatchRepository repository;

	@Inject
	private SelectQuery selectQuery;
	
	@Inject
	private UpdateQuery updateQuery;

	// Page Values Start From 1 .. 
	public List<CBReportDTO> getWalletReportWithPagination(String userId, Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		ResultSet rs = selectQuery.selectAll(tableName, map);
		List<CBReportDTO> results = cBReportDTO().map(rs).all();
		
		if (page != null && results.size() > MAX_TRANSACTION_LIST){
			if (page < 2){
				page = 1;
			}
			page--; // because items start from 0
			if (results.size() > (page+1)*MAX_TRANSACTION_LIST){
				return results.subList(page*MAX_TRANSACTION_LIST, (page+1)*MAX_TRANSACTION_LIST);
			}
			else {
				return results.subList(page*MAX_TRANSACTION_LIST, results.size());
			}
		}
		
		return results;
	}

	public List<CBReportDTO> getWalletReport(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		ResultSet rs = selectQuery.selectAll(tableName, map);
		List<CBReportDTO> results = cBReportDTO().map(rs).all();
		return results;
	}

	public void batchUpdate(String userId, List<UUID> uuidList, CBStatusEnum status) {
		BatchStatement batch = new BatchStatement();
		try {
			for (UUID uuid : uuidList) {
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("status", status.name());
				values.put("modifiedOn", new Date());

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", userId);
				map.put("time", uuid);

				Statement rs = updateQuery.updateQueryStatement(tableName, values, map);
				if (rs != null) {
					batch.add(rs);
				}
			}
			session.execute(batch);
		} catch (Exception e) {
			log.error("", e);
		}
		batch.clear();
	}

	public void addToWallet(CBReportDTO cb) {
		repository.save(cb);
	}

	public void mergeCBReport(String userId, String finalUserId) {
		BatchStatement batch = new BatchStatement();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		ResultSet rs = selectQuery.selectAll(tableName, map);

		List<CBReportDTO> cbReportDTOList = cBReportDTO().map(rs).all();

		for (CBReportDTO cbReportDTO : cbReportDTOList) {
			batch.add(cBReportDTO().deleteQuery(cbReportDTO));
			cbReportDTO.setUserId(finalUserId);
			batch.add(cBReportDTO().saveQuery(cbReportDTO));
		}

		if (cbReportDTOList.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
	}
}
