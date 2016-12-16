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
import java.sql.Date;
import java.util.UUID;

import org.apache.cassandra.utils.UUIDGen;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author abhishekagarwal
 * UserTransactionDTO have info about the user clicking on any of the home(s) urls.
 */
@Table(keyspace = "afferve", name = "usertransactions")
public class UserTransactionDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private UUID time;
	@PartitionKey
	private String transactionId;
	private String userId;
	private String trackingId;
	private String trackingSubId;
	private String url;
	private String home;
	
	
	public UserTransactionDTO() {
		super();
		this.time = UUIDGen.getTimeUUID(System.currentTimeMillis());
		this.transactionId = UUID.randomUUID().toString();
	}
	public UserTransactionDTO(String userId, String home) {
		super();
		this.time = UUIDGen.getTimeUUID(System.currentTimeMillis());
		this.transactionId = UUID.randomUUID().toString();
		this.userId = userId;
		this.home = home;
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
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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
	 * @return the trackingId
	 */
	public String getTrackingId() {
		return trackingId;
	}
	/**
	 * @param trackingId the trackingId to set
	 */
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	/**
	 * @return the trackingSubId
	 */
	public String getTrackingSubId() {
		return trackingSubId;
	}
	/**
	 * @param trackingSubId the trackingSubId to set
	 */
	public void setTrackingSubId(String trackingSubId) {
		this.trackingSubId = trackingSubId;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((trackingId == null) ? 0 : trackingId.hashCode());
		result = prime * result + ((trackingSubId == null) ? 0 : trackingSubId.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		UserTransactionDTO other = (UserTransactionDTO) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		if (trackingId == null) {
			if (other.trackingId != null)
				return false;
		}
		else if (!trackingId.equals(other.trackingId))
			return false;
		if (trackingSubId == null) {
			if (other.trackingSubId != null)
				return false;
		}
		else if (!trackingSubId.equals(other.trackingSubId))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		}
		else if (!transactionId.equals(other.transactionId))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		}
		else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionDTO [time=" + time + ", transactionId=" + transactionId + ", userId=" + userId + ", trackingId=" + trackingId + ", trackingSubId="
				+ trackingSubId + ", url=" + url + "]";
	}
	/**
	 * @return the home
	 */
	public String getHome() {
		return home;
	}
	/**
	 * @param home the home to set
	 */
	public void setHome(String home) {
		this.home = home;
	}

	public Date date() {
		long date = UUIDs.unixTimestamp(time);
		return new Date(date);
	}
}
