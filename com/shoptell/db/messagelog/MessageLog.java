/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.messagelog;

import java.util.Date;
import java.util.UUID;

import javax.inject.Named;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author abhishekagarwal
 *
 */
@Table(keyspace = "afferve", name = "message_log")
public class MessageLog {

	@PartitionKey
	@Named (value = "message_log_id")
	private UUID message_log_id;
	
	private String severity;

	private Date time;
	
	private String description;
	
	private String message;
	
	public MessageLog(){
		
	}

	public MessageLog(String severity, Date time, String description, String message){
		this.message_log_id = UUID.randomUUID();
		this.severity = severity;
		this.time = time;
		this.description = description;
		this.message = message;
	}

	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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

	/**
	 * @return the uuid
	 */
	public UUID getMessage_log_id() {
		return message_log_id;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setMessage_log_id(UUID uuid) {
		this.message_log_id = uuid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((severity == null) ? 0 : severity.hashCode());
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
		MessageLog other = (MessageLog) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (severity == null) {
			if (other.severity != null)
				return false;
		}
		else if (!severity.equals(other.severity))
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
		return "MessageLog [uuid=" + message_log_id + ", severity=" + severity + ", time=" + time + ", description=" + description + ", message=" + message + "]";
	}
	
}
