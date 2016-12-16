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

import static com.shoptell.backoffice.BackofficeConstants.CONVERSION_RATE;
import static com.shoptell.backoffice.BackofficeConstants.DEDUCT_FROM_SOURCE;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.BackofficeConstants.MIN_CUT_AMOUNT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.CBRateDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "CBRateService")
public class CBRateService extends Service {
	private List<CBRateDTO> list;

	private static Map<String, CBRateDTO> map = new HashMap<String, CBRateDTO>();
	
	private Mapper<CBRateDTO> mapper;

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.cbrate.name();
		mapper = new MappingManager(session).mapper(CBRateDTO.class);

		init();
	}

	public void init() {
		preprocess();
		execute();
	}

	public void preprocess() {
		Select stmt = QueryBuilder.select().all().from(keyspace, tableName);
		list = mapper.map(session.execute(stmt)).all();
	}

	public void execute() {
		map.clear();
		for (CBRateDTO row : list) {
			map.put(row.getHome() + "_" + row.getSubCategory(), row);
		}
	}

	public static CBRateDTO getCBRate(String key) {
		CBRateDTO val = null;
		if (map.containsKey(key)) {
			val = map.get(key);
		}
		return val;
	}

	public static double getRate(String home, String subcategory) {
		double rate = 0;
		String key = home + "_" + subcategory;
		CBRateDTO val = getCBRate(key);
		if (val != null) {
			rate = val.getWebCBRate();
		}
		return rate;
	}

	public List<CBRateDTO> getList() {
		return list;
	}

	public void add(CBRateDTO dto) {
		repository.save(dto);
		init();// refresh List
	}

	public static double getMaxAmount(String home, String categoryName, double sellingPrice) {
		double cb = 0;
		if (sellingPrice <= 0) {
			return cb;
		}
		String key = home + "_" + categoryName;
		CBRateDTO val = getCBRate(key);
		if (val != null) {
			double rate = val.getWebCBRate();
			rate = rate - DEDUCT_FROM_SOURCE;
			cb = sellingPrice * rate / 100;
			double maxAmt = val.getMaxComission();
			if (maxAmt > 0 && cb > maxAmt) {
				cb = maxAmt;
			}

			if (cb > MIN_CUT_AMOUNT) {
				cb = CONVERSION_RATE * cb;
			}
		}
		String tmp = BackofficeUtil.roundOff(cb);
		return Double.parseDouble(tmp);
	}

	public void delete(CBRateDTO request) {
		mapper.delete(request);
		init();
	}
}
