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

import static com.shoptell.backoffice.enums.TableEnum.ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.enums.TicketStatusEnum;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.UpdateQuery;
import com.shoptell.backoffice.repository.dto.TicketDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class TicketService {

	@Inject
	private Session session;

	@Inject
	private TicketMessageService messageProvider;

	@Inject
	private SelectQuery selectQuery;
	
	@Inject
	private UpdateQuery updateQuery;

	private TableEnum tableName;

	@PostConstruct
	public void start() {
		tableName = ticket;
	}

	public List<TicketDTO> getOpenTickets() {
		List<TicketDTO> results = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", TicketStatusEnum.OPEN.name());

		ResultSet rs = selectQuery.selectAll(tableName, map);

		if (rs != null) {
			results = QueryMapper.ticketDTO().map(rs).all();
		}
		return results;
	}

	public List<TicketDTO> getTicketsForUser(String userId) {
		List<TicketDTO> results = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);

		ResultSet rs = selectQuery.selectAll(tableName, map);

		if (rs != null) {
			results = QueryMapper.ticketDTO().map(rs).all();
		}
		return results;
	}

	public TicketDTO getTicketWithId(String ticketId) {
		TicketDTO results = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ticketId", ticketId);

		ResultSet rs = selectQuery.selectAll(tableName, map);

		if (rs != null) {
			results = QueryMapper.ticketDTO().map(rs).one();
		}
		return results;
	}

	public List<TicketDTO> getTicketsWithMessages(String userId) {
		List<TicketDTO> list = getTicketsForUser(userId);
		for (TicketDTO ticket : list) {
			ticket.setMessages(messageProvider.getMessages(ticket.getTicketId()));
		}
		return list;
	}

	public String openTicket(String userId, String message) {
		List<TicketDTO> ticketDTOList = getTicketsForUser(userId);
		int numOpenTickets = 0;
		for (TicketDTO ticketDTO : ticketDTOList) {
			if (ticketDTO.getStatus().equals(TicketStatusEnum.OPEN.name())) {
				numOpenTickets++;
			}
		}
		if (numOpenTickets > 3) {
			return null;
		}

		TicketDTO ticketDTO = new TicketDTO();
		ticketDTO.setCloseDate(new Date());
		ticketDTO.setFirstMessage(message);
		ticketDTO.setUserId(userId);
		QueryMapper.ticketDTO().save(ticketDTO);
		return ticketDTO.getTicketId();
	}

	public void closeTicket(String ticketId, UUID time, boolean isUser) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("status", TicketStatusEnum.CLOSED.name());
		values.put("closeDate", new Date(System.currentTimeMillis()));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ticketId", ticketId);
		map.put("time", time);
		
		updateQuery.updateQuery(tableName, values, map);
		messageProvider.sendMail(ticketId);
	}

	public void mergeTicketTable(String userId, String finalUserId) {
		BatchStatement batch = new BatchStatement();

		List<TicketDTO> ticketDTOList = getTicketsForUser(userId);

		for (TicketDTO ticketDTO : ticketDTOList) {
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("userId", finalUserId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ticketId", ticketDTO.getTicketId());
			map.put("time", ticketDTO.getTime());
			batch.add(updateQuery.updateQueryStatement(tableName, values, map));
		}
		if (ticketDTOList.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
	}

	public String getUser(String ticketId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ticketid", ticketId);

		ResultSet rs = selectQuery.selectColumns(tableName, map, "userid");
		if (rs != null) {
			Row row = rs.one();
			return row.getString("userid");
		}
		return null;
	}

	/**
	 * Automatic Ticket Close 
	 * On no-reply by the user in last 10 days
	 * Close will happen on weekly basis
	 * through cronservice
	 */
	public void autoClose() {
		List<TicketDTO> tickets = getOpenTickets();
		if (tickets != null && tickets.size() > 0){
			for (TicketDTO ticket : tickets){
				if (messageProvider.isMessageExpired(ticket.getTicketId())){
					closeTicket(ticket.getTicketId(), ticket.getTime(), false);
				}
			}
		}
		
	}
}
