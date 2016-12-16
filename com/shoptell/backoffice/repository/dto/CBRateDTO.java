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
import java.util.Set;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author abhishekagarwal
 * cbrate table contains information for the cashback rates provided by the home(s). This table 
 * needs to be manually updated for every commission rates changes applied by the home(s).
 */

@Table(keyspace = "afferve", name = "cbrate")
public class CBRateDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	@PartitionKey
	private String home;
	@ClusteringColumn(value = 0)
	private String subCategory;
	private Set<String> tags;
	private double webCBRate;
	private double webCBRateNewUser;
	private double mobileCBRate;
	private double mobileCBRateNewUser;
	private double maxComission;
	private Date createdOn;
	
	public CBRateDTO() {
		createdOn = new Date(System.currentTimeMillis());
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
	 * @return the subCategory
	 */
	public String getSubCategory() {
		return subCategory;
	}
	/**
	 * @param subCategory the subCategory to set
	 */
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	/**
	 * @return the webCBRate
	 */
	public double getWebCBRate() {
		return webCBRate;
	}
	/**
	 * @param webCBRate the webCBRate to set
	 */
	public void setWebCBRate(double webCBRate) {
		this.webCBRate = webCBRate;
	}
	/**
	 * @return the webCBRateNewUser
	 */
	public double getWebCBRateNewUser() {
		return webCBRateNewUser;
	}
	/**
	 * @param webCBRateNewUser the webCBRateNewUser to set
	 */
	public void setWebCBRateNewUser(double webCBRateNewUser) {
		this.webCBRateNewUser = webCBRateNewUser;
	}
	/**
	 * @return the mobileCBRate
	 */
	public double getMobileCBRate() {
		return mobileCBRate;
	}
	/**
	 * @param mobileCBRate the mobileCBRate to set
	 */
	public void setMobileCBRate(double mobileCBRate) {
		this.mobileCBRate = mobileCBRate;
	}
	/**
	 * @return the mobileCBRateNewUser
	 */
	public double getMobileCBRateNewUser() {
		return mobileCBRateNewUser;
	}
	/**
	 * @param mobileCBRateNewUser the mobileCBRateNewUser to set
	 */
	public void setMobileCBRateNewUser(double mobileCBRateNewUser) {
		this.mobileCBRateNewUser = mobileCBRateNewUser;
	}
	/**
	 * @return the maxComission
	 */
	public double getMaxComission() {
		return maxComission;
	}
	/**
	 * @param maxComission the maxComission to set
	 */
	public void setMaxComission(double maxComission) {
		this.maxComission = maxComission;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxComission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mobileCBRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mobileCBRateNewUser);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		temp = Double.doubleToLongBits(webCBRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(webCBRateNewUser);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CBRateDTO other = (CBRateDTO) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		}
		else if (!home.equals(other.home))
			return false;
		if (Double.doubleToLongBits(maxComission) != Double.doubleToLongBits(other.maxComission))
			return false;
		if (Double.doubleToLongBits(mobileCBRate) != Double.doubleToLongBits(other.mobileCBRate))
			return false;
		if (Double.doubleToLongBits(mobileCBRateNewUser) != Double.doubleToLongBits(other.mobileCBRateNewUser))
			return false;
		if (subCategory == null) {
			if (other.subCategory != null)
				return false;
		}
		else if (!subCategory.equals(other.subCategory))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		}
		else if (!tags.equals(other.tags))
			return false;
		if (Double.doubleToLongBits(webCBRate) != Double.doubleToLongBits(other.webCBRate))
			return false;
		if (Double.doubleToLongBits(webCBRateNewUser) != Double.doubleToLongBits(other.webCBRateNewUser))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CBRateDTO [home=" + home + ", subCategory=" + subCategory + ", tags=" + tags + ", webCBRate=" + webCBRate + ", webCBRateNewUser="
				+ webCBRateNewUser + ", mobileCBRate=" + mobileCBRate + ", mobileCBRateNewUser=" + mobileCBRateNewUser + ", maxComission=" + maxComission
				+ ", createdOn=" + createdOn + "]";
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
	
}
