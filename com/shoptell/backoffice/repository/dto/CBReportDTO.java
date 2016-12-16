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

import static com.shoptell.backoffice.BackofficeConstants.MIN_CUT_AMOUNT;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CBStatusEnum;

/**
 * @author abhishekagarwal CBReportDTO contains info about the cashback tracked
 *         by the home(s)
 */
@Table(keyspace = "afferve", name = "cbreport")
public class CBReportDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ClusteringColumn
	private UUID time;
	@PartitionKey
	private String userId;
	private String home;
	private String transactionNumber;
	private double amount;
	private String cashBackAmount;
	private String status;
	private Date expectedConfirmationDate;
	private String description;
	private Date modifiedOn;
	private String product;
	private boolean bonus;
	private UUID myBonus;
	private UUID referBonus;
	private boolean user;
	private boolean doNotMail;
	@Transient
	private Date date;

	public CBReportDTO() {
		super();
		this.time = UUIDs.timeBased();
	}
	
	public CBReportDTO(String userId, UUID time, CBReportDTO prod, String cashBackAmount, boolean user) {
		super();
		this.time = time;
		this.userId = userId;
		this.user = user;
		this.home = prod.getHome();
		this.transactionNumber = prod.getTransactionNumber();
		this.amount = 0;
		this.cashBackAmount = cashBackAmount;
		this.status = prod.getStatus();
		this.product = prod.getProduct();
		this.bonus = true;
		if (this.status.equals(CBStatusEnum.PENDING.name())){
			this.expectedConfirmationDate = BackofficeUtil.getExpectedCBDate();
		}else {
			this.expectedConfirmationDate = new Date(System.currentTimeMillis());
		}
		this.modifiedOn = new Date(System.currentTimeMillis());
		this.doNotMail = prod.isDoNotMail();
	}

	public CBReportDTO(String userId, UUID time, String status, String product, String home) {
		this.time = time;
		this.userId = userId;
		this.home = home;
		this.status = status;
		this.product = product;
	}

	public UUID getTime() {
		return time;
	}

	public void setTime(UUID time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getCashBackAmount() {
		return cashBackAmount;
	}

	public void setCashBackAmount(String cashBackAmount) {
		this.cashBackAmount = cashBackAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getExpectedConfirmationDate() {
		return expectedConfirmationDate;
	}

	public void setExpectedConfirmationDate(Date expectedConfirmationDate) {
		this.expectedConfirmationDate = expectedConfirmationDate;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (bonus ? 1231 : 1237);
		result = prime * result + ((cashBackAmount == null) ? 0 : cashBackAmount.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((expectedConfirmationDate == null) ? 0 : expectedConfirmationDate.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		result = prime * result + ((myBonus == null) ? 0 : myBonus.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((referBonus == null) ? 0 : referBonus.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((transactionNumber == null) ? 0 : transactionNumber.hashCode());
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
		CBReportDTO other = (CBReportDTO) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (bonus != other.bonus)
			return false;
		if (cashBackAmount == null) {
			if (other.cashBackAmount != null)
				return false;
		}
		else if (!cashBackAmount.equals(other.cashBackAmount))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (expectedConfirmationDate == null) {
			if (other.expectedConfirmationDate != null)
				return false;
		}
		else if (!expectedConfirmationDate.equals(other.expectedConfirmationDate))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		}
		else if (!home.equals(other.home))
			return false;
		if (modifiedOn == null) {
			if (other.modifiedOn != null)
				return false;
		}
		else if (!modifiedOn.equals(other.modifiedOn))
			return false;
		if (myBonus == null) {
			if (other.myBonus != null)
				return false;
		}
		else if (!myBonus.equals(other.myBonus))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		}
		else if (!product.equals(other.product))
			return false;
		if (referBonus == null) {
			if (other.referBonus != null)
				return false;
		}
		else if (!referBonus.equals(other.referBonus))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		if (transactionNumber == null) {
			if (other.transactionNumber != null)
				return false;
		}
		else if (!transactionNumber.equals(other.transactionNumber))
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
		return "CBReportDTO [time=" + time + ", userId=" + userId + ", home=" + home + ", transactionNumber=" + transactionNumber + ", amount=" + amount
				+ ", cashBackAmount=" + cashBackAmount + ", status=" + status + ", expectedConfirmationDate=" + expectedConfirmationDate + ", description="
				+ description + ", modifiedOn=" + modifiedOn + ", product=" + product + ", bonus=" + bonus + ", myBonus=" + myBonus + ", referBonus="
				+ referBonus + ", date=" + date + "]";
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return the bonus
	 */
	public boolean isBonus() {
		return bonus;
	}

	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(boolean bonus) {
		this.bonus = bonus;
	}

	/**
	 * @return the myBonus
	 */
	public UUID getMyBonus() {
		return myBonus;
	}

	/**
	 * @param myBonus the myBonus to set
	 */
	public void setMyBonus(UUID myBonus) {
		this.myBonus = myBonus;
	}

	/**
	 * @return the referBonus
	 */
	public UUID getReferBonus() {
		return referBonus;
	}

	/**
	 * @param referBonus the referBonus to set
	 */
	public void setReferBonus(UUID referBonus) {
		this.referBonus = referBonus;
	}

	public boolean isEligibleForCut() {
		return this.getAmount() > MIN_CUT_AMOUNT;
	}

	public boolean isActive() {
		return this.getStatus().equals(CBStatusEnum.PENDING.name()) || this.getStatus().equals(CBStatusEnum.RECEIVED.name());
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

	/**
	 * @return the doNotMail
	 */
	public boolean isDoNotMail() {
		return doNotMail;
	}

	/**
	 * @param doNotMail the doNotMail to set
	 */
	public void setDoNotMail(boolean doNotMail) {
		this.doNotMail = doNotMail;
	}
	
	}
