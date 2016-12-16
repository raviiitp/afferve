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
import java.util.List;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "afferve", name = "productoffer")
public class ProductOfferDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private String merchantName;
	private String loyaltyPoints;
	private String condition;
	private List<String> price;
	private List<String> salePrice;
	private List<String> amountSaved;
	private List<String> percentageSaved;
	private List<String> promotionSummaryBenefitDesc;
	private List<String> promotionSummaryCategory;
	private List<String> promotionSummaryPromotionId;
	private List<String> promotionSummaryStartData;
	private List<String> promotionSummaryEndData;
	private List<String> promotionSummaryEligibilityReqDesc;
	private List<String> promotionSummaryTermsAndConditions;
	
	public ProductOfferDTO(){
		merchantName = null;
		loyaltyPoints = null;
		condition = null;
		price = null;
		salePrice = null;
		amountSaved = null;
		percentageSaved = null;
		promotionSummaryBenefitDesc = null;
		promotionSummaryCategory = null;
		promotionSummaryStartData = null;
		promotionSummaryEndData = null;
		promotionSummaryPromotionId = null;
		promotionSummaryEligibilityReqDesc = null;
		promotionSummaryTermsAndConditions = null;
	}
	
	/**
	 * @return the merchantName
	 */
	public String getMerchantName() {
		return merchantName;
	}
	/**
	 * @param merchantName the merchantName to set
	 */
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	/**
	 * @return the loyaltyPoints
	 */
	public String getLoyaltyPoints() {
		return loyaltyPoints;
	}
	/**
	 * @param loyaltyPoints the loyaltyPoints to set
	 */
	public void setLoyaltyPoints(String loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
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
	 * @return the price
	 */
	public List<String> getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(List<String> price) {
		this.price = price;
	}
	/**
	 * @return the salePrice
	 */
	public List<String> getSalePrice() {
		return salePrice;
	}
	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(List<String> salePrice) {
		this.salePrice = salePrice;
	}
	/**
	 * @return the amountSaved
	 */
	public List<String> getAmountSaved() {
		return amountSaved;
	}
	/**
	 * @param amountSaved the amountSaved to set
	 */
	public void setAmountSaved(List<String> amountSaved) {
		this.amountSaved = amountSaved;
	}
	/**
	 * @return the percentageSaved
	 */
	public List<String> getPercentageSaved() {
		return percentageSaved;
	}
	/**
	 * @param percentageSaved the percentageSaved to set
	 */
	public void setPercentageSaved(List<String> percentageSaved) {
		this.percentageSaved = percentageSaved;
	}
	/**
	 * @return the promotionSummaryBenefitDesc
	 */
	public List<String> getPromotionSummaryBenefitDesc() {
		return promotionSummaryBenefitDesc;
	}
	/**
	 * @param promotionSummaryBenefitDesc the promotionSummaryBenefitDesc to set
	 */
	public void setPromotionSummaryBenefitDesc(
			List<String> promotionSummaryBenefitDesc) {
		this.promotionSummaryBenefitDesc = promotionSummaryBenefitDesc;
	}
	/**
	 * @return the promotionSummaryCategory
	 */
	public List<String> getPromotionSummaryCategory() {
		return promotionSummaryCategory;
	}
	/**
	 * @param promotionSummaryCategory the promotionSummaryCategory to set
	 */
	public void setPromotionSummaryCategory(List<String> promotionSummaryCategory) {
		this.promotionSummaryCategory = promotionSummaryCategory;
	}
	/**
	 * @return the promotionSummaryPromotionId
	 */
	public List<String> getPromotionSummaryPromotionId() {
		return promotionSummaryPromotionId;
	}
	/**
	 * @param promotionSummaryPromotionId the promotionSummaryPromotionId to set
	 */
	public void setPromotionSummaryPromotionId(
			List<String> promotionSummaryPromotionId) {
		this.promotionSummaryPromotionId = promotionSummaryPromotionId;
	}
	/**
	 * @return the promotionSummaryStartData
	 */
	public List<String> getPromotionSummaryStartData() {
		return promotionSummaryStartData;
	}
	/**
	 * @param promotionSummaryStartData the promotionSummaryStartData to set
	 */
	public void setPromotionSummaryStartData(List<String> promotionSummaryStartData) {
		this.promotionSummaryStartData = promotionSummaryStartData;
	}
	/**
	 * @return the promotionSummaryEndData
	 */
	public List<String> getPromotionSummaryEndData() {
		return promotionSummaryEndData;
	}
	/**
	 * @param promotionSummaryEndData the promotionSummaryEndData to set
	 */
	public void setPromotionSummaryEndData(List<String> promotionSummaryEndData) {
		this.promotionSummaryEndData = promotionSummaryEndData;
	}
	/**
	 * @return the promotionSummaryEligibilityReqDesc
	 */
	public List<String> getPromotionSummaryEligibilityReqDesc() {
		return promotionSummaryEligibilityReqDesc;
	}
	/**
	 * @param promotionSummaryEligibilityReqDesc the promotionSummaryEligibilityReqDesc to set
	 */
	public void setPromotionSummaryEligibilityReqDesc(
			List<String> promotionSummaryEligibilityReqDesc) {
		this.promotionSummaryEligibilityReqDesc = promotionSummaryEligibilityReqDesc;
	}
	/**
	 * @return the promotionSummaryTermsAndConditions
	 */
	public List<String> getPromotionSummaryTermsAndConditions() {
		return promotionSummaryTermsAndConditions;
	}
	/**
	 * @param promotionSummaryTermsAndConditions the promotionSummaryTermsAndConditions to set
	 */
	public void setPromotionSummaryTermsAndConditions(
			List<String> promotionSummaryTermsAndConditions) {
		this.promotionSummaryTermsAndConditions = promotionSummaryTermsAndConditions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amountSaved == null) ? 0 : amountSaved.hashCode());
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result
				+ ((loyaltyPoints == null) ? 0 : loyaltyPoints.hashCode());
		result = prime * result
				+ ((merchantName == null) ? 0 : merchantName.hashCode());
		result = prime * result
				+ ((percentageSaved == null) ? 0 : percentageSaved.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime
				* result
				+ ((promotionSummaryBenefitDesc == null) ? 0
						: promotionSummaryBenefitDesc.hashCode());
		result = prime
				* result
				+ ((promotionSummaryCategory == null) ? 0
						: promotionSummaryCategory.hashCode());
		result = prime
				* result
				+ ((promotionSummaryEligibilityReqDesc == null) ? 0
						: promotionSummaryEligibilityReqDesc.hashCode());
		result = prime
				* result
				+ ((promotionSummaryEndData == null) ? 0
						: promotionSummaryEndData.hashCode());
		result = prime
				* result
				+ ((promotionSummaryPromotionId == null) ? 0
						: promotionSummaryPromotionId.hashCode());
		result = prime
				* result
				+ ((promotionSummaryStartData == null) ? 0
						: promotionSummaryStartData.hashCode());
		result = prime
				* result
				+ ((promotionSummaryTermsAndConditions == null) ? 0
						: promotionSummaryTermsAndConditions.hashCode());
		result = prime * result
				+ ((salePrice == null) ? 0 : salePrice.hashCode());
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
		ProductOfferDTO other = (ProductOfferDTO) obj;
		if (amountSaved == null) {
			if (other.amountSaved != null)
				return false;
		} else if (!amountSaved.equals(other.amountSaved))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (loyaltyPoints == null) {
			if (other.loyaltyPoints != null)
				return false;
		} else if (!loyaltyPoints.equals(other.loyaltyPoints))
			return false;
		if (merchantName == null) {
			if (other.merchantName != null)
				return false;
		} else if (!merchantName.equals(other.merchantName))
			return false;
		if (percentageSaved == null) {
			if (other.percentageSaved != null)
				return false;
		} else if (!percentageSaved.equals(other.percentageSaved))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (promotionSummaryBenefitDesc == null) {
			if (other.promotionSummaryBenefitDesc != null)
				return false;
		} else if (!promotionSummaryBenefitDesc
				.equals(other.promotionSummaryBenefitDesc))
			return false;
		if (promotionSummaryCategory == null) {
			if (other.promotionSummaryCategory != null)
				return false;
		} else if (!promotionSummaryCategory
				.equals(other.promotionSummaryCategory))
			return false;
		if (promotionSummaryEligibilityReqDesc == null) {
			if (other.promotionSummaryEligibilityReqDesc != null)
				return false;
		} else if (!promotionSummaryEligibilityReqDesc
				.equals(other.promotionSummaryEligibilityReqDesc))
			return false;
		if (promotionSummaryEndData == null) {
			if (other.promotionSummaryEndData != null)
				return false;
		} else if (!promotionSummaryEndData
				.equals(other.promotionSummaryEndData))
			return false;
		if (promotionSummaryPromotionId == null) {
			if (other.promotionSummaryPromotionId != null)
				return false;
		} else if (!promotionSummaryPromotionId
				.equals(other.promotionSummaryPromotionId))
			return false;
		if (promotionSummaryStartData == null) {
			if (other.promotionSummaryStartData != null)
				return false;
		} else if (!promotionSummaryStartData
				.equals(other.promotionSummaryStartData))
			return false;
		if (promotionSummaryTermsAndConditions == null) {
			if (other.promotionSummaryTermsAndConditions != null)
				return false;
		} else if (!promotionSummaryTermsAndConditions
				.equals(other.promotionSummaryTermsAndConditions))
			return false;
		if (salePrice == null) {
			if (other.salePrice != null)
				return false;
		} else if (!salePrice.equals(other.salePrice))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductOffer [merchantName=" + merchantName
				+ ", loyaltyPoints=" + loyaltyPoints + ", condition="
				+ condition + ", price=" + price + ", salePrice=" + salePrice
				+ ", amountSaved=" + amountSaved + ", percentageSaved="
				+ percentageSaved + ", promotionSummaryBenefitDesc="
				+ promotionSummaryBenefitDesc + ", promotionSummaryCategory="
				+ promotionSummaryCategory + ", promotionSummaryPromotionId="
				+ promotionSummaryPromotionId + ", promotionSummaryStartData="
				+ promotionSummaryStartData + ", promotionSummaryEndData="
				+ promotionSummaryEndData
				+ ", promotionSummaryEligibilityReqDesc="
				+ promotionSummaryEligibilityReqDesc
				+ ", promotionSummaryTermsAndConditions="
				+ promotionSummaryTermsAndConditions + "]";
	}
	
	
}
