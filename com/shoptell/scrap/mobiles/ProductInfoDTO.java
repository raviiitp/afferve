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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

@Table(keyspace = "afferve", name = "product_info")
public class ProductInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private Set<String> tags;
	@PartitionKey
	private String subCategoryName;
	@ClusteringColumn(value = 0)
	private String productBrand;
	@ClusteringColumn(value = 1)
	private UUID id;
	private String productSubBrand;
	private String series;
	private String model;
	private String link;
	private Date createdOn;
	private Date modifiedOn;
	private Map<String, String> network;
	private Map<String, String> launch;
	private Map<String, String> body;
	private Map<String, String> display;
	private Map<String, String> platform;
	private Map<String, String> memory;
	private Map<String, String> camera;
	private Map<String, String> sound;
	private Map<String, String> comms;
	private Map<String, String> features;
	private Map<String, String> battery;
	private Map<String, String> misc;
	private Map<String, String> tests;
	private Map<String, String> property1;
	private Map<String, String> property2;
	private Map<String, String> property3;
	private Map<String, String> property4;
	private Map<String, String> property5;
	private Map<String, String> property6;
	private Map<String, String> property7;
	private String propertyName1;
	private String propertyName2;
	private String propertyName3;
	private String propertyName4;
	private String propertyName5;
	private String propertyName6;
	private String propertyName7;
	
	@Transient
	private Map<String, String> allProperties = new HashMap<String, String>();

	@Transient
	private Pattern p = Pattern.compile("([&.+\\w\\d]+\\s)?(([&.+\\w\\d]+\\s)+)?([&.+\\w\\d]+)");

	public ProductInfoDTO() {
		this.setId(UUID.randomUUID());
	}

	public ProductInfoDTO(String brand, String title, String url, String subCategoryName) {
		this.setId(UUID.randomUUID());
		this.link = url.trim();
		this.productBrand = brand.trim();
		title = title.replaceAll("\\s?\\(.*\\)", "");
		this.title = title.trim().toUpperCase();
		Matcher m = p.matcher(this.title);
		if (m.find()) {
			if (StringUtils.isNotBlank(m.group(1))) {
				this.productSubBrand = m.group(1);
			}
			else {
				this.productSubBrand = "";
			}
			if (StringUtils.isNotBlank(m.group(2))) {
				this.series = m.group(2);
			}
			else {
				this.series = "";
			}
			if (StringUtils.isNotBlank(m.group(4))) {
				this.model = m.group(4);
			}
			else {
				this.model = "";
			}
		}
		this.tags = new HashSet<String>(Arrays.asList(this.title.split(" ")));
		this.tags.add(brand);
		this.createdOn = new Date(System.currentTimeMillis());
		this.subCategoryName = subCategoryName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(Set<String> tags) {
		this.tags = tags;
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
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
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

	/**
	 * @return the network
	 */
	public Map<String, String> getNetwork() {
		return network;
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(Map<String, String> network) {
		this.network = network;
	}

	/**
	 * @return the launch
	 */
	public Map<String, String> getLaunch() {
		return launch;
	}

	/**
	 * @param launch
	 *            the launch to set
	 */
	public void setLaunch(Map<String, String> launch) {
		this.launch = launch;
	}

	/**
	 * @return the body
	 */
	public Map<String, String> getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(Map<String, String> body) {
		this.body = body;
	}

	/**
	 * @return the display
	 */
	public Map<String, String> getDisplay() {
		return display;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(Map<String, String> display) {
		this.display = display;
	}

	/**
	 * @return the platform
	 */
	public Map<String, String> getPlatform() {
		return platform;
	}

	/**
	 * @param platform
	 *            the platform to set
	 */
	public void setPlatform(Map<String, String> platform) {
		this.platform = platform;
	}

	/**
	 * @return the memory
	 */
	public Map<String, String> getMemory() {
		return memory;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(Map<String, String> memory) {
		this.memory = memory;
	}

	/**
	 * @return the camera
	 */
	public Map<String, String> getCamera() {
		return camera;
	}

	/**
	 * @param camera
	 *            the camera to set
	 */
	public void setCamera(Map<String, String> camera) {
		this.camera = camera;
	}

	/**
	 * @return the sound
	 */
	public Map<String, String> getSound() {
		return sound;
	}

	/**
	 * @param sound
	 *            the sound to set
	 */
	public void setSound(Map<String, String> sound) {
		this.sound = sound;
	}

	/**
	 * @return the comms
	 */
	public Map<String, String> getComms() {
		return comms;
	}

	/**
	 * @param comms
	 *            the comms to set
	 */
	public void setComms(Map<String, String> comms) {
		this.comms = comms;
	}

	/**
	 * @return the features
	 */
	public Map<String, String> getFeatures() {
		return features;
	}

	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(Map<String, String> features) {
		this.features = features;
	}

	/**
	 * @return the battery
	 */
	public Map<String, String> getBattery() {
		return battery;
	}

	/**
	 * @param battery
	 *            the battery to set
	 */
	public void setBattery(Map<String, String> battery) {
		this.battery = battery;
	}

	/**
	 * @return the misc
	 */
	public Map<String, String> getMisc() {
		return misc;
	}

	/**
	 * @param misc
	 *            the misc to set
	 */
	public void setMisc(Map<String, String> misc) {
		this.misc = misc;
	}

	/**
	 * @return the tests
	 */
	public Map<String, String> getTests() {
		return tests;
	}

	/**
	 * @param tests
	 *            the tests to set
	 */
	public void setTests(Map<String, String> tests) {
		this.tests = tests;
	}

	/**
	 * @return the property1
	 */
	public Map<String, String> getProperty1() {
		return property1;
	}

	/**
	 * @param property1
	 *            the property1 to set
	 */
	public void setProperty1(Map<String, String> property1) {
		this.property1 = property1;
	}

	/**
	 * @return the property2
	 */
	public Map<String, String> getProperty2() {
		return property2;
	}

	/**
	 * @param property2
	 *            the property2 to set
	 */
	public void setProperty2(Map<String, String> property2) {
		this.property2 = property2;
	}

	/**
	 * @return the property3
	 */
	public Map<String, String> getProperty3() {
		return property3;
	}

	/**
	 * @param property3
	 *            the property3 to set
	 */
	public void setProperty3(Map<String, String> property3) {
		this.property3 = property3;
	}

	/**
	 * @return the property4
	 */
	public Map<String, String> getProperty4() {
		return property4;
	}

	/**
	 * @param property4
	 *            the property4 to set
	 */
	public void setProperty4(Map<String, String> property4) {
		this.property4 = property4;
	}

	/**
	 * @return the property5
	 */
	public Map<String, String> getProperty5() {
		return property5;
	}

	/**
	 * @param property5
	 *            the property5 to set
	 */
	public void setProperty5(Map<String, String> property5) {
		this.property5 = property5;
	}

	/**
	 * @return the property6
	 */
	public Map<String, String> getProperty6() {
		return property6;
	}

	/**
	 * @param property6
	 *            the property6 to set
	 */
	public void setProperty6(Map<String, String> property6) {
		this.property6 = property6;
	}

	/**
	 * @return the property7
	 */
	public Map<String, String> getProperty7() {
		return property7;
	}

	/**
	 * @param property7
	 *            the property7 to set
	 */
	public void setProperty7(Map<String, String> property7) {
		this.property7 = property7;
	}

	/**
	 * @return the propertyName1
	 */
	public String getPropertyName1() {
		return propertyName1;
	}

	/**
	 * @param propertyName1
	 *            the propertyName1 to set
	 */
	public void setPropertyName1(String propertyName1) {
		this.propertyName1 = propertyName1;
	}

	/**
	 * @return the propertyName2
	 */
	public String getPropertyName2() {
		return propertyName2;
	}

	/**
	 * @param propertyName2
	 *            the propertyName2 to set
	 */
	public void setPropertyName2(String propertyName2) {
		this.propertyName2 = propertyName2;
	}

	/**
	 * @return the propertyName3
	 */
	public String getPropertyName3() {
		return propertyName3;
	}

	/**
	 * @param propertyName3
	 *            the propertyName3 to set
	 */
	public void setPropertyName3(String propertyName3) {
		this.propertyName3 = propertyName3;
	}

	/**
	 * @return the propertyName4
	 */
	public String getPropertyName4() {
		return propertyName4;
	}

	/**
	 * @param propertyName4
	 *            the propertyName4 to set
	 */
	public void setPropertyName4(String propertyName4) {
		this.propertyName4 = propertyName4;
	}

	/**
	 * @return the propertyName5
	 */
	public String getPropertyName5() {
		return propertyName5;
	}

	/**
	 * @param propertyName5
	 *            the propertyName5 to set
	 */
	public void setPropertyName5(String propertyName5) {
		this.propertyName5 = propertyName5;
	}

	/**
	 * @return the propertyName6
	 */
	public String getPropertyName6() {
		return propertyName6;
	}

	/**
	 * @param propertyName6
	 *            the propertyName6 to set
	 */
	public void setPropertyName6(String propertyName6) {
		this.propertyName6 = propertyName6;
	}

	/**
	 * @return the propertyName7
	 */
	public String getPropertyName7() {
		return propertyName7;
	}

	/**
	 * @param propertyName7
	 *            the propertyName7 to set
	 */
	public void setPropertyName7(String propertyName7) {
		this.propertyName7 = propertyName7;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((battery == null) ? 0 : battery.hashCode());
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((camera == null) ? 0 : camera.hashCode());
		result = prime * result + ((comms == null) ? 0 : comms.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((launch == null) ? 0 : launch.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((memory == null) ? 0 : memory.hashCode());
		result = prime * result + ((misc == null) ? 0 : misc.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modifiedOn == null) ? 0 : modifiedOn.hashCode());
		result = prime * result + ((network == null) ? 0 : network.hashCode());
		result = prime * result + ((platform == null) ? 0 : platform.hashCode());
		result = prime * result + ((productBrand == null) ? 0 : productBrand.hashCode());
		result = prime * result + ((productSubBrand == null) ? 0 : productSubBrand.hashCode());
		result = prime * result + ((property1 == null) ? 0 : property1.hashCode());
		result = prime * result + ((property2 == null) ? 0 : property2.hashCode());
		result = prime * result + ((property3 == null) ? 0 : property3.hashCode());
		result = prime * result + ((property4 == null) ? 0 : property4.hashCode());
		result = prime * result + ((property5 == null) ? 0 : property5.hashCode());
		result = prime * result + ((property6 == null) ? 0 : property6.hashCode());
		result = prime * result + ((property7 == null) ? 0 : property7.hashCode());
		result = prime * result + ((propertyName1 == null) ? 0 : propertyName1.hashCode());
		result = prime * result + ((propertyName2 == null) ? 0 : propertyName2.hashCode());
		result = prime * result + ((propertyName3 == null) ? 0 : propertyName3.hashCode());
		result = prime * result + ((propertyName4 == null) ? 0 : propertyName4.hashCode());
		result = prime * result + ((propertyName5 == null) ? 0 : propertyName5.hashCode());
		result = prime * result + ((propertyName6 == null) ? 0 : propertyName6.hashCode());
		result = prime * result + ((propertyName7 == null) ? 0 : propertyName7.hashCode());
		result = prime * result + ((series == null) ? 0 : series.hashCode());
		result = prime * result + ((sound == null) ? 0 : sound.hashCode());
		result = prime * result + ((subCategoryName == null) ? 0 : subCategoryName.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((tests == null) ? 0 : tests.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		ProductInfoDTO other = (ProductInfoDTO) obj;
		if (battery == null) {
			if (other.battery != null)
				return false;
		}
		else if (!battery.equals(other.battery))
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		}
		else if (!body.equals(other.body))
			return false;
		if (camera == null) {
			if (other.camera != null)
				return false;
		}
		else if (!camera.equals(other.camera))
			return false;
		if (comms == null) {
			if (other.comms != null)
				return false;
		}
		else if (!comms.equals(other.comms))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		}
		else if (!createdOn.equals(other.createdOn))
			return false;
		if (display == null) {
			if (other.display != null)
				return false;
		}
		else if (!display.equals(other.display))
			return false;
		if (features == null) {
			if (other.features != null)
				return false;
		}
		else if (!features.equals(other.features))
			return false;
		if (launch == null) {
			if (other.launch != null)
				return false;
		}
		else if (!launch.equals(other.launch))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		}
		else if (!link.equals(other.link))
			return false;
		if (memory == null) {
			if (other.memory != null)
				return false;
		}
		else if (!memory.equals(other.memory))
			return false;
		if (misc == null) {
			if (other.misc != null)
				return false;
		}
		else if (!misc.equals(other.misc))
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
		if (network == null) {
			if (other.network != null)
				return false;
		}
		else if (!network.equals(other.network))
			return false;
		if (platform == null) {
			if (other.platform != null)
				return false;
		}
		else if (!platform.equals(other.platform))
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
		if (property1 == null) {
			if (other.property1 != null)
				return false;
		}
		else if (!property1.equals(other.property1))
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
		if (propertyName1 == null) {
			if (other.propertyName1 != null)
				return false;
		}
		else if (!propertyName1.equals(other.propertyName1))
			return false;
		if (propertyName2 == null) {
			if (other.propertyName2 != null)
				return false;
		}
		else if (!propertyName2.equals(other.propertyName2))
			return false;
		if (propertyName3 == null) {
			if (other.propertyName3 != null)
				return false;
		}
		else if (!propertyName3.equals(other.propertyName3))
			return false;
		if (propertyName4 == null) {
			if (other.propertyName4 != null)
				return false;
		}
		else if (!propertyName4.equals(other.propertyName4))
			return false;
		if (propertyName5 == null) {
			if (other.propertyName5 != null)
				return false;
		}
		else if (!propertyName5.equals(other.propertyName5))
			return false;
		if (propertyName6 == null) {
			if (other.propertyName6 != null)
				return false;
		}
		else if (!propertyName6.equals(other.propertyName6))
			return false;
		if (propertyName7 == null) {
			if (other.propertyName7 != null)
				return false;
		}
		else if (!propertyName7.equals(other.propertyName7))
			return false;
		if (series == null) {
			if (other.series != null)
				return false;
		}
		else if (!series.equals(other.series))
			return false;
		if (sound == null) {
			if (other.sound != null)
				return false;
		}
		else if (!sound.equals(other.sound))
			return false;
		if (subCategoryName == null) {
			if (other.subCategoryName != null)
				return false;
		}
		else if (!subCategoryName.equals(other.subCategoryName))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		}
		else if (!tags.equals(other.tags))
			return false;
		if (tests == null) {
			if (other.tests != null)
				return false;
		}
		else if (!tests.equals(other.tests))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductInfoDTO [title=" + title + ", tags=" + tags + ", subCategoryName=" + subCategoryName + ", productBrand=" + productBrand
				+ ", productSubBrand=" + productSubBrand + ", series=" + series + ", model=" + model + ", link=" + link + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + ", network=" + network + ", launch=" + launch + ", body=" + body + ", display=" + display + ", platform="
				+ platform + ", memory=" + memory + ", camera=" + camera + ", sound=" + sound + ", comms=" + comms + ", features=" + features + ", battery="
				+ battery + ", misc=" + misc + ", tests=" + tests + ", property1=" + property1 + ", property2=" + property2 + ", property3=" + property3
				+ ", property4=" + property4 + ", property5=" + property5 + ", property6=" + property6 + ", property7=" + property7 + ", propertyName1="
				+ propertyName1 + ", propertyName2=" + propertyName2 + ", propertyName3=" + propertyName3 + ", propertyName4=" + propertyName4
				+ ", propertyName5=" + propertyName5 + ", propertyName6=" + propertyName6 + ", propertyName7=" + propertyName7 + "]";
	}

	public Map<String, String> populateMap(String head) {
		Map<String, String> map = new HashMap<String, String>();
		switch (head.toUpperCase()) {
		case "NETWORK":
			this.network = map;
			break;
		case "LAUNCH":
			this.launch = map;
			break;
		case "BODY":
			this.body = map;
			break;
		case "DISPLAY":
			this.display = map;
			break;
		case "PLATFORM":
			this.platform = map;
			break;
		case "MEMORY":
			this.memory = map;
			break;
		case "CAMERA":
			this.camera = map;
			break;
		case "SOUND":
			this.sound = map;
			break;
		case "COMMS":
			this.comms = map;
			break;
		case "FEATURES":
			this.features = map;
			break;
		case "BATTERY":
			this.battery = map;
			break;
		case "MISC":
			this.misc = map;
			break;
		case "TESTS":
			this.tests = map;
			break;
		default:
			if (this.property1 == null) {
				this.property1 = map;
				this.propertyName1 = head;
			}
			else if (this.property2 == null) {
				this.property2 = map;
				this.propertyName2 = head;
			}
			else if (this.property3 == null) {
				this.property3 = map;
				this.propertyName3 = head;
			}
			else if (this.property4 == null) {
				this.property4 = map;
				this.propertyName4 = head;
			}
			else if (this.property5 == null) {
				this.property5 = map;
				this.propertyName5 = head;
			}
			else if (this.property6 == null) {
				this.property6 = map;
				this.propertyName6 = head;
			}
			else if (this.property7 == null) {
				this.property7 = map;
				this.propertyName7 = head;
			}
			break;
		}
		return map;
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
	 * @return the allProperties
	 */
	public Map<String, String> getAllProperties() {
		if (network != null && network.size() > 0){
			for (String tmp : new String[] {"4G bands","3G bands"}){
				String key = tmp.toUpperCase();
				if (network.containsKey(key)){
					allProperties.put(key.split("\\s")[0], network.get(key));
				}
			}
		}
		
		if (body != null && body.size() > 0){
			for (String tmp : new String[] {"Dimensions"}){
				String key = tmp.toUpperCase();
				if (body.containsKey(key)){
					allProperties.put(key, body.get(key));
				}
			}
		}
		
		if (display != null && display.size() > 0){
			for (String tmp : new String[] {"Resolution"}){
				String key = tmp.toUpperCase();
				if (display.containsKey(key)){
					allProperties.put(key, display.get(key));
				}
			}
		}
		
		if (platform != null && platform.size() > 0){
			for (String tmp : new String[] {"OS"}){
				String key = tmp.toUpperCase();
				if (platform.containsKey(key)){
					allProperties.put(key, platform.get(key));
				}
			}
		}
		
		if (memory != null && memory.size() > 0){
			for (String tmp : new String[] {"Internal"}){
				String key = tmp.toUpperCase();
				if (memory.containsKey(key)){
					allProperties.put(key+" MEMORY", memory.get(key));
				}
			}
		}
		
		if (camera != null && camera.size() > 0){
			for (String tmp : new String[] {"Primary","Secondary"}){
				String key = tmp.toUpperCase();
				if (camera.containsKey(key)){
					allProperties.put(key+" CAMERA", camera.get(key));
				}
			}
		}
		
		if (battery != null && battery.size() > 0){
			for (String tmp : new String[] {"BATTERY_0"}){
				String key = tmp.toUpperCase();
				if (battery.containsKey(key)){
					allProperties.put(key.split("_")[0], battery.get(key));
				}
			}
		}
		return allProperties;
	}

}
