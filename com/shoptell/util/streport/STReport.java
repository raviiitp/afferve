/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.util.streport;

import static com.shoptell.backoffice.BackofficeConstants.FETCHSIZE;
import static com.shoptell.backoffice.BackofficeConstants.ONE_DAY;
import static com.shoptell.backoffice.BackofficeUtil.getStartOfDay;
import static com.shoptell.db.messagelog.MessageEnum.ERROR;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.env.Environment;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.enums.CBStatusEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.enums.TicketStatusEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.DeleteQuery;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.FeedbackDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.db.messagelog.MessageLog;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

/**
 * @author abhishekagarwal
 *
 */

@Named(value = "STReport")
public class STReport {
	@Inject
	DeleteQuery deleteQuery;

	@Inject
	BatchRepository repository;

	@Inject
	Environment env;

	@Inject
	private Session session;

	public List<MessageLog> generateReport() {
		Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.message_log.name()).allowFiltering()
				.where(QueryBuilder.eq("severity", ERROR.name()))
				.and(QueryBuilder.gte("time", new Date(System.currentTimeMillis() - ONE_DAY / 2))).limit(5);
		return QueryMapper.messageLog().map(session.execute(findAllStmt)).all();
	}

	public List<FeedbackDTO> getOpenFeedbacks() {
		Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.feedback.name()).allowFiltering()
				.where(QueryBuilder.eq("status", TicketStatusEnum.OPEN.name()))
				.and(QueryBuilder.gte("closeDate", new Date(System.currentTimeMillis() - ONE_DAY)));
		return QueryMapper.feedbackDTO().map(session.execute(findAllStmt)).all();
	}

	public List<TicketDTO> getOpenTickets() {
		Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.ticket.name())
				.where(QueryBuilder.eq("status", TicketStatusEnum.OPEN.name()));
		return QueryMapper.ticketDTO().map(session.execute(findAllStmt)).all();
	}

	public List<CBPaymentDTO> getOpenPayments() {
		Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.cbpayment.name())
				.where(QueryBuilder.eq("status", CBStatusEnum.REQUESTED.name()));
		return QueryMapper.cBPaymentDTO().map(session.execute(findAllStmt)).all();
	}

	public List<ProcessLog> getProcessLog() {
		UUID low = UUIDs.startOf(getStartOfDay(new Date(System.currentTimeMillis() - ONE_DAY)).getTime() - 5 * 60 * 60 * 1000);
		for (ProcessLogJobEnum value : ProcessLogJobEnum.values()) {
			Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.process_log.name()).allowFiltering()
					.where(QueryBuilder.lt("time", low)).and(QueryBuilder.eq("job", value.name()));
			findAllStmt.setFetchSize(FETCHSIZE);
			List<ProcessLog> list = QueryMapper.processLog().map(session.execute(findAllStmt)).all();
			repository.batchDelete(list);
		}

		LinkedList<ProcessLog> response = new LinkedList<ProcessLog>();
		UUID high = UUIDs.startOf(getStartOfDay(new Date(System.currentTimeMillis())).getTime());
		for (ProcessLogJobEnum value : ProcessLogJobEnum.values()) {
			Statement findAllStmt = QueryBuilder.select().all().from(env.getProperty("keyspaceName"), TableEnum.process_log.name()).allowFiltering()
					.where(QueryBuilder.gte("time", high)).and(QueryBuilder.eq("job", value.name()));
			findAllStmt.setFetchSize(FETCHSIZE);
			List<ProcessLog> list = QueryMapper.processLog().map(session.execute(findAllStmt)).all();
			if (list != null && list.size() > 0) {
				response.addAll(list);
			}
		}

		return response;
	}
}
