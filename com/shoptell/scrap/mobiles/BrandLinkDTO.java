/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.scrap.mobiles;

import java.io.Serializable;
import java.util.Date;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "brand_link")
public class BrandLinkDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	@PartitionKey
	private String brand;
	@ClusteringColumn(value = 0)
	private String subCategoryName;
	private boolean enable;
	private String link;
	private int count;
	private int scrapCount;
	private Date modifiedOn;
	
	public BrandLinkDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public BrandLinkDTO(String name, String subCategoryName, String url, int count) {
		this.brand = name.trim().toUpperCase();
		this.subCategoryName = subCategoryName.trim();
		this.link = url.trim();
		this.count = count;
		this.scrapCount = 0;
		this.enable = true;
		this.modifiedOn = new Date(System.currentTimeMillis());
	}
	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/**
	 * @return the subCategoryName
	 */
	public String getSubCategoryName() {
		return subCategoryName;
	}
	/**
	 * @param subCategoryName the subCategoryName to set
	 */
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	/**
	 * @return the enable
	 */
	public boolean isEnable() {
		return enable;
	}
	/**
	 * @param enable the enable to set
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the scrapCount
	 */
	public int getScrapCount() {
		return scrapCount;
	}
	/**
	 * @param scrapCount the scrapCount to set
	 */
	public void setScrapCount(int scrapCount) {
		this.scrapCount = scrapCount;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + count;
		result = prime * result + (enable ? 1231 : 1237);
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + scrapCount;
		result = prime * result + ((subCategoryName == null) ? 0 : subCategoryName.hashCode());
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
		BrandLinkDTO other = (BrandLinkDTO) obj;
		if (brand == null) {
			if (other.brand != null)
				return false;
		}
		else if (!brand.equals(other.brand))
			return false;
		if (count != other.count)
			return false;
		if (enable != other.enable)
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		}
		else if (!link.equals(other.link))
			return false;
		if (scrapCount != other.scrapCount)
			return false;
		if (subCategoryName == null) {
			if (other.subCategoryName != null)
				return false;
		}
		else if (!subCategoryName.equals(other.subCategoryName))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BrandLinkDTO [brand=" + brand + ", subCategoryName=" + subCategoryName + ", enable=" + enable + ", link=" + link + ", count=" + count
				+ ", scrapCount=" + scrapCount + "]";
	}
	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}
	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
}
