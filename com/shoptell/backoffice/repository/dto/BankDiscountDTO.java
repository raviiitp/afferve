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

import static com.shoptell.backoffice.enums.CategoryEnum.ALL;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

/**
 * @author abhishekagarwal
 *
 */
@Table(keyspace = "afferve", name = "bank_discounts")
public class BankDiscountDTO implements Discount,Serializable{
	private static final long serialVersionUID = 1L;
	@PartitionKey
	private String home;
	@ClusteringColumn(value = 1)
	private String subCategory; //all products
	@ClusteringColumn(value = 2)
	private String bank;
	private String url;
	private String image;
	private boolean creditcard;
	private boolean debitcard;
	private boolean netbanking;
	private boolean appOnly;
	private boolean active;
	private boolean emi;
	private Date startDate;
	@ClusteringColumn(value = 0)
	private Date endDate;
	private Date createdOn;
	private double minBuyAmount;
	private double maxDiscount;
	private double rate;
	private String code;
	private String type; //off,cashback
	@Transient
	private String detail;
	
	public BankDiscountDTO() {
		this.createdOn = new Date(System.currentTimeMillis());
		this.subCategory = ALL.name();
	}
	
	@PostConstruct
	public void postConstruct(){
		this.detail = populateDetails();
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
	 * @return the bank
	 */
	public String getBank() {
		return bank;
	}
	/**
	 * @param bank the bank to set
	 */
	public void setBank(String bank) {
		this.bank = bank;
	}
	/**
	 * @return the category
	 */
	public String getSubCategory() {
		return subCategory;
	}
	/**
	 * @param category the category to set
	 */
	public void setSubCategory(String category) {
		this.subCategory = category;
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
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the creditcard
	 */
	public boolean isCreditcard() {
		return creditcard;
	}
	/**
	 * @param creditcard the creditcard to set
	 */
	public void setCreditcard(boolean creditcard) {
		this.creditcard = creditcard;
	}
	/**
	 * @return the debitcard
	 */
	public boolean isDebitcard() {
		return debitcard;
	}
	/**
	 * @param debitcard the debitcard to set
	 */
	public void setDebitcard(boolean debitcard) {
		this.debitcard = debitcard;
	}
	/**
	 * @return the netbanking
	 */
	public boolean isNetbanking() {
		return netbanking;
	}
	/**
	 * @param netbanking the netbanking to set
	 */
	public void setNetbanking(boolean netbanking) {
		this.netbanking = netbanking;
	}
	/**
	 * @return the appOnly
	 */
	public boolean isAppOnly() {
		return appOnly;
	}
	/**
	 * @param appOnly the appOnly to set
	 */
	public void setAppOnly(boolean appOnly) {
		this.appOnly = appOnly;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the minBuyAmount
	 */
	public double getMinBuyAmount() {
		return minBuyAmount;
	}
	/**
	 * @param minBuyAmount the minBuyAmount to set
	 */
	public void setMinBuyAmount(double minBuyAmount) {
		this.minBuyAmount = minBuyAmount;
	}
	/**
	 * @return the maxDiscount
	 */
	public double getMaxDiscount() {
		return maxDiscount;
	}
	/**
	 * @param maxDiscount the maxDiscount to set
	 */
	public void setMaxDiscount(double maxDiscount) {
		this.maxDiscount = maxDiscount;
	}
	/**
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (appOnly ? 1231 : 1237);
		result = prime * result + ((bank == null) ? 0 : bank.hashCode());
		result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
		result = prime * result + (creditcard ? 1231 : 1237);
		result = prime * result + (debitcard ? 1231 : 1237);
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxDiscount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minBuyAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (netbanking ? 1231 : 1237);
		temp = Double.doubleToLongBits(rate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		BankDiscountDTO other = (BankDiscountDTO) obj;
		if (appOnly != other.appOnly)
			return false;
		if (bank == null) {
			if (other.bank != null)
				return false;
		}
		else if (!bank.equals(other.bank))
			return false;
		if (subCategory == null) {
			if (other.subCategory != null)
				return false;
		}
		else if (!subCategory.equals(other.subCategory))
			return false;
		if (creditcard != other.creditcard)
			return false;
		if (debitcard != other.debitcard)
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		}
		else if (!endDate.equals(other.endDate))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		}
		else if (!home.equals(other.home))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		}
		else if (!image.equals(other.image))
			return false;
		if (Double.doubleToLongBits(maxDiscount) != Double.doubleToLongBits(other.maxDiscount))
			return false;
		if (Double.doubleToLongBits(minBuyAmount) != Double.doubleToLongBits(other.minBuyAmount))
			return false;
		if (netbanking != other.netbanking)
			return false;
		if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		}
		else if (!startDate.equals(other.startDate))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BankDiscountDTO [home=" + home + ", bank=" + bank + ", category=" + subCategory + ", url=" + url + ", image=" + image + ", creditcard="
				+ creditcard + ", debitcard=" + debitcard + ", netbanking=" + netbanking + ", appOnly=" + appOnly + ", startDate=" + startDate + ", endDate="
				+ endDate + ", minBuyAmount=" + minBuyAmount + ", maxDiscount=" + maxDiscount + ", rate=" + rate + "]";
	}
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the detail
	 */
	public String getDetail() {
		setDetail(detail);
		return detail;
	}

	private String populateDetails() {
		StringBuilder sb = new StringBuilder();
		if (rate > 0)
			sb.append(rate).append("%").append(" ");
		if (StringUtils.isNotBlank(type))
			sb.append(type).append(" ").append("ON").append(" ");
		if (StringUtils.isNotBlank(subCategory)){
			sb.append(subCategory).append(" ");
			if (ALL.name().equalsIgnoreCase(subCategory)){
				sb.append("PRODUCTS").append(" ");
			}
		}
		if (StringUtils.isNotBlank(bank))
			sb.append("USING").append(" ").append(bank);
		if (netbanking) {
			sb.append(" ").append("NET BANKING").append(",");
		}
		if (creditcard) {
			sb.append(" ").append("CREDIT CARD").append(",");
		}
		if (debitcard) {
			sb.append(" ").append("DEBIT CARD").append(",");
		}
		if (emi) {
			sb.append(" ").append("EMI OPTIONS").append(",");
		}

		String desc = sb.toString();
		if (StringUtils.isNotBlank(desc)) {
			int indx = desc.lastIndexOf(",");
			if (indx != -1) {
				sb = sb.delete(indx,sb.length());
				desc = sb.toString();
			}
			indx = desc.lastIndexOf(",");
			if (indx != -1) {
				sb = sb.replace(indx, indx + 1, " AND ");
			}
		}
		
		if (appOnly) {
			sb.append(", AVAILABLE ONLY ON THE APP");
		}

		return sb.toString().trim()+".".toUpperCase();
	}
	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = populateDetails();
		//this.detail = detail;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the emi
	 */
	public boolean isEmi() {
		return emi;
	}

	/**
	 * @param emi the emi to set
	 */
	public void setEmi(boolean emi) {
		this.emi = emi;
	}
	
}
