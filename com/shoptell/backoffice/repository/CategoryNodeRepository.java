/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;

/**
 * Cassandra repository for the ProductInfo entity.
 */
@Named
public class CategoryNodeRepository {

	private static final Logger log = LoggerFactory.getLogger(CategoryNodeRepository.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;
	private String tableName;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.category_node.name();
	}
	
	public List<CategoryNodeDTO> execute(Statement statement){
		List<CategoryNodeDTO> list = QueryMapper.categoryNodeDTO().map(session.execute(statement)).all();
		return list;
	}

	public List<CategoryNodeDTO> findAllByHome(String home) {
		Statement findAllStmt = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("home", home));
		return execute(findAllStmt);
	}

	public List<CategoryNodeDTO> findAll() {
		Statement findAllStmt = QueryBuilder.select().all().from(keyspace, tableName);
		return execute(findAllStmt);
	}

	public List<CategoryNodeDTO> findAllLeaves(String home) {
		Statement findAllLeaves = QueryBuilder.select().all().from(keyspace, tableName).allowFiltering().where(QueryBuilder.eq("isLeaf", true))
				.and(QueryBuilder.eq("home", home));
		return execute(findAllLeaves);
	}

	public List<CategoryNodeDTO> findLeaf(String home, String categoryname) {
		Statement findLeaf = QueryBuilder.select().all().from(keyspace, tableName).allowFiltering().where(QueryBuilder.eq("isLeaf", true))
				.and(QueryBuilder.eq("home", home)).and(QueryBuilder.eq("categoryname", categoryname));
		return execute(findLeaf);
	}
	
	public List<CategoryNodeDTO> findLeafBySearchIndex(String home, String searchIndex) {
		Statement findLeaf = QueryBuilder.select().all().from(keyspace, tableName).allowFiltering().where(QueryBuilder.eq("isLeaf", true))
				.and(QueryBuilder.eq("home", home)).and(QueryBuilder.eq("searchIndex", searchIndex));
		return execute(findLeaf);
	}

	public List<CategoryNodeDTO> getLeaf(String... categoryId) {
		List<CategoryNodeDTO> list = new LinkedList<CategoryNodeDTO>();
		for (String catId : categoryId) {
			Statement findLeaf = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("categoryid", catId));
			list.addAll(execute(findLeaf));
		}
		return list;
	}

	public List<CategoryNodeDTO> findAllRoot(String home) {
		Statement findAllRoots = QueryBuilder.select().all().from(keyspace, tableName).allowFiltering().where(QueryBuilder.eq("isRoot", true))
				.and(QueryBuilder.eq("home", home));
		return execute(findAllRoots);
	}

	/**
	 * 
	 * @param proCatInfo
	 *            is the new product info If a pinfo with pinfo.getId() is
	 *            already present in DB and both are not equal then delete old
	 *            one i.e. stored one. Then insert new pinfo. Else nothing to do
	 */
	public void save(CategoryNodeDTO proCatInfo) {
		// TODO Batch Inserts into DB for Collection<?> generic class
		try {
			CategoryNodeDTO oldProCatInfo = QueryMapper.categoryNodeDTO().get(proCatInfo.getCategoryId());
			if (oldProCatInfo != null) {
				if (!proCatInfo.equals(oldProCatInfo)) {
					delete(oldProCatInfo);
				}
				else {
					return;
				}
			}
			BatchStatement batch = new BatchStatement();
			batch.add(QueryMapper.categoryNodeDTO().saveQuery(proCatInfo));
			session.execute(batch);
			batch.clear();
		} catch (InvalidQueryException e) {
			log.error("", e);
		}

	}

	public void delete(CategoryNodeDTO proCatInfo) {
		BatchStatement batch = new BatchStatement();
		batch.add(QueryMapper.categoryNodeDTO().deleteQuery(proCatInfo));
		session.execute(batch);
		batch.clear();
	}

	public String getNameFromId(String parentId) {
		// TODO Auto-generated method stub
		return null;
	}
}
