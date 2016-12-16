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

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.enums.CategoryEnum.ALL;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.scheduling.annotation.Scheduled;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.BankDiscountDTO;

@Named(value = "BankDiscountsService")
public class BankDiscountsService extends Service {

	private List<BankDiscountDTO> list;
	private Mapper<BankDiscountDTO> mapper;
	private static Map<String, List<BankDiscountDTO>> map = new HashMap<String, List<BankDiscountDTO>>();
	
	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.bank_discounts.name();
		mapper = new MappingManager(session).mapper(BankDiscountDTO.class);
		init();
	}

	@Scheduled(cron="0 35 18 ? * *") //UTC to IST
	public void dailyReset(){
		init();
	}
	
	public void init() {
		preprocess();
		execute();
	}

	private void execute() {
		map.clear();
		for (BankDiscountDTO row : list) {
			if ((row.getStartDate() != null && row.getStartDate().after(new Date(System.currentTimeMillis()))) || !row.isActive()) {
				continue;
			}
			String key = row.getHome() + "_" + row.getSubCategory();
			if (!map.containsKey(key)){
				map.put(key, new LinkedList<BankDiscountDTO>());
			}
			map.get(key).add(row);
		}

	}

	private void preprocess() {
		Where stmt = QueryBuilder.select().all().from(keyspace, tableName).allowFiltering().where(QueryBuilder.gte("endDate", new Date(System.currentTimeMillis())));
		list = mapper.map(session.execute(stmt)).all();
	}

	public static List<BankDiscountDTO> getDiscount(String home, String subcategory) {
		List<BankDiscountDTO> value = null;
		String key = home + "_" + subcategory;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		else {
			key = home + "_" + ALL.name();
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		return value;
	}

	public List<BankDiscountDTO> getList() {
		return list;
	}
	
	public void delete(BankDiscountDTO dto){
		if (dto != null){
			repository.delete(dto);
		}
		init();
	}

	public void add(BankDiscountDTO dto) {
		repository.save(dto);
		init();// refresh List
	}
}
