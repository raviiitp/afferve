/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.dto;

import java.io.Serializable;
import java.util.UUID;

import org.apache.cassandra.utils.UUIDGen;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "sent_mail")
public class SentMailDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@PartitionKey
	private UUID time;
	private String email;
	private String subject;
	private String content;
	private String status;
	
	public SentMailDTO() {
		super();
		this.setTime(UUIDGen.getTimeUUID(System.currentTimeMillis()));
	}

	public SentMailDTO(String email, String subject, String content, String status) {
		super();
		this.setTime(UUIDGen.getTimeUUID(System.currentTimeMillis()));
		this.email = email;
		this.subject = subject;
		this.content = null; //skip mail content
		this.status = status;
	}

	/**
	 * @return the to
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param to the to to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
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
	
}
