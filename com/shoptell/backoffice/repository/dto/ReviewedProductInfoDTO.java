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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;
import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.ImageSizeEnum;

@Table(keyspace = "afferve", name = "reviewed_product_info")
public class ReviewedProductInfoDTO implements Serializable {
	/**
	 * PRIMARY KEY (( home), categoryName, id);
	 */
	private static final long serialVersionUID = 1L;
	@PartitionKey
	private String home;
	@ClusteringColumn(value = 0)
	private String subCategoryName;
	@ClusteringColumn(value = 1)
	private String id; // Amazon : ASIN, Flipkart : id and Ebay : id
	
	@Column(name = "isMerged")
	private boolean merged;
	private boolean inStock;
	private boolean disabled;
	private UUID mergeProdInfoId;
	private UUID prodInfoId;
	private boolean reviewed;
	
	private boolean hotUpdate; // set when updated properties from hot update
	private boolean markForDelete; // when needed to be cleaned off
	
	private double mrp;
	private double sellingPrice;
	private double score;
	private double salesRank;
	
	private String metaCategory;
	private String categoryName;
	private Set<String> categoryPaths;
	private String categoryId;
	
	private String productBrand; //e.g. Samsung, Apple
	private String productSubBrand; // e.g. Galaxy, iPhone
	private String series;			//e.g. 5S, S5, 5C, A4
	private String model;		 //e.g. GT-I9500, iPhone 5s
	
	private Map<String, String> properties;
	private List<String> features;
	
	private String title;
	
	@Transient
	private double cbRate;
	@Transient
	private double maxCBAmount;
	@Transient
	private List<Discount> discounts = new LinkedList<Discount>();
	@Transient
	private String name;
	
	private Date createdOn;
	private String createdBy;
	private Date modifiedOn;
	private String modifiedBy;
	
	private Map<String, String> imageUrls;
	
	private Set<String> tags;
	
	private String color;
	private String condition;
	
	private String description;
	
	private String productUrl;
	
	private String subTitle;
	
	@Transient
	private boolean updated = false;

	public ReviewedProductInfoDTO() {
		super();
		this.markForDelete = false;
		this.hotUpdate = false;
		this.title = null;
		this.home = null;
		this.subCategoryName = null;
		this.id = null;
		this.merged = false;
		this.inStock = false;
		this.mergeProdInfoId = null;
		this.mrp = 0;
		this.sellingPrice = 0;
		this.score = 0;
		this.salesRank = Double.MAX_VALUE;
		this.metaCategory = null;
		this.categoryName = null;
		this.categoryPaths = null;
		this.categoryId = null;
		this.productBrand = null;
		this.productSubBrand = null;
		this.series = null;
		this.model = null;
		this.reviewed = false;
		this.properties = new HashMap<String, String>();
		this.features = new LinkedList<String>();
		this.cbRate = 0;
		this.maxCBAmount = 0;
		this.discounts = new LinkedList<Discount>();
		this.createdOn = new Date(System.currentTimeMillis());
		this.createdBy = null;
		this.modifiedOn = new Date(System.currentTimeMillis());
		this.modifiedBy = null;
		this.imageUrls = new HashMap<String, String>();
		this.tags = new HashSet<String>();
		this.color = null;
		this.condition = null;
		this.description = null;
		this.productUrl = null;
		this.subTitle = null;
	}

	public ReviewedProductInfoDTO(String product_brand, String product_sub_brand, String series, String model, String color, HashMap<String, String> map,
			HomeProductInfoDTO dto, UUID uuid) {
		super();
		this.markForDelete = false;
		this.hotUpdate = false;
		this.reviewed = false;
		this.prodInfoId = uuid;
		this.categoryPaths = dto.getCategoryPaths();
		this.categoryId = dto.getCategoryId();
		this.productBrand = product_brand;
		this.productSubBrand = product_sub_brand;
		this.series = series;
		this.model = model;
		this.properties = map;
		this.title = dto.getOriginalTitle();
		this.home = dto.getHome();
		this.subCategoryName = dto.getSubCategoryName();
		this.id = dto.getId();
		this.merged = false;
		this.inStock = dto.isInStock();
		this.mergeProdInfoId = null;
		this.mrp = dto.getMrp();
		this.sellingPrice = dto.getSellingPrice();
		this.score = 0;
		this.salesRank = dto.getSalesRank();
		if ((HomeEnum.FLIPKART.name().equals(dto.getHome()) || HomeEnum.AMAZON.name().equals(dto.getHome())) && dto.getFeatures() != null && dto.getFeatures().size() > 0) {
			List<String> tmp = dto.getFeatures();
			BackofficeUtil.listUpperCase(tmp);
			this.features = tmp;
		}
		else {
			this.features = new LinkedList<String>();
		}
		this.cbRate = 0;
		this.maxCBAmount = 0;
		this.discounts = new LinkedList<Discount>();
		this.createdOn = new Date(System.currentTimeMillis());
		this.createdBy = null;
		this.modifiedOn = new Date(System.currentTimeMillis());
		this.modifiedBy = null;
		this.imageUrls = populateImageMap(dto);
		this.tags = new HashSet<String>();
		this.condition = dto.getCondition();
		this.description = dto.getDescription();
		this.productUrl = dto.getProductUrl();
		this.subTitle = dto.getSubTitle();
		this.color = color;
		if (StringUtils.isNotBlank(this.subCategoryName)){
			this.metaCategory = CategoryEnum.getCategory(this.subCategoryName).getMeta().name();
		}
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the merged
	 */
	public boolean isMerged() {
		return merged;
	}

	/**
	 * @param merged the merged to set
	 */
	public void setMerged(boolean merged) {
		this.merged = merged;
	}

	/**
	 * @return the inStock
	 */
	public boolean isInStock() {
		return inStock;
	}

	/**
	 * @param inStock the inStock to set
	 */
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
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
	 * @return the mergeProdInfoId
	 */
	public UUID getMergeProdInfoId() {
		return mergeProdInfoId;
	}

	/**
	 * @param mergeProdInfoId the mergeProdInfoId to set
	 */
	public void setMergeProdInfoId(UUID mergeProdInfoId) {
		this.mergeProdInfoId = mergeProdInfoId;
	}

	/**
	 * @return the mrp
	 */
	public double getMrp() {
		return mrp;
	}

	/**
	 * @param mrp the mrp to set
	 */
	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	/**
	 * @return the sellingPrice
	 */
	public double getSellingPrice() {
		return sellingPrice;
	}

	/**
	 * @param sellingPrice the sellingPrice to set
	 */
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
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

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the categoryPaths
	 */
	public Set<String> getCategoryPaths() {
		return categoryPaths;
	}

	/**
	 * @param categoryPaths the categoryPaths to set
	 */
	public void setCategoryPaths(Set<String> categoryPaths) {
		this.categoryPaths = categoryPaths;
	}

	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the productBrand
	 */
	public String getProductBrand() {
		return productBrand;
	}

	/**
	 * @param productBrand the productBrand to set
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
	 * @param productSubBrand the productSubBrand to set
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
	 * @param series the series to set
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
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
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
	 * @return the cbRate
	 */
	public double getCbRate() {
		return cbRate;
	}

	/**
	 * @param cbRate the cbRate to set
	 */
	public void setCbRate(double cbRate) {
		this.cbRate = cbRate;
	}

	/**
	 * @return the maxCBAmount
	 */
	public double getMaxCBAmount() {
		return maxCBAmount;
	}

	/**
	 * @param maxCBAmount the maxCBAmount to set
	 */
	public void setMaxCBAmount(double maxCBAmount) {
		this.maxCBAmount = maxCBAmount;
	}

	/**
	 * @return the discounts
	 */
	public List<Discount> getDiscounts() {
		return discounts;
	}

	/**
	 * @param discounts the discounts to set
	 */
	public void setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
	}

	/**
	 * @return the name
	 */
	public String getName(){
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(productBrand)){
			sb.append(productBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(productSubBrand)){
			sb.append(productSubBrand.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(series)){
			sb.append(series.trim()).append(" ");
		}
		if (!StringUtils.isEmpty(model)){
			sb.append(model.trim()).append(" ");
		}
		if (sb.length() > 0) {
			return sb.toString().trim();
		}
		return null;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the imageUrls
	 */
	public Map<String, String> getImageUrls() {
		return imageUrls;
	}

	/**
	 * @param imageUrls the imageUrls to set
	 */
	public void setImageUrls(Map<String, String> imageUrls) {
		this.imageUrls = imageUrls;
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
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
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

	/**
	 * @return the productUrl
	 */
	public String getProductUrl() {
		return productUrl;
	}

	/**
	 * @param productUrl the productUrl to set
	 */
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReviewedProductInfoDTO [home=" + home + ", subCategoryName=" + subCategoryName + ", id=" + id + ", merged=" + merged + ", inStock=" + inStock
				+ ", disabled=" + disabled /*+ ", readyToMerge=" + readyToMerge*/ + ", mergeProdInfoId=" + mergeProdInfoId + ", mrp=" + mrp + ", sellingPrice="
				+ sellingPrice + ", score=" + score + ", salesRank=" + salesRank + ", metaCategory=" + metaCategory + ", categoryName=" + categoryName
				+ ", categoryPaths=" + categoryPaths + ", categoryId=" + categoryId + ", productBrand=" + productBrand + ", productSubBrand=" + productSubBrand
				+ ", series=" + series + ", model=" + model /*+ ", primaryProperty=" + primaryProperty*/ /*+ ", secondaryProperty=" + secondaryProperty*/
				+ ", properties=" + properties + ", features=" + features + ", cbRate=" + cbRate + ", maxCBAmount=" + maxCBAmount + ", discounts=" + discounts
				+ ", name=" + name + ", createdOn=" + createdOn + ", createdBy=" + createdBy + ", modifiedOn=" + modifiedOn + ", modifiedBy=" + modifiedBy
				+ ", imageUrls=" + imageUrls + ", tags=" + tags /*+ ", color=" + color*/ /*+ ", size=" + size*/ + ", condition=" + condition + ", description="
				+ description + ", productUrl=" + productUrl + ", subTitle=" + subTitle + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + ((categoryPaths == null) ? 0 : categoryPaths.hashCode());
		long temp;
		temp = Double.doubleToLongBits(cbRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		/*result = prime * result + ((color == null) ? 0 : color.hashCode());*/
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result + ((discounts == null) ? 0 : discounts.hashCode());
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((home == null) ? 0 : home.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrls == null) ? 0 : imageUrls.hashCode());
		result = prime * result + (inStock ? 1231 : 1237);
		temp = Double.doubleToLongBits(maxCBAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((mergeProdInfoId == null) ? 0 : mergeProdInfoId.hashCode());
		result = prime * result + (merged ? 1231 : 1237);
		result = prime * result + ((metaCategory == null) ? 0 : metaCategory.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		temp = Double.doubleToLongBits(mrp);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((productBrand == null) ? 0 : productBrand.hashCode());
		result = prime * result + ((productSubBrand == null) ? 0 : productSubBrand.hashCode());
		result = prime * result + ((productUrl == null) ? 0 : productUrl.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		temp = Double.doubleToLongBits(salesRank);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(sellingPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((series == null) ? 0 : series.hashCode());
		result = prime * result + ((subCategoryName == null) ? 0 : subCategoryName.hashCode());
		result = prime * result + ((subTitle == null) ? 0 : subTitle.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		ReviewedProductInfoDTO other = (ReviewedProductInfoDTO) obj;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		}
		else if (!categoryId.equals(other.categoryId))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		}
		else if (!categoryName.equals(other.categoryName))
			return false;
		if (categoryPaths == null) {
			if (other.categoryPaths != null)
				return false;
		}
		else if (!categoryPaths.equals(other.categoryPaths))
			return false;
		if (Double.doubleToLongBits(cbRate) != Double.doubleToLongBits(other.cbRate))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		}
		else if (!createdBy.equals(other.createdBy))
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
		if (disabled != other.disabled)
			return false;
		if (discounts == null) {
			if (other.discounts != null)
				return false;
		}
		else if (!discounts.equals(other.discounts))
			return false;
		if (features == null) {
			if (other.features != null)
				return false;
		}
		else if (!features.equals(other.features))
			return false;
		if (home == null) {
			if (other.home != null)
				return false;
		}
		else if (!home.equals(other.home))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (imageUrls == null) {
			if (other.imageUrls != null)
				return false;
		}
		else if (!imageUrls.equals(other.imageUrls))
			return false;
		if (inStock != other.inStock)
			return false;
		if (Double.doubleToLongBits(maxCBAmount) != Double.doubleToLongBits(other.maxCBAmount))
			return false;
		if (mergeProdInfoId == null) {
			if (other.mergeProdInfoId != null)
				return false;
		}
		else if (!mergeProdInfoId.equals(other.mergeProdInfoId))
			return false;
		if (merged != other.merged)
			return false;
		if (metaCategory == null) {
			if (other.metaCategory != null)
				return false;
		}
		else if (!metaCategory.equals(other.metaCategory))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		}
		else if (!model.equals(other.model))
			return false;
		if (modifiedBy == null) {
			if (other.modifiedBy != null)
				return false;
		}
		else if (!modifiedBy.equals(other.modifiedBy))
			return false;
		if (modifiedOn == null) {
			if (other.modifiedOn != null)
				return false;
		}
		else if (!modifiedOn.equals(other.modifiedOn))
			return false;
		if (Double.doubleToLongBits(mrp) != Double.doubleToLongBits(other.mrp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
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
		if (productUrl == null) {
			if (other.productUrl != null)
				return false;
		}
		else if (!productUrl.equals(other.productUrl))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		}
		else if (!properties.equals(other.properties))
			return false;
		if (Double.doubleToLongBits(salesRank) != Double.doubleToLongBits(other.salesRank))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		if (Double.doubleToLongBits(sellingPrice) != Double.doubleToLongBits(other.sellingPrice))
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
		if (subTitle == null) {
			if (other.subTitle != null)
				return false;
		}
		else if (!subTitle.equals(other.subTitle))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		}
		else if (!tags.equals(other.tags))
			return false;
		return true;
	}
	
	private Map<String, String> populateImageMap(HomeProductInfoDTO data) {
		Map<String, String> imageMap = new HashMap<String, String>();
		String tmp = null;
		if (StringUtils.isNotBlank(data.getImageUrl())){
			tmp = data.getImageUrl().replaceAll("https:", "http:").trim();
			data.setImageUrl(tmp);
		}
		if (StringUtils.isNotBlank(data.getImageUrlSmall())){
			tmp = data.getImageUrlSmall().replaceAll("https:", "http:").trim();
			data.setImageUrlSmall(tmp);
		}
		if (StringUtils.isNotBlank(data.getImageUrlLarge())){
			tmp = data.getImageUrlLarge().replaceAll("https:", "http:").trim();
			data.setImageUrlLarge(tmp);
		}
		
		imageMap.put(ImageSizeEnum.M.name(), data.getImageUrl() == null ? "null" : data.getImageUrl());
		imageMap.put(ImageSizeEnum.S.name(), data.getImageUrlSmall() == null ? "null" : data.getImageUrlSmall());
		imageMap.put(ImageSizeEnum.L.name(), data.getImageUrlLarge() == null ? "null" : data.getImageUrlLarge());
		return imageMap;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the prodInfoId
	 */
	public UUID getProdInfoId() {
		return prodInfoId;
	}

	/**
	 * @param prodInfoId the prodInfoId to set
	 */
	public void setProdInfoId(UUID prodInfoId) {
		this.prodInfoId = prodInfoId;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the reviewed
	 */
	public boolean isReviewed() {
		return reviewed;
	}

	/**
	 * @param reviewed the reviewed to set
	 */
	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	/**
	 * @return the hotUpdate
	 */
	public boolean isHotUpdate() {
		return hotUpdate;
	}

	/**
	 * @param hotUpdate the hotUpdate to set
	 */
	public void setHotUpdate(boolean hotUpdate) {
		this.hotUpdate = hotUpdate;
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
	 * @return the updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
