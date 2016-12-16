package com.shoptell.domain;

import java.io.Serializable;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "userNotification")
public class UserNotificationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PartitionKey
    private String userId; //afferve user id
	
	private UUID myMoneyLastVisit;

	
	
	public UserNotificationDTO() {
		super();
		this.myMoneyLastVisit = UUIDs.timeBased();
	}

	public UserNotificationDTO(String userId) {
		super();
		this.userId = userId;
		this.myMoneyLastVisit = UUIDs.timeBased();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UUID getMyMoneyLastVisit() {
		return myMoneyLastVisit;
	}

	public void setMyMoneyLastVisit(UUID myMoneyLastVisit) {
		this.myMoneyLastVisit = myMoneyLastVisit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((myMoneyLastVisit == null) ? 0 : myMoneyLastVisit.hashCode());
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
		UserNotificationDTO other = (UserNotificationDTO) obj;
		if (myMoneyLastVisit == null) {
			if (other.myMoneyLastVisit != null)
				return false;
		} else if (!myMoneyLastVisit.equals(other.myMoneyLastVisit))
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
		return "UserNotificationDTO [userId=" + userId + ", myMoneyLastVisit=" + myMoneyLastVisit + "]";
	}
	
	
	
}
