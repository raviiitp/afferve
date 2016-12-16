/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;
import com.shoptell.backoffice.enums.TicketStatusEnum;

/**
 * @author abhishekagarwal
 * All Tickets Raised are persisted in the table
 */
@Table(keyspace = "afferve", name = "ticket")
public class TicketDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ClusteringColumn
	private UUID time;
	@PartitionKey
	private String ticketId;
	private String userId;
	private String status;
	private Date closeDate;
	private String firstMessage;
	@Transient
	private List<TicketMessageDTO> messages;
	@Transient
	private Date date;
	
	public TicketDTO() {
		super();
		this.time = UUIDs.timeBased();
		this.ticketId = UUID.randomUUID().toString();
		this.status = TicketStatusEnum.OPEN.name();
	}
	
	/**
	 * @return the time
	 */
	public UUID getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(UUID time) {
		this.time = time;
	}
	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}
	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the closeDate
	 */
	public Date getCloseDate() {
		return closeDate;
	}
	/**
	 * @param closeDate the closeDate to set
	 */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	public String getFirstMessage() {
		return firstMessage;
	}

	public void setFirstMessage(String firstMessage) {
		this.firstMessage = firstMessage;
	}

	public Date getDate() {
		if (this.time != null){
			this.date = new Date(UUIDs.unixTimestamp(time));
		}
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((closeDate == null) ? 0 : closeDate.hashCode());
		result = prime * result + ((firstMessage == null) ? 0 : firstMessage.hashCode());
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((ticketId == null) ? 0 : ticketId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketDTO other = (TicketDTO) obj;
		if (closeDate == null) {
			if (other.closeDate != null)
				return false;
		} else if (!closeDate.equals(other.closeDate))
			return false;
		if (firstMessage == null) {
			if (other.firstMessage != null)
				return false;
		} else if (!firstMessage.equals(other.firstMessage))
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (ticketId == null) {
			if (other.ticketId != null)
				return false;
		} else if (!ticketId.equals(other.ticketId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TicketDTO [time=" + time + ", ticketId=" + ticketId + ", userId=" + userId + ", status=" + status + ", closeDate=" + closeDate
				+ ", firstMessage=" + firstMessage + ", messages=" + messages + ", date=" + date + "]";
	}

	/**
	 * @return the messages
	 */
	public List<TicketMessageDTO> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<TicketMessageDTO> messages) {
		this.messages = messages;
	}
	
}
