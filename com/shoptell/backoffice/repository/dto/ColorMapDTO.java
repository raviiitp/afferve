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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.shoptell.backoffice.enums.CategoryEnum;

public class ColorMapDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Map<String,String> properties;
	
	private float avgSalesRank;
	
	private Set<ReviewedProductInfoDTO> responseData;
	
	private double bestPrice;

	public ColorMapDTO(MergeProductPropertiesDTO dto, Set<ReviewedProductInfoDTO> itemSet) {
		this.properties = new HashMap<String, String>();
		this.responseData = itemSet;
		for (ReviewedProductInfoDTO item : itemSet){
			if (bestPrice == 0 || (item.getSellingPrice() > 0 && bestPrice > item.getSellingPrice())){
				bestPrice = item.getSellingPrice();
			}
		}
		generateRank();
		String subCategory = dto.getSubCategoryName();
		String[] propKeys = CategoryEnum.getProperties(subCategory);
		if (propKeys != null){
			for (int i = 0; i < propKeys.length; i++) {
				if (i==0){
					properties.put(propKeys[i], dto.getProperty0());
				}
				else if (i==1){
					properties.put(propKeys[i], dto.getProperty1());
				}
				else if (i==2){
					properties.put(propKeys[i], dto.getProperty2());
				}
				else if (i==3){
					properties.put(propKeys[i], dto.getProperty3());
				}
				else if (i==4){
					properties.put(propKeys[i], dto.getProperty4());
				}
				else if (i==5){
					properties.put(propKeys[i], dto.getProperty5());
				}
				else if (i==6){
					properties.put(propKeys[i], dto.getProperty6());
				}
				else if (i==7){
					properties.put(propKeys[i], dto.getProperty7());
				}
				else if (i==8){
					properties.put(propKeys[i], dto.getProperty8());
				}
				else if (i==9){
					properties.put(propKeys[i], dto.getProperty9());
				}
				else if (i==10){
					properties.put(propKeys[i], dto.getProperty10());
				}
				else if (i==11){
					properties.put(propKeys[i], dto.getProperty11());
				}
				else if (i==12){
					properties.put(propKeys[i], dto.getProperty12());
				}
				else if (i==13){
					properties.put(propKeys[i], dto.getProperty13());
				}
				else if (i==14){
					properties.put(propKeys[i], dto.getProperty14());
				}
				else if (i==15){
					properties.put(propKeys[i], dto.getProperty15());
				}
				else if (i==16){
					properties.put(propKeys[i], dto.getProperty16());
				}
				else if (i==17){
					properties.put(propKeys[i], dto.getProperty17());
				}
				else if (i==18){
					properties.put(propKeys[i], dto.getProperty18());
				}
				else if (i==19){
					properties.put(propKeys[i], dto.getProperty19());
				}
				else {
					break;
				}
			}
		}
	}

	public float generateRank() {
		if (getResponseData() == null || getResponseData().size() < 1){
			return 0;
		}
		
		float sum = 0f;
		int count = 0;
		double tmp = 0;

		// Avg Rank Calculations
			for (ReviewedProductInfoDTO val : getResponseData()) {
				if (val.getSalesRank() > 0) {
					tmp = val.getSalesRank();
					if (tmp > 0) {
						sum += tmp;
						count++;
					}
				}
			}
		if (count > 0) {
			setAvgSalesRank(sum / count);
		}
		return getAvgSalesRank();
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
	 * @return the avgSalesRank
	 */
	public float getAvgSalesRank() {
		return avgSalesRank;
	}

	/**
	 * @param avgSalesRank the avgSalesRank to set
	 */
	public void setAvgSalesRank(float avgSalesRank) {
		this.avgSalesRank = avgSalesRank;
	}

	/**
	 * @return the responseData
	 */
	public Set<ReviewedProductInfoDTO> getResponseData() {
		return responseData;
	}

	/**
	 * @param responseData the responseData to set
	 */
	public void setResponseData(Set<ReviewedProductInfoDTO> responseData) {
		this.responseData = responseData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(avgSalesRank);
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((responseData == null) ? 0 : responseData.hashCode());
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
		ColorMapDTO other = (ColorMapDTO) obj;
		if (Float.floatToIntBits(avgSalesRank) != Float.floatToIntBits(other.avgSalesRank))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		}
		else if (!properties.equals(other.properties))
			return false;
		if (responseData == null) {
			if (other.responseData != null)
				return false;
		}
		else if (!responseData.equals(other.responseData))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ColorMapDTO [properties=" + properties + ", avgSalesRank=" + avgSalesRank + ", responseData=" + responseData + "]";
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
}

