/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.crons;

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.ONE_DAY;
import static com.shoptell.backoffice.enums.TableEnum.home_product_info;
import static com.shoptell.backoffice.enums.TableEnum.merged_product_info;
import static com.shoptell.backoffice.enums.TableEnum.merged_product_properties;
import static com.shoptell.backoffice.enums.TableEnum.message_log;
import static com.shoptell.backoffice.enums.TableEnum.partner_coupons;
import static com.shoptell.backoffice.enums.TableEnum.reviewed_product_info;
import static com.shoptell.backoffice.repository.QueryOperations.EQ;
import static com.shoptell.backoffice.repository.QueryOperations.LTE;
import static com.shoptell.db.messagelog.MessageEnum.ERROR;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.shoptell.backoffice.BackofficeProcessor;
import com.shoptell.backoffice.repository.CountQuery;
import com.shoptell.backoffice.repository.DeleteQuery;
import com.shoptell.backoffice.repository.QueryOperations;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.util.stproperties.STProperties;

@Named(value = "DataClear")
public class DataClear {

	private static final Logger log = LoggerFactory.getLogger(BackofficeProcessor.class);

	@Inject
	private Session session;

	@Inject
	protected STProperties stprop;

	@Inject
	private CountQuery countQuery;

	@Inject
	private ProcessLogUtil process_log;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private DeleteQuery deleteQuery;

	@Async
	public void init() {
		log.info("init() Enter");
		clearAll();
		log.info("init() Exit");
	}

	public void clearAll() {
		log.info("clearAll() Enter");
		ProcessLog pLog = process_log.start("ALL", "DATA CLEAR");
		productTableClear();
		reviewTableClear();
		mergePropertiesTableClear();
		mergeInfoTableClear();
		partnerCouponsClear();
		messageLogClear();
		process_log.end(pLog);
		log.info("clearAll() Exit");
	}

	private void messageLogClear() {
		log.info("messageLogClear() Enter");
		BatchStatement batch = new BatchStatement();
		String[] keys = { "severity", "time" };
		QueryOperations[] operations = { EQ, LTE };
		Object[] values = { ERROR.name(), new Date(System.currentTimeMillis() - 15 * ONE_DAY) };
		ResultSet rows = selectQuery.selectWithOperations(message_log, true, keys, operations, values, "message_log_id");
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("message_log_id", tmp.getUUID("message_log_id"));
				batch.add(deleteQuery.deleteQueryStatement(message_log, map));
				if (batch.size() > BATCHSIZE) {
					session.execute(batch);
					batch.clear();
				}
			}
		}
		session.execute(batch);
		log.info("messageLogClear() Exit");
	}

	private void partnerCouponsClear() {
		log.info("partnerCouponsClear() Enter");
		BatchStatement batch = new BatchStatement();
		ResultSet rows = selectQuery.selectColumns(partner_coupons, "home", "time", "expiredate");
		Date clearDate = new Date(System.currentTimeMillis());
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();
				Date expireOn = tmp.getDate("expiredate");
				if (expireOn != null && clearDate.before(expireOn)) {
					continue;
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("home", tmp.getString("home"));
				map.put("time", tmp.getUUID("time"));

				batch.add(deleteQuery.deleteQueryStatement(partner_coupons, map));

				if (batch.size() > BATCHSIZE) {
					session.execute(batch);
					batch.clear();
				}
			}
		}
		session.execute(batch);
		log.info("partnerCouponsClear() Exit");
	}

	private void reviewTableClear() {
		log.info("reviewTableClear() Enter");
		BatchStatement batch = new BatchStatement();
		ResultSet rows = selectQuery.selectColumns(reviewed_product_info, "home", "subcategoryname", "id");
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("home", tmp.getString("home"));
				map.put("subcategoryname", tmp.getString("subcategoryname"));
				map.put("id", tmp.getString("id"));

				long count = countQuery.countAll(home_product_info, map);

				if (count == 0) {
					Map<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("home", tmp.getString("home"));
					queryMap.put("subcategoryname", tmp.getString("subcategoryname"));
					queryMap.put("id", tmp.getString("id"));
					batch.add(deleteQuery.deleteQueryStatement(reviewed_product_info, queryMap));
				}

				if (batch.size() > BATCHSIZE) {
					session.execute(batch);
					batch.clear();
				}
			}
		}
		session.execute(batch);
		log.info("reviewTableClear() Exit");
	}

	private void mergeInfoTableClear() {
		log.info("mergeInfoTableClear() Enter");
		BatchStatement batch = new BatchStatement();
		ResultSet rows = selectQuery.selectColumns(merged_product_info, "id");
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mergeprodid", tmp.getUUID("id"));
				long count = countQuery.countAll(merged_product_properties, map);

				if (count == 0) {
					Map<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("id", tmp.getUUID("id"));
					deleteQuery.deleteQueryStatement(merged_product_info, queryMap);

					if (batch.size() > BATCHSIZE) {
						session.execute(batch);
						batch.clear();
					}
				}
			}
		}
		session.execute(batch);
		log.info("mergeInfoTableClear() Exit");
	}

	private void mergePropertiesTableClear() {
		log.info("mergePropertiesTableClear() Enter");
		BatchStatement batch = new BatchStatement();
		ResultSet rows = selectQuery.selectColumns(merged_product_properties, "mergeprodid", "id");
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mergeprodinfoid", tmp.getUUID("id"));
				long count = countQuery.countAll(reviewed_product_info, map);
				if (count == 0) {
					Map<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("mergeprodid", tmp.getUUID("mergeprodid"));
					queryMap.put("id", tmp.getUUID("id"));
					batch.add(deleteQuery.deleteQueryStatement(merged_product_properties, queryMap));

					if (batch.size() > BATCHSIZE) {
						session.execute(batch);
						batch.clear();
					}
				}
			}
		}
		session.execute(batch);
		log.info("mergePropertiesTableClear() Exit");
	}

	private void productTableClear() {
		log.info("productTableClear() Enter");
		BatchStatement batch = new BatchStatement();
		ResultSet rows = selectQuery.selectColumns(home_product_info, "home", "subcategoryname", "id", "createdon", "modifiedon");
		Date clearDate = new Date(System.currentTimeMillis() - ONE_DAY * 30);
		if (rows != null) {
			Iterator<Row> rs = rows.iterator();
			while (rs.hasNext()) {
				Row tmp = rs.next();
				Date createdOn = tmp.getDate("createdon");
				if (createdOn != null) {
					if (clearDate.before(createdOn)) {
						continue;
					}
					Date modifiedOn = tmp.getDate("modifiedon");
					if (modifiedOn != null && clearDate.before(modifiedOn)) {
						continue;
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("home", tmp.getString("home"));
					map.put("subcategoryname", tmp.getString("subcategoryname"));
					map.put("id", tmp.getString("id"));
					batch.add(deleteQuery.deleteQueryStatement(home_product_info, map));

					if (batch.size() > BATCHSIZE) {
						session.execute(batch);
						batch.clear();
					}
				}
			}
		}
		session.execute(batch);
		log.info("productTableClear() Exit");
	}
}
