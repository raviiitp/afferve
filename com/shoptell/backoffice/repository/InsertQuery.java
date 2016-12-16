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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class InsertQuery {
	private static final Logger log = LoggerFactory.getLogger(InsertQuery.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
	}

	public void insertQuery(TableEnum table, Map<String, Object> map) {
		if (map == null || map.size() < 1) {
			return;
		}
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

		Insert statement = QueryBuilder.insertInto(keyspace, table.name()).values(key, value);
		//log.info(statement.toString());

		session.execute(statement);
	}
}
