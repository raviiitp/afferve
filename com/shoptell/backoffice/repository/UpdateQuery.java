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

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import com.datastax.driver.core.querybuilder.Update.Where;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class UpdateQuery {
	private static final Logger log = LoggerFactory.getLogger(UpdateQuery.class);

	@Inject
	private Environment env;
	
	@Inject
	private Session session;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
	}
	
	public void updateQuery(TableEnum table, Map<String, Object> values, Map<String, Object> map){
		Statement statement = updateQueryStatement(table, values, map);
		session.execute(statement);
	}

	public Statement updateQueryStatement(TableEnum table, Map<String, Object> values, Map<String, Object> map) {
		Update update = QueryBuilder.update(keyspace, table.name());
		Statement statement = where(update, values, map);
		//log.info("Update Query - {}", statement.toString());
		return statement;
	}

	private Statement where(Update update, Map<String, Object> values, Map<String, Object> map) {
		Where statement = null;
		if (values != null && values.size() > 0) {
			boolean first = true;
			Assignments assignment = null;
			for (Entry<String, Object> entry : values.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (StringUtils.isNotBlank(key) && value != null) {
					if (first) {
						assignment = update.with(QueryBuilder.set(key, value));
						first = false;
					}
					else {
						assignment = assignment.and(QueryBuilder.set(key, value));
					}
				}
			}
			if (assignment == null) {
				return update;
			}
			first = true;
			if (map != null && map.size() > 0) {
				for (Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (StringUtils.isNotBlank(key) && value != null) {
						if (first) {
							statement = assignment.where(QueryBuilder.eq(key, value));
							first = false;
						}
						else {
							statement = statement.and(QueryBuilder.eq(key, value));
						}
					}
				}
			}
		}

		if (statement == null) {
			return update;
		}
		return statement;
	}
}
