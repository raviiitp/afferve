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

import static com.shoptell.backoffice.BackofficeConstants.ONE_DAY;
import static com.shoptell.backoffice.BackofficeConstants.ONE_HOUR;
import static com.shoptell.backoffice.enums.TableEnum.newticket;
import static com.shoptell.backoffice.enums.TableEnum.ticketMessage;
import static com.shoptell.backoffice.enums.TicketStatusEnum.CLOSED;
import static com.shoptell.backoffice.repository.QueryOperations.LTE;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.DeleteQuery;
import com.shoptell.backoffice.repository.InsertQuery;
import com.shoptell.backoffice.repository.QueryOperations;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.service.MailService;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class TicketMessageService {

	@Inject
	private Session session;

	@Inject
	private InsertQuery insertQuery;

	@Inject
	private DeleteQuery deleteQuery;

	@Inject
	private TicketService ticketService;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private MailService mail;

	private Mapper<TicketMessageDTO> mapper;

	private TableEnum tableName;

	@PostConstruct
	public void start() {
		mapper = new MappingManager(session).mapper(TicketMessageDTO.class);
		tableName = ticketMessage;
	}

	public List<TicketMessageDTO> getMessages(String ticketId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ticketId", ticketId);
		ResultSet rs = selectQuery.selectAll(tableName, map);
		if (rs != null) {
			List<TicketMessageDTO> results = mapper.map(rs).all();
			Collections.sort(results, BackofficeUtil.compareTicket);
			return results;
		}
		return null;
	}

	public void saveTicket(String userId, String ticketId, boolean isUser, String message) {
		if (StringUtils.equalsIgnoreCase(ticketId, "null")) {
			openTicket(userId, isUser, message);
		}
		else {
			addNewMessageInTicket(userId, ticketId, isUser, message);
		}
	}

	private void addNewMessageInTicket(String userId, String ticketId, boolean isUser, String message) {
		TicketMessageDTO ticketMessageDTO = new TicketMessageDTO();
		ticketMessageDTO.setMessage(message);
		ticketMessageDTO.setTicketId(ticketId);
		ticketMessageDTO.setUser(isUser);
		mapper.save(ticketMessageDTO);

		sendMail(ticketId);
	}

	public void openTicket(String userId, boolean isUser, String message) {
		String ticketId = ticketService.openTicket(userId, message);
		if (StringUtils.isNotBlank(ticketId)) {
			addNewMessageInTicket(userId, ticketId, isUser, message);
		}
	}

	public void sendMail(String ticketId) {
		if (StringUtils.isNotBlank(ticketId)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ticketId", ticketId);
			map.put("time", UUIDs.timeBased());
			insertQuery.insertQuery(newticket, map);
		}
	}

	public void sendMail() {
		Set<String> set = new HashSet<String>();
		UUID high = UUIDs.startOf(System.currentTimeMillis() - ONE_HOUR);
		String[] keys = { "time" };
		QueryOperations[] operations = { LTE };
		Object[] values = { high };
		ResultSet rs = selectQuery.selectWithOperations(newticket, true, keys, operations, values);
		Iterator<Row> itr = rs.iterator();
		while (itr.hasNext()) {
			Row row = itr.next();
			String ticketId = row.getString("ticketId");
			UUID time = row.getUUID("time");
			set.add(ticketId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ticketId", ticketId);
			map.put("time", time);
			deleteQuery.deleteQuery(newticket, map);
		}
		if (set.size() > 0) {
			for (String tmp : set) {
				sendTicketMail(tmp);
			}
		}
	}

	private void sendTicketMail(String ticketId) {
		if (StringUtils.isNotBlank(ticketId)) {
			List<TicketMessageDTO> msgs = getMessages(ticketId);
			if (msgs != null && msgs.size() > 0) {
				String userId = ticketService.getUser(ticketId);
				if (StringUtils.isNotBlank(userId)) {
					TicketDTO ticket = ticketService.getTicketWithId(ticketId);
					if (ticket != null) {
						TicketMessageDTO msg = msgs.get(msgs.size() - 1);
						mail.sendTicketMail(userId, msgs, msg.isUser(), CLOSED.name().equalsIgnoreCase(ticket.getStatus()));
					}
				}
			}
		}
	}

	/**
	 * @param ticketId
	 * @return
	 */
	public boolean isMessageExpired(String ticketId) {
		List<TicketMessageDTO> msgs = getMessages(ticketId);
		if (msgs != null && msgs.size() > 0) {
			TicketMessageDTO message = msgs.get(msgs.size() - 1);
			if (message.getDate().before(new Date(System.currentTimeMillis() - 10 * ONE_DAY)) && !message.isUser()) {
				return true;
			}
		}
		return false;
	}
}
