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

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;

@Named
public class UserTransactionService {

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	private String tableName;

	private Mapper<UserTransactionDTO> mapper;

	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.usertransactions.name();
		mapper = new MappingManager(session).mapper(UserTransactionDTO.class);
	}
	
	public List<UserTransactionDTO> getTrasnactionsByUserId(String userId) {
		Where select = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("userId", userId));
		List<UserTransactionDTO> results = mapper.map(session.execute(select)).all();
		return results;
	}
	
	public void mergeUserTransactionsTable(String userId, String finalUserId) {
		BatchStatement batch = new BatchStatement();

		List<UserTransactionDTO> userTransactionDTOList = getTrasnactionsByUserId(userId);

		for (UserTransactionDTO userTransactionDTO : userTransactionDTOList) {
			batch.add(QueryBuilder.update(keyspace, tableName).with(QueryBuilder.set("userId", finalUserId))
					.where(QueryBuilder.eq("transactionId", userTransactionDTO.getTransactionId())).and(QueryBuilder.eq("time", userTransactionDTO.getTime())));
		}
		if (userTransactionDTOList.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
	}
}
