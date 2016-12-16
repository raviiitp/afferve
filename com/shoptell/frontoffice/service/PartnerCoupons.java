/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.BackofficeConstants.ALL_COUPON_CODES;
import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.enums.TypesEnum;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;

@Named(value="PartnerCoupons")
public class PartnerCoupons {

	private static final Logger log = LoggerFactory.getLogger(PartnerCoupons.class);
	
	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	private String tableName;

	private Mapper<PartnerCouponsDTO> mapper;
	
	public static Map<String, List<PartnerCouponsDTO>> couponMap = new HashMap<String, List<PartnerCouponsDTO>>();

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.partner_coupons.name();
		mapper = new MappingManager(session).mapper(PartnerCouponsDTO.class);
	}
	
	@Scheduled(cron="0 35 18 ? * *") //UTC to IST
	public void dailyReset(){
		couponMap.clear();
	}

	public List<PartnerCouponsDTO> getCoupons(String key, int count, String pageNumber) {
		List<PartnerCouponsDTO> results = null;
		if (couponMap.containsKey(key)){
			results = couponMap.get(key);
		}
		else {
			try {
				if (StringUtils.equals(key, "ALL")) {
					Select all = QueryBuilder.select().all().from(keyspace, tableName);
					all.setFetchSize(FETCHSIZE);
					results = mapper.map(session.execute(all)).all();
				}
				else {
					Where matcherAll = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.contains("belongsTo", key));
					matcherAll.setFetchSize(FETCHSIZE);
					results = mapper.map(session.execute(matcherAll)).all();
					if (TypesEnum.RECHARGE.getName().equalsIgnoreCase(key)) {
						couponMap.put(key, results);
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
		if (results != null && results.size() > count && count > 0){
			return results.subList(0, count);
		}
		return results;
	}
	
	public List<PartnerCouponsDTO> getCouponCodes() {
		if (!couponMap.containsKey(ALL_COUPON_CODES)) {
			try {
				List<PartnerCouponsDTO> results = null;
				Where matcherAll = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("hasCouponCode", true));
				matcherAll.setFetchSize(FETCHSIZE);
				results = mapper.map(session.execute(matcherAll)).all();
				couponMap.put(ALL_COUPON_CODES, results);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return couponMap.get(ALL_COUPON_CODES);
	}

	public void saveCoupon(PartnerCouponsDTO dto) {
		if (dto != null) {
			dto.setHasCouponCode(StringUtils.isNotBlank(dto.getCouponCode()));
			List<PartnerCouponsDTO> list = getCoupons(dto.getHome(), Integer.MAX_VALUE, null);
			for (PartnerCouponsDTO tmp : list) {
				if ((StringUtils.isNotBlank(dto.getCouponCode()) && dto.getCouponCode().equalsIgnoreCase(tmp.getCouponCode()))
						&& (StringUtils.isNotBlank(dto.getHeader_text()) && dto.getHeader_text().equalsIgnoreCase(tmp.getHeader_text()))
						&& (StringUtils.isNotBlank(dto.getBody_text()) && dto.getBody_text().equalsIgnoreCase(tmp.getBody_text()))) {
					return;
				}
			}
			refine(dto);
			if (dto.getExpireDate() != null) {
				Date currDate = new Date(System.currentTimeMillis());
				if (dto.getExpireDate().after(currDate)) {
					int sec = (int) ((dto.getExpireDate().getTime() - currDate.getTime()) / 1000);
					mapper.save(dto, Option.ttl(sec));
					couponMap.clear();
				}
			}
			else {
				mapper.save(dto);
				couponMap.clear();
			}
		}
	}

	private void refine(PartnerCouponsDTO dto) {
		String bt = dto.getBody_text();
		if (StringUtils.isNotBlank(bt)){
			bt = bt.replace("&amp;", "&").replaceAll("\\s+", " ");
			bt = bt.replace("Rs.", "₹").replace("Rs", "₹");
			dto.setBody_text(bt);
		}
		bt = dto.getHeader_text();
		if (StringUtils.isNotBlank(bt)){
			bt = bt.replace("&amp;", "&").replaceAll("\\s+", " ");
			bt = bt.replace("Rs.", "₹").replace("Rs", "₹");
			dto.setHeader_text(bt);
		}
		bt = dto.getFooter_text();
		if (StringUtils.isNotBlank(bt)){
			bt = bt.replace("&amp;", "&").replaceAll("\\s+", " ");
			bt = bt.replace("Rs.", "₹").replace("Rs", "₹");
			dto.setFooter_text(bt);
		}
		
	}

	public void deleteCoupon(PartnerCouponsDTO dto){
		mapper.delete(dto);
		couponMap.clear();
	}

	/**
	 * @return the couponMap
	 */
	public Map<String, List<PartnerCouponsDTO>> getCouponMap() {
		return couponMap;
	}

	public void save(PartnerCouponsDTO dto) {
		if (dto != null) {
			dto.setHasCouponCode(StringUtils.isNotBlank(dto.getCouponCode()));
			refine(dto);
			mapper.save(dto);
			couponMap.clear();
		}
	}
}
