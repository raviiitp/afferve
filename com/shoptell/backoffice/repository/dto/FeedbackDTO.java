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

import javax.validation.constraints.NotNull;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.shoptell.backoffice.enums.TicketStatusEnum;

@Table(keyspace = "afferve", name = "feedback")
public class FeedbackDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PartitionKey
	private UUID feedbackId;
	@NotNull
	private String email;
	private String mobileNumber;
	private String status;
	private Date closeDate;
	private String message;
	
	public FeedbackDTO() {
		super();
		this.feedbackId = UUIDs.timeBased();
		this.status = TicketStatusEnum.OPEN.name();
	}
	
	public FeedbackDTO(String email, String mobileNumber, String message) {
		super();
		this.feedbackId = UUIDs.timeBased();
		this.status = TicketStatusEnum.OPEN.name();
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.message = message;
		this.closeDate = new Date(System.currentTimeMillis());
	}

	public UUID getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(UUID feedbackId) {
		this.feedbackId = feedbackId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((closeDate == null) ? 0 : closeDate.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((feedbackId == null) ? 0 : feedbackId.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((mobileNumber == null) ? 0 : mobileNumber.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		FeedbackDTO other = (FeedbackDTO) obj;
		if (closeDate == null) {
			if (other.closeDate != null)
				return false;
		} else if (!closeDate.equals(other.closeDate))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (feedbackId == null) {
			if (other.feedbackId != null)
				return false;
		} else if (!feedbackId.equals(other.feedbackId))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (mobileNumber == null) {
			if (other.mobileNumber != null)
				return false;
		} else if (!mobileNumber.equals(other.mobileNumber))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FeedbackDTO [feedbackId=" + feedbackId + ", email=" + email + ", mobileNumber=" + mobileNumber + ", status=" + status + ", closeDate="
				+ closeDate + ", message=" + message + "]";
	}
	
}
