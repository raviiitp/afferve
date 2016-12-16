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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.CBStatusEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.UpdateQuery;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.security.EncryptDecryptUtil;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "PaymentService")
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
	
	private static final TableEnum tableName = TableEnum.cbpayment;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private UpdateQuery updateQuery;

	@Inject
	private Session session;

	@Inject
	private BatchRepository repository;

	@Inject
	private WalletService walletService;

	@Inject
	private EncryptDecryptUtil encryptDecryptUtil;

	private Mapper<CBPaymentDTO> mapper;

	@PostConstruct
	public void start() throws InterruptedException {
		mapper = new MappingManager(session).mapper(CBPaymentDTO.class);
	}

	public List<CBPaymentDTO> getPaymentReport(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		ResultSet rs = selectQuery.selectAll(tableName, map);
		List<CBPaymentDTO> results = mapper.map(rs).all();
		for (CBPaymentDTO result : results) {
			try {
				result.setAccountNumber(encryptDecryptUtil.decryptAndMask(result.getAccountNumber()));
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return results;
	}

	public void addPayment(CBPaymentDTO dto) {
		repository.save(dto);
	}

	public CBPaymentDTO transferMoneyToAccount(String userId, UserAccountDTO account) {
		List<CBReportDTO> walletReportList = walletService.getWalletReport(userId);
		List<UUID> uuidList = null;
		double cashBack = 0;
		if (walletReportList != null && walletReportList.size() > 0) {
			uuidList = new ArrayList<UUID>();
			for (CBReportDTO report : walletReportList) {
				if (StringUtils.equalsIgnoreCase(report.getStatus(), "RECEIVED")) {
					uuidList.add(report.getTime());
					cashBack += Double.parseDouble(report.getCashBackAmount());
				}
			}
		}
		
		String description = null;
		if (!StringUtils.equals(account.getBankName(), "AFFERVE")) {
			description = "5% has been deducted from ₹ " + String.valueOf(cashBack) + " as NEFT transaction charge";
			cashBack = (cashBack * 0.95); // deduct 5% in case of NEFT
		}
		cashBack = Math.floor(cashBack * 100) / 100;

		walletService.batchUpdate(userId, uuidList, CBStatusEnum.WITHDRAWN);

		CBPaymentDTO cBPaymentDTO = new CBPaymentDTO();
		cBPaymentDTO.setAccountNumber(account.getAccountNumber());
		cBPaymentDTO.setBankName(account.getBankName());
		cBPaymentDTO.setCashBackAmount(cashBack);
		cBPaymentDTO.setDescription(description);
		cBPaymentDTO.setIfscCode(account.getIfscCode());
		cBPaymentDTO.setStatus(CBStatusEnum.REQUESTED.name());
		cBPaymentDTO.setTransactionNumber("");
		cBPaymentDTO.setUserId(userId);
		cBPaymentDTO.setUsername(account.getUserName());
		cBPaymentDTO.setModifiedOn(new Date());
		mapper.save(cBPaymentDTO);

		return cBPaymentDTO;
	}

	public void batchUpdate(String userId, List<UUID> uuidList, CBStatusEnum status) {
		BatchStatement batch = new BatchStatement();
		try {
			for (UUID uuid : uuidList) {
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("status", status.name());

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

	public void mergeCBPayment(String userId, String finalUserId) {
		BatchStatement batch = new BatchStatement();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		ResultSet rs = selectQuery.selectAll(tableName, map);

		List<CBPaymentDTO> cbPaymentDTOList = mapper.map(rs).all();

		for (CBPaymentDTO cbPaymentDTO : cbPaymentDTOList) {
			batch.add(mapper.deleteQuery(cbPaymentDTO));
			cbPaymentDTO.setUserId(finalUserId);
			batch.add(mapper.saveQuery(cbPaymentDTO));
		}

		if (cbPaymentDTOList.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
	}
}
