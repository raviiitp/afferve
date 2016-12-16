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
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

/**
 * @author abhishekagarwal
 * Messages corresponding to ticket are added to ticketMessage table
 */
@Table(keyspace = "afferve", name = "ticketMessage")
public class TicketMessageDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ClusteringColumn
	private UUID time;
	@PartitionKey
	private String messageId;
	private String ticketId;
	private String message;
	private boolean user; // If true means the message is written by the User else by Afferve Team
	@Transient
	private Date date;
	
	public TicketMessageDTO() {
		super();
		this.time = UUIDs.timeBased();
		this.messageId = UUID.randomUUID().toString();
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
	public String getMessageId() {
		return messageId;
	}
	/**
	 * @param ticketId the ticketId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TicketMessageDTO other = (TicketMessageDTO) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		}
		else if (!messageId.equals(other.messageId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TicketMessageDTO [time=" + time + ", messageId=" + messageId + ", message=" + message + "]";
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
	 * @return the user
	 */
	public boolean isUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(boolean user) {
		this.user = user;
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
	
}
