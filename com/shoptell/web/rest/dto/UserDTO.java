/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.web.rest.dto;

import java.util.Date;
import javax.validation.constraints.Size;

public class UserDTO {

	private String userId;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Size(min = 2, max = 100)
	private String email;

	private String imageUrl;
	
	private boolean activated;

	@Size(min = 1, max = 5)
	private String langKey;

    private String referId;
    
    private boolean broadcastNotification;
    
    private boolean monthlyAccountStatementNotification;
    
    private int personReferred;
    
    private boolean referredBySomeone;
    
    private String phoneNumber;
    
    private Date dob;
    
/*	private List<String> roles;*/

	public UserDTO() {
		super();
	}

	public UserDTO(String userId, String firstName, String lastName, String email, String imageUrl, boolean activated, String langKey, String referId, boolean broadcastNotification, boolean monthlyAccountStatementNotification,  int personReferred, boolean referredBySomeone, String phoneNumber, Date dob) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.imageUrl = imageUrl;
		this.activated = activated;
		this.langKey = langKey;
		this.referId = referId;
		this.broadcastNotification = broadcastNotification;
		this.monthlyAccountStatementNotification = monthlyAccountStatementNotification;
		this.personReferred = personReferred;
		this.referredBySomeone = referredBySomeone;
		this.phoneNumber = phoneNumber;
		this.dob = dob;
	}



	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isActivated(){
		return activated;
	}
	
	public String getLangKey() {
		return langKey;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getReferId() {
		return referId;
	}

	public boolean isBroadcastNotification() {
		return broadcastNotification;
	}

	public int getPersonReferred() {
		return personReferred;
	}
	
	public boolean isReferredBySomeone() {
		return referredBySomeone;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Date getDob() {
		return dob;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public void setReferId(String referId) {
		this.referId = referId;
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

	public void setPersonReferred(int personReferred) {
		this.personReferred = personReferred;
	}

	public void setReferredBySomeone(boolean referredBySomeone) {
		this.referredBySomeone = referredBySomeone;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "UserDTO [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", imageUrl=" + imageUrl
				+ ", activated=" + activated + ", langKey=" + langKey + ", referId=" + referId + ", broadcastNotification=" + broadcastNotification
				+ ", monthlyAccountStatementNotification=" + monthlyAccountStatementNotification + ", personReferred=" + personReferred
				+ ", referredBySomeone=" + referredBySomeone + ", phoneNumber=" + phoneNumber + ", dob=" + dob + "]";
	}
	
}
