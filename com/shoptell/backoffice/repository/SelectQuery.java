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

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

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

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class SelectQuery {
	private static final Logger log = LoggerFactory.getLogger(SelectQuery.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
	}

	public ResultSet selectWithOperations(TableEnum table, boolean allowFiltering, String[] keys, QueryOperations[] operations, Object[] values,
			String... columns) {
		if (keys != null && operations != null && values != null) {
			if (keys.length == operations.length && keys.length == values.length) {
				Select select = null;
				if (allowFiltering) {
					if (columns != null && columns.length > 0) {
						select = QueryBuilder.select(columns).from(keyspace, table.name()).allowFiltering();
					}
					else {
						select = QueryBuilder.select().all().from(keyspace, table.name()).allowFiltering();
					}
				}
				else {
					if (columns != null && columns.length > 0) {
						select = QueryBuilder.select(columns).from(keyspace, table.name());
					}
					else {
						select = QueryBuilder.select().all().from(keyspace, table.name());
					}
				}
				return selectQuery(select, keys, operations, values);
			}
		}
		log.error("INVALID OPERATIONS SELECT QUERY FOR TABLE " + table.name());
		return null;
	}

	private ResultSet selectQuery(Select select, String[] keys, QueryOperations[] operations, Object[] values) {
		Where statement = null;
		boolean first = true;
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object value = values[i];
			QueryOperations op = operations[i];
			if (StringUtils.isNotBlank(key) && value != null) {
				if (first) {
					statement = select.where(op.getQuery(key, value));
					first = false;
				}
				else {
					statement = statement.and(op.getQuery(key, value));
				}
			}
		}
		Statement stmt = null;
		if (statement == null) {
			stmt = select;
		}
		stmt = statement;
		return executeQuery(stmt);
	}

	public ResultSet selectAllWithFiltering(TableEnum table, Map<String, Object> map) {
		return selectAll(table, map, null, true, null);
	}

	public ResultSet selectAll(TableEnum table) {
		return selectAll(table, null);
	}

	public ResultSet selectAll(TableEnum table, Map<String, Object> map) {
		return selectAll(table, map, null, false, null);
	}

	public ResultSet selectAll(TableEnum table, Map<String, Object> map, int limit) {
		return selectAll(table, map, null, false, limit);
	}

	public ResultSet selectAll(TableEnum table, Map<String, Object> map, Map<String, Object> contains) {
		return selectAll(table, map, contains, false, null);
	}

	public ResultSet selectAll(TableEnum table, Map<String, Object> map, Map<String, Object> contains, boolean allowFiltering) {
		return selectAll(table, map, contains, allowFiltering, null);
	}

	public ResultSet selectAll(TableEnum table, Map<String, Object> map, Map<String, Object> contains, boolean allowFiltering, Integer limit) {
		Select select = null;
		if (allowFiltering) {
			select = QueryBuilder.select().all().from(keyspace, table.name()).allowFiltering();
		}
		else {
			select = QueryBuilder.select().all().from(keyspace, table.name());
		}

		if (limit != null) {
			select = select.limit(limit);
		}

		return selectQuery(select, map, contains);
	}

	private ResultSet selectQuery(Select select, Map<String, Object> map, Map<String, Object> contains) {
		Statement statement = where(select, map, contains);
		return executeQuery(statement);
	}

	private ResultSet executeQuery(Statement statement) {
		if (statement == null) {
			return null;
		}
		statement.setFetchSize(BATCHSIZE);

		// log.info("Select Query - {}", statement.toString());
		ResultSet row = null;
		try {
			row = session.execute(statement);
		} catch (NoHostAvailableException e) {
			log.error("executeQuery() NoHostAvailableException Found. Retrying query again");
			try {
				row = session.execute(statement);
			} catch (NoHostAvailableException ex) {
				log.error("executeQuery() NoHostAvailableException Found Again. Query - {}", statement.toString());
			}
		}
		return row;
	}

	@SuppressWarnings("unchecked")
	private Statement where(Select select, Map<String, Object> map, Map<String, Object> contains) {
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
		if (contains != null && contains.size() > 0) {
			for (Entry<String, Object> entry : contains.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (StringUtils.isNotBlank(key) && value != null) {
					if (value instanceof List<?>) {
						for (Object val : (List<Object>) value) {
							if (first) {
								statement = select.where(QueryBuilder.contains(key, val));
								first = false;
							}
							else {
								statement = statement.and(QueryBuilder.contains(key, val));
							}
						}
					}
					else {
						if (first) {
							statement = select.where(QueryBuilder.contains(key, value));
							first = false;
						}
						else {
							statement = statement.and(QueryBuilder.contains(key, value));
						}
					}
				}
			}
		}

		if (statement == null) {
			return select;
		}
		return statement;
	}

	public ResultSet selectColumns(TableEnum table, String... columns) {
		Select select = QueryBuilder.select(columns).from(keyspace, table.name());
		return selectQuery(select, null, null);
	}

	public ResultSet selectColumns(TableEnum table, Map<String, Object> map, String... columns) {
		Select select = QueryBuilder.select(columns).from(keyspace, table.name());
		return selectQuery(select, map, null);
	}

	public ResultSet selectColumns(TableEnum table, Map<String, Object> map, boolean distinct, String... columns) {
		if (distinct) {
			Select select = QueryBuilder.select(columns).distinct().from(keyspace, table.name());
			return selectQuery(select, map, null);
		}
		else {
			return selectColumns(table, map, columns);
		}
	}
}
