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

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.shoptell.backoffice.enums.CategoryEnum;

@Table(keyspace = "afferve", name = "merged_product_properties")
public class MergeProductPropertiesDTO implements Serializable {

private static final long serialVersionUID = 1L;
	
	@PartitionKey
	private UUID mergeProdId;
	
	@ClusteringColumn(value=0)
	private UUID id;
	
	private boolean markForDelete;
	
	private String subCategoryName;
	
	private String property0;
	
	private String property1;
	
	private String property2;
	
	private String property3;
	
	private String property4;
	
	private String property5;
	
	private String property6;
	
	private String property7;
	
	private String property8;
	
	private String property9;
	
	private String property10;
	
	private String property11;
	
	private String property12;
	
	private String property13;
	
	private String property14;
	
	private String property15;
	
	private String property16;
	
	private String property17;
	
	private String property18;
	
	private String property19;
	
	private Date createdOn;
	
	private Date modifiedOn;
	
	public MergeProductPropertiesDTO() {
		// TODO Auto-generated constructor stub
	}

	public MergeProductPropertiesDTO(UUID id, UUID mergeProdId, String subCategoryName, Map<String, String> properties) {
		this.id = id;
		this.markForDelete = false;
		this.mergeProdId = mergeProdId;
		this.subCategoryName = subCategoryName;
		String[] propKeys = CategoryEnum.getProperties(subCategoryName);
		if (propKeys != null){
			for (int i = 0; i < propKeys.length; i++) {
				String value = properties.get(propKeys[i]);
				if (StringUtils.isBlank(value)) {
					value = "";
				}
				if (StringUtils.isBlank(property0)) {
					property0 = value;
				}
				else if (StringUtils.isBlank(property1)) {
					property1 = value;
				}
				else if (StringUtils.isBlank(property2)) {
					property2 = value;
				}
				else if (StringUtils.isBlank(property3)) {
					property3 = value;
				}
				else if (StringUtils.isBlank(property4)) {
					property4 = value;
				}
				else if (StringUtils.isBlank(property5)) {
					property5 = value;
				}
				else if (StringUtils.isBlank(property6)) {
					property6 = value;
				}
				else if (StringUtils.isBlank(property7)) {
					property7 = value;
				}
				else if (StringUtils.isBlank(property8)) {
					property8 = value;
				}
				else if (StringUtils.isBlank(property9)) {
					property9 = value;
				}
				else if (StringUtils.isBlank(property10)) {
					property10 = value;
				}
				else if (StringUtils.isBlank(property11)) {
					property11 = value;
				}
				else if (StringUtils.isBlank(property12)) {
					property12 = value;
				}
				else if (StringUtils.isBlank(property13)) {
					property13 = value;
				}
				else if (StringUtils.isBlank(property14)) {
					property14 = value;
				}
				else if (StringUtils.isBlank(property15)) {
					property15 = value;
				}
				else if (StringUtils.isBlank(property16)) {
					property16 = value;
				}
				else if (StringUtils.isBlank(property17)) {
					property17 = value;
				}
				else if (StringUtils.isBlank(property18)) {
					property18 = value;
				}
				else if (StringUtils.isBlank(property19)) {
					property19 = value;
				}
				else {
					break;
				}
			}
		}
	}

	/**
	 * @return the property10
	 */
	public String getProperty10() {
		return property10;
	}

	/**
	 * @param property10 the property10 to set
	 */
	public void setProperty10(String property10) {
		this.property10 = property10;
	}

	/**
	 * @return the property11
	 */
	public String getProperty11() {
		return property11;
	}

	/**
	 * @param property11 the property11 to set
	 */
	public void setProperty11(String property11) {
		this.property11 = property11;
	}

	/**
	 * @return the property12
	 */
	public String getProperty12() {
		return property12;
	}

	/**
	 * @param property12 the property12 to set
	 */
	public void setProperty12(String property12) {
		this.property12 = property12;
	}

	/**
	 * @return the property13
	 */
	public String getProperty13() {
		return property13;
	}

	/**
	 * @param property13 the property13 to set
	 */
	public void setProperty13(String property13) {
		this.property13 = property13;
	}

	/**
	 * @return the property14
	 */
	public String getProperty14() {
		return property14;
	}

	/**
	 * @param property14 the property14 to set
	 */
	public void setProperty14(String property14) {
		this.property14 = property14;
	}

	/**
	 * @return the property15
	 */
	public String getProperty15() {
		return property15;
	}

	/**
	 * @param property15 the property15 to set
	 */
	public void setProperty15(String property15) {
		this.property15 = property15;
	}

	/**
	 * @return the property16
	 */
	public String getProperty16() {
		return property16;
	}

	/**
	 * @param property16 the property16 to set
	 */
	public void setProperty16(String property16) {
		this.property16 = property16;
	}

	/**
	 * @return the property17
	 */
	public String getProperty17() {
		return property17;
	}

	/**
	 * @param property17 the property17 to set
	 */
	public void setProperty17(String property17) {
		this.property17 = property17;
	}

	/**
	 * @return the property18
	 */
	public String getProperty18() {
		return property18;
	}

	/**
	 * @param property18 the property18 to set
	 */
	public void setProperty18(String property18) {
		this.property18 = property18;
	}

	/**
	 * @return the property19
	 */
	public String getProperty19() {
		return property19;
	}

	/**
	 * @param property19 the property19 to set
	 */
	public void setProperty19(String property19) {
		this.property19 = property19;
	}

	/**
	 * @return the mergeProdId
	 */
	public UUID getMergeProdId() {
		return mergeProdId;
	}

	/**
	 * @param mergeProdId the mergeProdId to set
	 */
	public void setMergeProdId(UUID mergeProdId) {
		this.mergeProdId = mergeProdId;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
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
	 * @return the property0
	 */
	public String getProperty0() {
		return property0;
	}

	/**
	 * @param property0 the property0 to set
	 */
	public void setProperty0(String property0) {
		this.property0 = property0;
	}

	/**
	 * @return the property1
	 */
	public String getProperty1() {
		return property1;
	}

	/**
	 * @param property1 the property1 to set
	 */
	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	/**
	 * @return the property2
	 */
	public String getProperty2() {
		return property2;
	}

	/**
	 * @param property2 the property2 to set
	 */
	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	/**
	 * @return the property3
	 */
	public String getProperty3() {
		return property3;
	}

	/**
	 * @param property3 the property3 to set
	 */
	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	/**
	 * @return the property4
	 */
	public String getProperty4() {
		return property4;
	}

	/**
	 * @param property4 the property4 to set
	 */
	public void setProperty4(String property4) {
		this.property4 = property4;
	}

	/**
	 * @return the property5
	 */
	public String getProperty5() {
		return property5;
	}

	/**
	 * @param property5 the property5 to set
	 */
	public void setProperty5(String property5) {
		this.property5 = property5;
	}

	/**
	 * @return the property6
	 */
	public String getProperty6() {
		return property6;
	}

	/**
	 * @param property6 the property6 to set
	 */
	public void setProperty6(String property6) {
		this.property6 = property6;
	}

	/**
	 * @return the property7
	 */
	public String getProperty7() {
		return property7;
	}

	/**
	 * @param property7 the property7 to set
	 */
	public void setProperty7(String property7) {
		this.property7 = property7;
	}

	/**
	 * @return the property8
	 */
	public String getProperty8() {
		return property8;
	}

	/**
	 * @param property8 the property8 to set
	 */
	public void setProperty8(String property8) {
		this.property8 = property8;
	}

	/**
	 * @return the property9
	 */
	public String getProperty9() {
		return property9;
	}

	/**
	 * @param property9 the property9 to set
	 */
	public void setProperty9(String property9) {
		this.property9 = property9;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mergeProdId == null) ? 0 : mergeProdId.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		result = prime * result + ((property0 == null) ? 0 : property0.hashCode());
		result = prime * result + ((property1 == null) ? 0 : property1.hashCode());
		result = prime * result + ((property10 == null) ? 0 : property10.hashCode());
		result = prime * result + ((property11 == null) ? 0 : property11.hashCode());
		result = prime * result + ((property12 == null) ? 0 : property12.hashCode());
		result = prime * result + ((property13 == null) ? 0 : property13.hashCode());
		result = prime * result + ((property14 == null) ? 0 : property14.hashCode());
		result = prime * result + ((property15 == null) ? 0 : property15.hashCode());
		result = prime * result + ((property16 == null) ? 0 : property16.hashCode());
		result = prime * result + ((property17 == null) ? 0 : property17.hashCode());
		result = prime * result + ((property18 == null) ? 0 : property18.hashCode());
		result = prime * result + ((property19 == null) ? 0 : property19.hashCode());
		result = prime * result + ((property2 == null) ? 0 : property2.hashCode());
		result = prime * result + ((property3 == null) ? 0 : property3.hashCode());
		result = prime * result + ((property4 == null) ? 0 : property4.hashCode());
		result = prime * result + ((property5 == null) ? 0 : property5.hashCode());
		result = prime * result + ((property6 == null) ? 0 : property6.hashCode());
		result = prime * result + ((property7 == null) ? 0 : property7.hashCode());
		result = prime * result + ((property8 == null) ? 0 : property8.hashCode());
		result = prime * result + ((property9 == null) ? 0 : property9.hashCode());
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
		MergeProductPropertiesDTO other = (MergeProductPropertiesDTO) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (mergeProdId == null) {
			if (other.mergeProdId != null)
				return false;
		}
		else if (!mergeProdId.equals(other.mergeProdId))
			return false;
		if (modifiedOn == null) {
			if (other.modifiedOn != null)
				return false;
		}
		else if (!modifiedOn.equals(other.modifiedOn))
			return false;
		if (property0 == null) {
			if (other.property0 != null)
				return false;
		}
		else if (!property0.equals(other.property0))
			return false;
		if (property1 == null) {
			if (other.property1 != null)
				return false;
		}
		else if (!property1.equals(other.property1))
			return false;
		if (property10 == null) {
			if (other.property10 != null)
				return false;
		}
		else if (!property10.equals(other.property10))
			return false;
		if (property11 == null) {
			if (other.property11 != null)
				return false;
		}
		else if (!property11.equals(other.property11))
			return false;
		if (property12 == null) {
			if (other.property12 != null)
				return false;
		}
		else if (!property12.equals(other.property12))
			return false;
		if (property13 == null) {
			if (other.property13 != null)
				return false;
		}
		else if (!property13.equals(other.property13))
			return false;
		if (property14 == null) {
			if (other.property14 != null)
				return false;
		}
		else if (!property14.equals(other.property14))
			return false;
		if (property15 == null) {
			if (other.property15 != null)
				return false;
		}
		else if (!property15.equals(other.property15))
			return false;
		if (property16 == null) {
			if (other.property16 != null)
				return false;
		}
		else if (!property16.equals(other.property16))
			return false;
		if (property17 == null) {
			if (other.property17 != null)
				return false;
		}
		else if (!property17.equals(other.property17))
			return false;
		if (property18 == null) {
			if (other.property18 != null)
				return false;
		}
		else if (!property18.equals(other.property18))
			return false;
		if (property19 == null) {
			if (other.property19 != null)
				return false;
		}
		else if (!property19.equals(other.property19))
			return false;
		if (property2 == null) {
			if (other.property2 != null)
				return false;
		}
		else if (!property2.equals(other.property2))
			return false;
		if (property3 == null) {
			if (other.property3 != null)
				return false;
		}
		else if (!property3.equals(other.property3))
			return false;
		if (property4 == null) {
			if (other.property4 != null)
				return false;
		}
		else if (!property4.equals(other.property4))
			return false;
		if (property5 == null) {
			if (other.property5 != null)
				return false;
		}
		else if (!property5.equals(other.property5))
			return false;
		if (property6 == null) {
			if (other.property6 != null)
				return false;
		}
		else if (!property6.equals(other.property6))
			return false;
		if (property7 == null) {
			if (other.property7 != null)
				return false;
		}
		else if (!property7.equals(other.property7))
			return false;
		if (property8 == null) {
			if (other.property8 != null)
				return false;
		}
		else if (!property8.equals(other.property8))
			return false;
		if (property9 == null) {
			if (other.property9 != null)
				return false;
		}
		else if (!property9.equals(other.property9))
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
		return "MergeProductPropertiesDTO [mergeProdId=" + mergeProdId + ", id=" + id + ", subCategoryName=" + subCategoryName + ", property0=" + property0
				+ ", property1=" + property1 + ", property2=" + property2 + ", property3=" + property3 + ", property4=" + property4 + ", property5="
				+ property5 + ", property6=" + property6 + ", property7=" + property7 + ", property8=" + property8 + ", property9=" + property9
				+ ", property10=" + property10 + ", property11=" + property11 + ", property12=" + property12 + ", property13=" + property13 + ", property14="
				+ property14 + ", property15=" + property15 + ", property16=" + property16 + ", property17=" + property17 + ", property18=" + property18
				+ ", property19=" + property19 + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + "]";
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
}
