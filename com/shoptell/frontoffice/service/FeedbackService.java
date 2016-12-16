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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.enums.TicketStatusEnum;
import com.shoptell.backoffice.repository.dto.FeedbackDTO;

@Named
public class FeedbackService {

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private String keyspace;

	private String tableName;

	private Mapper<FeedbackDTO> mapper;
	
	@PostConstruct
	public void start() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.feedback.name();
		mapper = new MappingManager(session).mapper(FeedbackDTO.class);
	}
	
	public List<FeedbackDTO> getOpenFeedbacks(){
		Where select = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("status", TicketStatusEnum.OPEN.name()));
		List<FeedbackDTO> results = mapper.map(session.execute(select)).all();
		return results;
	}
	
	public void saveFeedback(String email, String mobileNumber, String message) {
		FeedbackDTO feedbackDTO = new FeedbackDTO(email, mobileNumber, message);
		mapper.save(feedbackDTO);
	}
	
	public void closeTicket(UUID feedbackId) {
		Statement updateCloseStatus = QueryBuilder.update(keyspace, tableName)
				.with(QueryBuilder.set("status", TicketStatusEnum.CLOSED.name()))
						.and(QueryBuilder.set("closeDate", new Date(System.currentTimeMillis())))
				.where(QueryBuilder.eq("feedbackId", feedbackId));
		session.execute(updateCloseStatus);
	}
}
