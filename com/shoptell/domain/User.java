/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A user.
 */
@Table(keyspace = "afferve", name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@PartitionKey
    private String id; //afferve user id

	@ClusteringColumn
    private String login;
    
    private String firstName;

    private String lastName;
    
    private String password;

    private String email;
    
    private String imageUrl;
    
	private boolean activated;
	
    @Column(name = "activation_key")
    private String activationKey;

    @Column(name = "lang_key")
    private String langKey;
    
    private String referId;
    
    private String referredBy;
    
    private boolean broadcastNotification;
    
    private boolean monthlyAccountStatementNotification;
    
    private String phoneNumber;
    
    private Date dob;
    
    private int personReferred;
    
    private Date registeredOn;

    @JsonIgnore
    private Set<String> authorities = new HashSet<>();

	public User() {
		super();
	}

	public User(String id, String login, String firstName, String lastName, String password, String email, String imageUrl, boolean activated,
			String activationKey, String langKey, String referId, String referredBy, boolean broadcastNotification,
			boolean monthlyAccountStatementNotification, String phoneNumber, Date dob, int personReferred, Date registeredOn, Set<String> authorities) {
		super();
		this.id = id;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.imageUrl = imageUrl;
		this.activated = activated;
		this.activationKey = activationKey;
		this.langKey = langKey;
		this.referId = referId;
		this.referredBy = referredBy;
		this.broadcastNotification = broadcastNotification;
		this.monthlyAccountStatementNotification = monthlyAccountStatementNotification;
		this.phoneNumber = phoneNumber;
		this.dob = dob;
		this.personReferred = personReferred;
		this.registeredOn = registeredOn;
		this.authorities = authorities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getReferId() {
		return referId;
	}

	public void setReferId(String referId) {
		this.referId = referId;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public boolean isBroadcastNotification() {
		return broadcastNotification;
	}

	public void setBroadcastNotification(boolean broadcastNotification) {
		this.broadcastNotification = broadcastNotification;
	}

	public boolean isMonthlyAccountStatementNotification() {
		return monthlyAccountStatementNotification;
	}

	public void setMonthlyAccountStatementNotification(boolean monthlyAccountStatementNotification) {
		this.monthlyAccountStatementNotification = monthlyAccountStatementNotification;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public int getPersonReferred() {
		return personReferred;
	}

	public void setPersonReferred(int personReferred) {
		this.personReferred = personReferred;
	}

	public Date getRegisteredOn() {
		return registeredOn;
	}

	public void setRegisteredOn(Date registeredOn) {
		this.registeredOn = registeredOn;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (activated ? 1231 : 1237);
		result = prime * result + ((activationKey == null) ? 0 : activationKey.hashCode());
		result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
		result = prime * result + (broadcastNotification ? 1231 : 1237);
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((langKey == null) ? 0 : langKey.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + (monthlyAccountStatementNotification ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + personReferred;
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((referId == null) ? 0 : referId.hashCode());
		result = prime * result + ((referredBy == null) ? 0 : referredBy.hashCode());
		result = prime * result + ((registeredOn == null) ? 0 : registeredOn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (activated != other.activated)
			return false;
		if (activationKey == null) {
			if (other.activationKey != null)
				return false;
		} else if (!activationKey.equals(other.activationKey))
			return false;
		if (authorities == null) {
			if (other.authorities != null)
				return false;
		} else if (!authorities.equals(other.authorities))
			return false;
		if (broadcastNotification != other.broadcastNotification)
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (langKey == null) {
			if (other.langKey != null)
				return false;
		} else if (!langKey.equals(other.langKey))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (monthlyAccountStatementNotification != other.monthlyAccountStatementNotification)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (personReferred != other.personReferred)
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (referId == null) {
			if (other.referId != null)
				return false;
		} else if (!referId.equals(other.referId))
			return false;
		if (referredBy == null) {
			if (other.referredBy != null)
				return false;
		} else if (!referredBy.equals(other.referredBy))
			return false;
		if (registeredOn == null) {
			if (other.registeredOn != null)
				return false;
		} else if (!registeredOn.equals(other.registeredOn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", firstName=" + firstName + ", lastName=" + lastName + ", password=" + password + ", email=" + email
				+ ", imageUrl=" + imageUrl + ", activated=" + activated + ", activationKey=" + activationKey + ", langKey=" + langKey + ", referId=" + referId
				+ ", referredBy=" + referredBy + ", broadcastNotification=" + broadcastNotification + ", monthlyAccountStatementNotification="
				+ monthlyAccountStatementNotification + ", phoneNumber=" + phoneNumber + ", dob=" + dob + ", personReferred=" + personReferred
				+ ", registeredOn=" + registeredOn + ", authorities=" + authorities + "]";
	}
	
	public String getName(){
		String name = "Friend";
		if (StringUtils.isNotBlank(firstName)) {
			firstName = StringUtils.capitaliseAllWords(firstName.trim().toLowerCase());
			name = firstName;
			if (StringUtils.isNotBlank(lastName)) {
				lastName = StringUtils.capitaliseAllWords(lastName.trim().toLowerCase());
				name = name +" "+lastName;
			}
		}
		return name;
	}
}
