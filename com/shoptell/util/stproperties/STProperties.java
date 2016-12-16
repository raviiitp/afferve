/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.util.stproperties;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.enums.TableEnum;

/**
 * @author abhishekagarwal
 *
 */
@Named (value="STProperties")
@Singleton
public class STProperties {
	
	@Inject
	Environment env;
	
	@Inject
	private Session session;
	
	private static Map<String, String> map = new HashMap<String, String>();

	private String keyspace;

	private String table;
	
	@PostConstruct
	private void start(){
		keyspace = env.getProperty("keyspaceName");
		table = TableEnum.st_properties.name();
		init();
	}
	
	public void init(){
		findAll();
	}
	
	public Map<String, String> Insert(String key, String value, String desc, boolean isUpdate){
		if (isUpdate){
			updateValue(key, value, desc);
		}
		else {
			addValue(key, value, desc);
		}
		return map;
	}
	
	private void addValue(String key, String value, String desc){
		Statement addStmt = QueryBuilder.insertInto(keyspace, table).value("property_name", key).value("property_value", value).value("description", desc);
		session.execute(addStmt);
		init();
	}
	
	private void updateValue(String key, String value, String desc){
		Statement addStmt = QueryBuilder.update(keyspace,table).with(QueryBuilder.set("property_value", value)).and(QueryBuilder.set("description", desc)).where(QueryBuilder.eq("property_name", key));
		session.execute(addStmt);
		init();
	}

	private void findAll(){
		Statement findAllStmt = QueryBuilder.select().all().from(keyspace, table);
		ResultSet rs = session.execute(findAllStmt);
		
		while (!rs.isExhausted()){
			Row row = rs.one();
			String key = row.getString("property_name");
			String value = row.getString("property_value");
			map.put(key, value);
		}
	}
	
	public String getValue(String key){
		if (map.containsKey(key)){
			return map.get(key);
		}
		return null;
	}

	public String getValueOrDefault(String key, String defaultValue) {
		String value = getValue(key);
		if (StringUtils.isBlank(value)){
			value = defaultValue;
		}
		return value;
	}
	
	public Map<String, String[]> getAllProperties(){
		Statement findAllStmt = QueryBuilder.select().all().from(keyspace, table);
		ResultSet rs = session.execute(findAllStmt);
		Map<String, String[]> mAll = new HashMap<String, String[]>();
		while (!rs.isExhausted()){
			Row row = rs.one();
			String key = row.getString("property_name");
			String value = row.getString("property_value");
			String desc = row.getString("description");
			String obj[] = {value,desc};
			mAll.put(key, obj);
		}
		return mAll;
	}

	public void delete(String value) {
		session.execute(QueryBuilder.delete().all().from(keyspace, table).where(QueryBuilder.eq("property_name", value)));
		init();
	}
}
