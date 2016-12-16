/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.util;

import static com.shoptell.backoffice.BackofficeConstants.CONVERSION_RATE;
import static com.shoptell.backoffice.BackofficeConstants.REFFERAL_RATE;
import static com.shoptell.backoffice.enums.CBStatusEnum.DELETED;
import static com.shoptell.backoffice.enums.TableEnum.newcashback;
import static com.shoptell.backoffice.enums.TableEnum.user;
import static com.shoptell.backoffice.repository.QueryMapper.cBReportDTO;
import static com.shoptell.backoffice.repository.QueryOperations.LTE;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.DeleteQuery;
import com.shoptell.backoffice.repository.InsertQuery;
import com.shoptell.backoffice.repository.QueryOperations;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.service.MailService;

@Named
public class CashbackUtil {
	
	@Inject
	private MailService mailService;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private InsertQuery insertQuery;

	@Inject
	private DeleteQuery deleteQuery;

	@Inject
	private BatchRepository batchRepository;

	public void bonusGenerate() {
		UUID high = UUIDs.startOf(System.currentTimeMillis());
		String[] keys = {"time"};
		QueryOperations[] operations = { LTE };
		Object[] values = {high};
		ResultSet rs = selectQuery.selectWithOperations(newcashback, true, keys, operations, values);
		Iterator<Row> itr = rs.iterator();
		while (itr.hasNext()) {
			Row row = itr.next();
			String userId = row.getString("userId");
			String referId = row.getString("referId");
			UUID time = row.getUUID("time");
			processBonus(userId, time, referId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("time", time);
			deleteQuery.deleteQuery(newcashback, map);
		}
	}

	private void processBonus(String userId, UUID time, String referId) {
		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(referId) && time != null) {
			CBReportDTO prod = cBReportDTO().get(userId, time);
			if (prod == null) {
				return;
			}
			UUID myBonusId = prod.getMyBonus();
			UUID referBonusId = prod.getReferBonus();

			switch (prod.getStatus()) {
			case "DELETED":
			case "CANCELLED":
				if (myBonusId != null) {
					changeStatus(userId, myBonusId, prod);
				}
				if (referBonusId != null) {
					changeStatus(referId, referBonusId, prod);
				}
				break;
			case "RECEIVED":
				if (myBonusId != null) {
					changeStatus(userId, myBonusId, prod);
				}
				if (referBonusId != null) {
					changeStatus(referId, referBonusId, prod);
				}
			case "PENDING":
				if (myBonusId == null && referBonusId == null) {
					UUID id = UUIDs.timeBased();
					myBonusId = id;
					referBonusId = id;
					prod.setMyBonus(myBonusId);
					prod.setReferBonus(referBonusId);
					String amt = calculateBonus(prod);
					CBReportDTO myBonus = new CBReportDTO(userId, id, prod, amt, true);
					myBonus.setDescription("Bonus on transaction# " + prod.getTransactionNumber());
					CBReportDTO referBonus = new CBReportDTO(referId, id, prod, amt, false);
					referBonus.setDescription("Referral bonus from " + batchRepository.name(userId) + " on transaction# " + prod.getTransactionNumber());
					batchRepository.save(prod);
					batchRepository.save(referBonus);
					batchRepository.save(myBonus);
				}
				default :
					if (myBonusId != null) {
					mailService.sendCashbackEmail(cBReportDTO().get(userId, myBonusId), true, null);
					}
					if (referBonusId != null) {
					mailService.sendCashbackEmail(cBReportDTO().get(referId, referBonusId), true, userId);
					}
					break;
			}
		}
	}

	private void changeStatus(String userId, UUID time, CBReportDTO prod) {
		CBReportDTO dto = cBReportDTO().get(userId, time);
		dto.setStatus(prod.getStatus());
		dto.setModifiedOn(new Date(System.currentTimeMillis()));
		batchRepository.save(dto);
	}

	private String calculateBonus(CBReportDTO prod) {
		return BackofficeUtil.roundOff(REFFERAL_RATE * CONVERSION_RATE * prod.getAmount());
	}

	public String getReferrer(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", userId);
		ResultSet rs = selectQuery.selectColumns(user, map, "referredby");
		Row row = rs.one();
		if (row != null) {
			String priest = row.getString("referredby");
			if (StringUtils.isNotBlank(priest)) {
				return priest;
			}
		}
		return null;
	}

	public void changeCashback(String userId, UUID bonusId, double amount) {
		if (StringUtils.isNotBlank(userId) && bonusId != null) {
			CBReportDTO prod = cBReportDTO().get(userId, bonusId);
			String amt = BackofficeUtil.roundOff(REFFERAL_RATE * CONVERSION_RATE * amount);
			prod.setCashBackAmount(amt);
			prod.setModifiedOn(new Date(System.currentTimeMillis()));
			batchRepository.save(prod);
		}
	}

	public void deleteCashback(String userId, UUID bonusId) {
		if (StringUtils.isNotBlank(userId) && bonusId != null) {
			CBReportDTO prod = cBReportDTO().get(userId, bonusId);
			prod.setStatus(DELETED.name());
			batchRepository.save(prod);
		}
	}

	public void amountChange(CBReportDTO prod, boolean doNotCut) {
		String referrer = getReferrer(prod.getUserId());
		if (doNotCut) {
			prod.setCashBackAmount(BackofficeUtil.roundOff(prod.getAmount()));
			if (StringUtils.isNotBlank(referrer)) {
				deleteCashback(prod.getUserId(), prod.getMyBonus());
				deleteCashback(referrer, prod.getReferBonus());
			}
		}
		else {
			if (prod.isEligibleForCut()) {
				prod.setCashBackAmount(BackofficeUtil.roundOff(CONVERSION_RATE * prod.getAmount()));
				if (StringUtils.isNotBlank(referrer)) {
					changeCashback(prod.getUserId(), prod.getMyBonus(), prod.getAmount());
					changeCashback(referrer, prod.getReferBonus(), prod.getAmount());
				}
			}
			else {
				prod.setCashBackAmount(BackofficeUtil.roundOff(prod.getAmount()));
				if (StringUtils.isNotBlank(referrer)) {
					deleteCashback(prod.getUserId(), prod.getMyBonus());
					deleteCashback(referrer, prod.getReferBonus());
				}
			}
		}
	}

	@Async
	public void pendingBonusUpdate(CBReportDTO prod) {
		String referrer = getReferrer(prod.getUserId());
		if (StringUtils.isNotBlank(referrer)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", prod.getUserId());
			map.put("referId", referrer);
			map.put("time", prod.getTime());
			insertQuery.insertQuery(newcashback, map);
		}
	}

}
