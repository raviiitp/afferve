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

import java.util.LinkedList;
import java.util.List;
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
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Delete.Where;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class DeleteQuery {
	private static final Logger log = LoggerFactory.getLogger(DeleteQuery.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
	}

	public Statement deleteQueryStatement(TableEnum table, Map<String, Object> map) {
		List<String> key = new LinkedList<String>();
		List<Object> value = new LinkedList<Object>();

		for (Entry<String, Object> en : map.entrySet()) {
			String pin = en.getKey();
			Object point = en.getValue();
			if (point != null) {
				key.add(pin);
				value.add(point);
			}
		}

		Statement statement = where(QueryBuilder.delete().all().from(keyspace, table.name()), map);
		log.info(statement.toString());
		return statement;
	}

	public void deleteQuery(TableEnum table, Map<String, Object> map) {
		session.execute(deleteQueryStatement(table, map));
	}

	private Statement where(Delete delete, Map<String, Object> map) {
		Where statement = null;
		boolean first = true;
		if (map != null && map.size() > 0) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (StringUtils.isNotBlank(key) && value != null) {
					if (first) {
						statement = delete.where(QueryBuilder.eq(key, value));
						first = false;
					}
					else {
						statement = statement.and(QueryBuilder.eq(key, value));
					}
				}
			}
		}

		if (statement == null) {
			return delete;
		}
		return statement;
	}
}
