/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository;

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class CountQuery {
	private static final Logger log = LoggerFactory.getLogger(CountQuery.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
	}

	public long countAll(TableEnum table, Map<String, Object> map) {
		return countAll(table, map, false);
	}

	public long countAll(TableEnum table, Map<String, Object> map, boolean allowFiltering) {
		Select select = null;
		if (allowFiltering) {
			select = QueryBuilder.select().countAll().from(keyspace, table.name()).allowFiltering();
		}
		else {
			select = QueryBuilder.select().countAll().from(keyspace, table.name());
		}

		Statement statement = where(select, map);

		//log.info("Query Executed - {}", statement.toString());
		ResultSet row = session.execute(statement);
		Row response = row.one();
		return response.getLong(0);
	}

	private Statement where(Select select, Map<String, Object> map) {
		Where statement = null;
		boolean first = true;
		if (map != null && map.size() > 0) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (StringUtils.isNotBlank(key) && value != null) {
					if (first) {
						statement = select.where(QueryBuilder.eq(key, value));
						first = false;
					}
					else {
						statement = statement.and(QueryBuilder.eq(key, value));
					}
				}
			}
		}
		if (statement == null){
			return select;
		}
		return statement;
	}
}
