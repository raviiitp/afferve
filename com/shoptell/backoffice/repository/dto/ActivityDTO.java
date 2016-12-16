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
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace = "afferve", name = "activity")
public class ActivityDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Transient
	public static final String TYPE_PRODUCT = "PRODUCT";
	@Transient
	public static final String TYPE_URL = "URL";
	@Transient
	public static final String TYPE_HOME = "HOME";
	
	@ClusteringColumn
	private UUID time;
	private String name;
	@PartitionKey
	private String type;
	private String home;
	private String image;
	private String url;
	private String userId;
	private String userName;
	private String subCategory;
	private String productId;
	private String couponId;
	private Date createdOn;
	private String description;
	private Map<String, String> properties;
	
	@Transient
	private Date date;
	
	public ActivityDTO() {
		super();
		this.time = UUIDs.timeBased();
	}
	
	public ActivityDTO(String name, String type, String home, String image, String url, String userId, String userName, String subCategory, String productId,
			String couponId, String description, Map<String, String> properties) {
		super();
		this.time = UUIDs.timeBased();
		this.name = name;
		this.type = type;
		this.home = home;
		this.image = image;
		this.url = url;
		this.userId = userId;
		this.userName = userName;
		this.subCategory = subCategory;
		this.productId = productId;
		this.couponId = couponId;
		this.description = description;
		this.createdOn = new Date(System.currentTimeMillis());
		this.properties = properties;
	}
	
	public ActivityDTO(String type, String home, String url, String userId, String userName, String description) {
		super();
		this.time = UUIDs.timeBased();
		this.createdOn = new Date(System.currentTimeMillis());
		this.type = type;
		this.home = home;
		this.url = url;
		this.userId = userId;
		this.userName = userName;
		this.description = description;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the couponId
	 */
	public String getCouponId() {
		return couponId;
	}
	/**
	 * @param couponId the couponId to set
	 */
	public void setCouponId(String couponId) {
		this.couponId = couponId;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((couponId == null) ? 0 : couponId.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		ActivityDTO other = (ActivityDTO) obj;
		if (couponId == null) {
			if (other.couponId != null)
				return false;
		}
		else if (!couponId.equals(other.couponId))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
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
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		}
		else if (!productId.equals(other.productId))
			return false;
		if (subCategory == null) {
			if (other.subCategory != null)
				return false;
		}
		else if (!subCategory.equals(other.subCategory))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
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
		if (userName == null) {
			if (other.userName != null)
				return false;
		}
		else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ActivityDTO [time=" + time + ", name=" + name + ", type=" + type + ", home=" + home + ", image=" + image + ", url=" + url + ", userId="
				+ userId + ", userName=" + userName + ", subCategory=" + subCategory + ", productId=" + productId + ", couponId=" + couponId + ", createdOn="
				+ createdOn + ", description=" + description + "]";
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		if (this.time != null){
			this.date = new Date(UUIDs.unixTimestamp(time));
		}
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
}
