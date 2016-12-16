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

import static com.shoptell.backoffice.BackofficeConstants.BATCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;

import java.util.Iterator;
import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update.Where;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

@Named(value = "HomeProductSyncService")
public class HomeProductSyncService extends Service {
	private static final Logger log = LoggerFactory.getLogger(HomeProductSyncService.class);
	
	public void checkout(List<ProcessLog> list, HomeEnum homeE) throws InterruptedException {
		log.info("checkout() Enter");
		process = processUtil.start(homeE.name(), ProcessLogJobEnum.SYNC_PRODUCTS.name());
		list.add(process);
		execute(homeE.name());
		list.remove(process);
		processUtil.end(process);
		log.info("checkout() Exit");
	}
	
	@Async
	public void init() throws InterruptedException{
		log.info("init() Enter");
		for (HomeEnum homeE : HomeEnum.values()){
			execute(homeE.name());
		}
		log.info("init() Exit");
	}

	private void execute(String home) throws InterruptedException {
		log.info("execute() Enter");
		BatchStatement batch = new BatchStatement();
		Statement statement = QueryBuilder.select("home","subcategoryname","id").from(keyspace, TableEnum.reviewed_product_info.name())
				.where(QueryBuilder.eq("home", home));
		statement.setFetchSize(FETCHSIZE);
			ResultSet rs = session.execute(statement);
			Iterator<Row> rows = rs.iterator();
			while (!rs.isFullyFetched()){
				rs.fetchMoreResults();
				Row row = rows.next();
				Where stmt = QueryBuilder.update(keyspace, TableEnum.home_product_info.name()).with(QueryBuilder.set("ismerged", true))
						.where(QueryBuilder.eq("home", row.getObject("home"))).and(QueryBuilder.eq("id", row.getObject("id")))
						.and(QueryBuilder.eq("subcategoryname", row.getObject("subcategoryname")));
				batch.add(stmt);
				if (batch.size() > BATCHSIZE) {
					session.execute(batch);
					batch.clear();
				}
			}
			session.execute(batch);
			batch.clear();
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("KILL THREAD");
			}
			log.info("execute() Exit");
	}
}
