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

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "afferve", name = "shippingoptions")
public class ShippingOptionsDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private double estimatedDelivery;
	private String deliveryTimeUnits;
	private String shippingType;

	public ShippingOptionsDTO() {
		this.estimatedDelivery = 3;
		this.deliveryTimeUnits = "DAYS";
		this.shippingType = "REGULAR";
	}

	/**
	 * @return the estimatedDelivery
	 */
	public double getEstimatedDelivery() {
		return estimatedDelivery;
	}

	/**
	 * @param estimatedDelivery the estimatedDelivery to set
	 */
	public void setEstimatedDelivery(double estimatedDelivery) {
		this.estimatedDelivery = estimatedDelivery;
	}

	/**
	 * @return the deliveryTimeUnits
	 */
	public String getDeliveryTimeUnits() {
		return deliveryTimeUnits;
	}

	/**
	 * @param deliveryTimeUnits the deliveryTimeUnits to set
	 */
	public void setDeliveryTimeUnits(String deliveryTimeUnits) {
		this.deliveryTimeUnits = deliveryTimeUnits;
	}

	/**
	 * @return the shippingType
	 */
	public String getShippingType() {
		return shippingType;
	}

	/**
	 * @param shippingType the shippingType to set
	 */
	public void setShippingType(String shippingType) {
		this.shippingType = shippingType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deliveryTimeUnits == null) ? 0 : deliveryTimeUnits.hashCode());
		long temp;
		temp = Double.doubleToLongBits(estimatedDelivery);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((shippingType == null) ? 0 : shippingType.hashCode());
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
		ShippingOptionsDTO other = (ShippingOptionsDTO) obj;
		if (deliveryTimeUnits == null) {
			if (other.deliveryTimeUnits != null)
				return false;
		} else if (!deliveryTimeUnits.equals(other.deliveryTimeUnits))
			return false;
		if (Double.doubleToLongBits(estimatedDelivery) != Double.doubleToLongBits(other.estimatedDelivery))
			return false;
		if (shippingType == null) {
			if (other.shippingType != null)
				return false;
		} else if (!shippingType.equals(other.shippingType))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShippingOptions [estimatedDelivery=" + estimatedDelivery + ", deliveryTimeUnits=" + deliveryTimeUnits + ", shippingType=" + shippingType + "]";
	}
	
}