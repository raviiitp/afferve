/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.processlog;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author abhishekagarwal
 *
 */
@Table(keyspace = "afferve", name = "process_log")
public class ProcessLog implements Serializable{

	private static final long serialVersionUID = 1L;
	@ClusteringColumn (value = 1)
	private UUID time;
	@PartitionKey
	private String home;
	@ClusteringColumn (value = 0)
	private String job;
	private String status;
	private Date completedOn;
	private Date createdOn;
	
	public ProcessLog() {
		this.time = UUIDs.timeBased();
		this.createdOn = new Date(System.currentTimeMillis());
	}
	
	public ProcessLog(String home, String job, String status) {
		super();
		this.time = UUIDs.timeBased();
		this.home = home;
		this.job = job;
		this.status = status;
		this.createdOn = new Date(System.currentTimeMillis());
	}
	
	public ProcessLog(UUID id, String home, String job, String status) {
		super();
		this.time = id;
		this.home = home;
		this.job = job;
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
	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}
	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProcessLog [time=" + time + ", home=" + home + ", job=" + job + ", status=" + status + "]";
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the completedOn
	 */
	public Date getCompletedOn() {
		return completedOn;
	}

	/**
	 * @param completedOn the completedOn to set
	 */
	public void setCompletedOn(Date completedOn) {
		this.completedOn = completedOn;
	}
}
