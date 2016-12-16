/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.config.elasticsearch;

/**
 * @author abhishekagarwal
 *
 */
public class ElasticSearchResponse {
	private String id;
	private String name;
	private double rank;
	private int views;
	private String subCategory;
	private String metaCategory;
	private String productBrand;
	
	public ElasticSearchResponse(){}
	
	public ElasticSearchResponse(String id, String name, double rank, int views) {
		super();
		this.id = id;
		this.name = name;
		this.rank = rank;
		this.views = views;
	}
	
	public ElasticSearchResponse(String id, String name, double rank, int views, String subCategory) {
		super();
		this.id = id;
		this.name = name;
		this.rank = rank;
		this.views = views;
		this.subCategory = subCategory;
	}
	
	public ElasticSearchResponse(String id, String name, double rank, int views, String subCategory, String metaCategory, String productBrand) {
		super();
		this.id = id;
		this.name = name;
		this.rank = rank;
		this.views = views;
		this.subCategory = subCategory;
		this.metaCategory = metaCategory;
		this.productBrand = productBrand;
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
	 * @return the rank
	 */
	public double getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(double rank) {
		this.rank = rank;
	}
	/**
	 * @return the views
	 */
	public int getViews() {
		return views;
	}
	/**
	 * @param views the views to set
	 */
	public void setViews(int views) {
		this.views = views;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ElasticSearchResponse [id=" + id + ", name=" + name + ", rank=" + rank + ", views=" + views + "]";
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
}
