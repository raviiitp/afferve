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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

/*
 * few fields should be same as fields of CompetitorFields class
 */
@Table(keyspace = "afferve", name = "merged_product_info")
public class MergedProductInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@PartitionKey
	private UUID id;
	private boolean disabled;
	private Date createdOn;
	private Date modifiedOn;
	private Set<String> tags;
	private String categoryName; // e.g. Mobiles
	private String metaCategory;
	private String model; // e.g. GT-I9500, iPhone
	private String productBrand; // e.g. Samsung, Apple
	private String productSubBrand; // e.g. Galaxy, iPhone
	private String series; // e.g. 5S, S5, 5C, A4
	private String subCategoryName; // e.g. CategoryNameEnum.SMARTPHONES.name()
	private boolean markForDelete;
	private double bestPrice;
	private double salesRank;
	@Transient
	private float relevanceRank;
	@Transient
	private Map<String, String> imageUrlMap;
	@Transient
	private List<String> features;
	@Transient
	private Map<UUID, ColorMapDTO> data;
	
	public void update(){
		generateRank();
		generateBestPrice();
	}
	
	public void generateRank(){
		if (data == null) return;
		double sum = 0;
		int count = 0;
		for (ColorMapDTO value : data.values()){
			float rank = value.generateRank();
			if (rank > 0){
				sum += rank;
				++count;
			}
		}
		if (count > 0){
			this.salesRank = sum/count;
		}
	}
	
	public void generateBestPrice() {
		if (data != null && data.size() > 0) {
			for (ColorMapDTO value : data.values()) {
				if (this.bestPrice == 0 || (value.getBestPrice() > 0 && this.bestPrice > value.getBestPrice())) {
					this.bestPrice = value.getBestPrice();
				}
			}
		}
	}
	
	/**
	 * @return the relevanceRank
	 */
	public float getRelevanceRank() {
		return relevanceRank;
	}

	/**
	 * @param relevanceRank the relevanceRank to set
	 */
	public void setRelevanceRank(float relevanceRank) {
		this.relevanceRank = relevanceRank;
	}

	public MergedProductInfoDTO() {
		this.id = UUID.randomUUID();
		this.createdOn = new Date();
		this.markForDelete = false;
	}

	public MergedProductInfoDTO(ReviewedProductInfoDTO competitorField) {
		this.id = UUID.randomUUID();
		this.createdOn = new Date();
		this.markForDelete = false;
		this.setMetaCategory(competitorField.getMetaCategory());
		this.setCategoryName(competitorField.getCategoryName());
		this.setSubCategoryName(competitorField.getSubCategoryName());
		this.setProductBrand(competitorField.getProductBrand());
		this.setProductSubBrand(competitorField.getProductSubBrand());
		this.setSeries(competitorField.getSeries());
		this.setModel(competitorField.getModel());
		this.setTags(this.createTags());
		HashMap<UUID, ColorMapDTO> newData = new HashMap<UUID, ColorMapDTO>();
		this.data = newData;
		
		this.update();
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the subCategoryName
	 */
	public String getSubCategoryName() {
		return subCategoryName;
	}

	/**
	 * @param subCategoryName
	 *            the subCategoryName to set
	 */
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	/**
	 * @return the productBrand
	 */
	public String getProductBrand() {
		return productBrand;
	}

	/**
	 * @param productBrand
	 *            the productBrand to set
	 */
	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	/**
	 * @return the productSubBrand
	 */
	public String getProductSubBrand() {
		return productSubBrand;
	}

	/**
	 * @param productSubBrand
	 *            the productSubBrand to set
	 */
	public void setProductSubBrand(String productSubBrand) {
		this.productSubBrand = productSubBrand;
	}

	/**
	 * @return the series
	 */
	public String getSeries() {
		return series;
	}

	/**
	 * @param series
	 *            the series to set
	 */
	public void setSeries(String series) {
		this.series = series;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the data
	 */
	public Map<UUID, ColorMapDTO> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<UUID, ColorMapDTO> data) {
		this.data = data;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn
	 *            the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getName() {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(productBrand)) {
			sb.append(productBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(productSubBrand)) {
			sb.append(productSubBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(series)) {
			sb.append(series.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(model)) {
			sb.append(model.trim()).append(" ");
		}
		if (sb.length() > 0) {
			return sb.toString().trim();
		}
		return null;
	}

	public Set<String> createTags() {
		Set<String> __tmpTags = null;
		String __tmpName = getName();
		if (__tmpName != null) {
			__tmpTags = new HashSet<>();
			String[] tokens = getName().split(" ");
			for (String token : tokens) {
				if (!token.isEmpty()) {
					__tmpTags.add(token.toLowerCase());
				}
			}
		}
		return __tmpTags;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		result = prime * result + ((productBrand == null) ? 0 : productBrand.hashCode());
		result = prime * result + ((productSubBrand == null) ? 0 : productSubBrand.hashCode());
		result = prime * result + ((series == null) ? 0 : series.hashCode());
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
		MergedProductInfoDTO other = (MergedProductInfoDTO) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		}
		else if (!categoryName.equals(other.categoryName))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		}
		else if (!model.equals(other.model))
			return false;
		if (modifiedOn == null) {
			if (other.modifiedOn != null)
				return false;
		}
		else if (!modifiedOn.equals(other.modifiedOn))
			return false;
		if (productBrand == null) {
			if (other.productBrand != null)
				return false;
		}
		else if (!productBrand.equals(other.productBrand))
			return false;
		if (productSubBrand == null) {
			if (other.productSubBrand != null)
				return false;
		}
		else if (!productSubBrand.equals(other.productSubBrand))
			return false;
		if (series == null) {
			if (other.series != null)
				return false;
		}
		else if (!series.equals(other.series))
			return false;
		if (subCategoryName == null) {
			if (other.subCategoryName != null)
				return false;
		}
		else if (!subCategoryName.equals(other.subCategoryName))
			return false;
		return true;
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
	 * @return the metaCategory
	 */
	public String getMetaCategory() {
		return metaCategory;
	}

	/**
	 * @param metaCategory the metaCategory to set
	 */
	public void setMetaCategory(String metaCategory) {
		this.metaCategory = metaCategory;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MergedProductInfoDTO [id=" + id + ", disabled=" + disabled + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", tags=" + tags
				+ ", categoryName=" + categoryName + ", metaCategory=" + metaCategory + ", model=" + model /*+ ", primaryProperty=" + primaryProperty*/
				+ ", productBrand=" + productBrand + ", productSubBrand=" + productSubBrand /*+ ", secondaryProperty=" + secondaryProperty*/ + ", series=" + series
				+ ", subCategoryName=" + subCategoryName /*+ ", properties=" + properties*/ + ", data=" + data 
				+ ", relevanceRank=" + relevanceRank + ", imageUrlMap=" + imageUrlMap + "]";
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param imageUrlMap the imageUrlMap to set
	 */
	public void setImageUrlMap(Map<String, String> imageUrlMap) {
		this.imageUrlMap = imageUrlMap;
	}

	public Map<String, String> getImageUrlMap() {
		return imageUrlMap;
	}

	/**
	 * @return the features
	 */
	public List<String> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<String> features) {
		this.features = features;
	}

	/**
	 * @return the markForDelete
	 */
	public boolean isMarkForDelete() {
		return markForDelete;
	}

	/**
	 * @param markForDelete the markForDelete to set
	 */
	public void setMarkForDelete(boolean markForDelete) {
		this.markForDelete = markForDelete;
	}

	/**
	 * @return the bestPrice
	 */
	public double getBestPrice() {
		return bestPrice;
	}

	/**
	 * @param bestPrice the bestPrice to set
	 */
	public void setBestPrice(double bestPrice) {
		this.bestPrice = bestPrice;
	}

	/**
	 * @return the salesRank
	 */
	public double getSalesRank() {
		return salesRank;
	}

	/**
	 * @param salesRank the salesRank to set
	 */
	public void setSalesRank(double salesRank) {
		this.salesRank = salesRank;
	}
}
