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

import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.ActivityDTO;
import com.shoptell.domain.User;

@Named(value="ActivityService")
public class ActivityService extends Service {
	
	private Mapper<ActivityDTO> mapper;

	private static final String[] suffix = {".com",".com/",".in",".in/"};
	
	private static final int MAX_COUNT = 10;
	
	private static List<ActivityDTO> list = new ArrayList<ActivityDTO>();
	
	private static final AtomicInteger counter = new AtomicInteger();

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.activity.name();
		mapper = new MappingManager(session).mapper(ActivityDTO.class);
		init();
	}
	
	private void init() {
		Select select = QueryBuilder.select().all().from(keyspace, tableName).limit(MAX_COUNT);
		select.setFetchSize(FETCHSIZE);
		ResultSet rs = session.execute(select);
		List<ActivityDTO> rows = mapper.map(rs).all();
		if (rows != null && rows.size() > 0) {
			//list.sort(BackofficeUtil.compareActivity);
			if (rows.size() > MAX_COUNT){
				list.addAll(rows.subList(0, MAX_COUNT));
			}
			else {
				list.addAll(rows);
			}
		}
	}

	public List<ActivityDTO> getList() {
		return list;
	}
	
	public void add(ActivityDTO dto){
		if (list.size() >= MAX_COUNT){
			list.remove(counter.get());
			counter.getAndIncrement();
			counter.compareAndSet(MAX_COUNT, 0);
		}
		list.add(0, dto);
		repository.save(dto);
	}

	@Async
	public void addProductActivity(String userId, HomeEnum home, String subCategoryName, String id) {
		Statement select = QueryBuilder.select("producturl","sellingprice","imageurl").from(keyspace, TableEnum.home_product_info.name()).where(QueryBuilder.eq("home", home.name()))
				.and(QueryBuilder.eq("id", id)).and(QueryBuilder.eq("subcategoryname", subCategoryName));

		List<Row> rs = session.execute(select).all();
		if (rs != null && rs.size() > 0) {
			String username = getUserName(userId);
			Map<String, String> prop = null;
			String name = null;
			select = QueryBuilder.select("productbrand","productsubbrand","series","model","properties").from(keyspace, TableEnum.reviewed_product_info.name()).where(QueryBuilder.eq("home", home.name()))
					.and(QueryBuilder.eq("id", id)).and(QueryBuilder.eq("subcategoryname", subCategoryName));
			List<Row> rset = session.execute(select).all();
			if (rset != null && rset.size() > 0){
				Row row = rset.get(0);
				prop = row.getMap("properties", String.class, String.class);
				name = BackofficeUtil.getName(row.getString("productbrand"), row.getString("productsubbrand"), row.getString("series"), row.getString("model"));
			}
			Row rw = rs.get(0);
			ActivityDTO dto = new ActivityDTO(name, ActivityDTO.TYPE_PRODUCT, home.name(), rw.getString("imageurl"), rw.getString("producturl"), userId, username
					, subCategoryName, id, null, null, prop);
			add(dto);
		}
		
	}

	@SuppressWarnings("deprecation")
	private String getUserName(String userId) {
		String username = "unknown";
		if (StringUtils.isNotBlank(username) && !"default".equals(userId)){
			User usr = repository.getUser(userId);
			if (usr != null){
				username = usr.getFirstName()+" "+usr.getLastName();
				if (StringUtils.isNotBlank(username)){
					username = username.trim().toLowerCase();
				}
				else {
					username = "unknown";
				}
			}
		}
		return StringUtils.capitaliseAllWords(username);
	}

	@Async
	public void addURLActivity(String userId, String preUrl, HomeEnum home) {
		String type = ActivityDTO.TYPE_URL;
		for (String sufx : suffix){
			if (preUrl.endsWith(sufx)){
				type = ActivityDTO.TYPE_HOME;
				break;
			}
		}
		
		String username = getUserName(userId);
		ActivityDTO dto = new ActivityDTO(type, home.name(), preUrl, userId, username, null);
		add(dto);
	}
}
