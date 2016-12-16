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

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author abhishekagarwal
 * 
 */

@Table(keyspace = "afferve", name = "category_node")
public class CategoryNodeDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PartitionKey
	private String categoryId;
	
	private String categoryName;
	
	private String parentId;
	
	private String searchIndex;
	
	@Column(name="isLeaf")
	private boolean leaf;
	
	@Column(name="isRoot")
	private boolean root;
	
	private String categoryUrl;
	
	private String home;
	
	private Date createdOn;
	
	private Date modifiedon;
	
	public CategoryNodeDTO(){
		this.createdOn = new Date(System.currentTimeMillis());
	}
	
	public CategoryNodeDTO(String categoryId, String categoryName, String parentId, String searchIndex, boolean isLeaf, boolean isRoot){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.parentId = parentId;
		this.searchIndex = searchIndex;
		this.leaf = isLeaf;
		this.root = isRoot;
		this.createdOn = new Date(System.currentTimeMillis());
	}
	
	public CategoryNodeDTO (String categoryId, String categoryName, String categoryUrl, String home){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryUrl = categoryUrl;
		this.home = home;
		this.createdOn = new Date(System.currentTimeMillis());
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSearchIndex() {
		return searchIndex;
	}

	public void setSearchIndex(String searchIndex) {
		this.searchIndex = searchIndex;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}
	/**
	 * @return the categoryUrl
	 */
	public String getCategoryUrl() {
		return categoryUrl;
	}
	/**
	 * @param categoryUrl the categoryUrl to set
	 */
	public void setCategoryUrl(String categoryUrl) {
		this.categoryUrl = categoryUrl;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CategoryNode [categoryId=" + categoryId + ", categoryName="
				+ categoryName + ", parentId=" + parentId + ", searchIndex="
				+ searchIndex + ", leaf=" + leaf + ", root=" + root
				+ ", categoryUrl=" + categoryUrl + ", home=" + home + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result
				+ ((categoryUrl == null) ? 0 : categoryUrl.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + (leaf ? 1231 : 1237);
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + (root ? 1231 : 1237);
		result = prime * result
				+ ((searchIndex == null) ? 0 : searchIndex.hashCode());
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
		CategoryNodeDTO other = (CategoryNodeDTO) obj;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (categoryUrl == null) {
			if (other.categoryUrl != null)
				return false;
		} else if (!categoryUrl.equals(other.categoryUrl))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		} else if (!home.equals(other.home))
			return false;
		if (leaf != other.leaf)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (root != other.root)
			return false;
		if (searchIndex == null) {
			if (other.searchIndex != null)
				return false;
		} else if (!searchIndex.equals(other.searchIndex))
			return false;
		return true;
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
	 * @return the modifiedon
	 */
	public Date getModifiedon() {
		return modifiedon;
	}

	/**
	 * @param modifiedon the modifiedon to set
	 */
	public void setModifiedon(Date modifiedon) {
		this.modifiedon = modifiedon;
	}
}