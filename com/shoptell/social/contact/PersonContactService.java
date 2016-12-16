/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.social.contact;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.google.api.plus.Person;

import com.shoptell.backoffice.repository.BatchRepository;

@Named(value = "PersonContactService")
public class PersonContactService {
	private static final Logger log = LoggerFactory.getLogger(PersonContactService.class);

	@Inject
	private BatchRepository repository;
	
	/**
	 * 1. if current person exists in person_not_in_contact table, move it to
	 * person_in_contact table 2. else if person already exists in
	 * person_in_contact table then do nothing 3. else save current person in
	 * person_in_contact table
	 * 
	 * @param googleProfile
	 */
	public void persistAuthenticatedUserinPersonInContact(Person googleProfile) {
		if (googleProfile == null) {
			log.debug("googleProfile is null");
			return;
		}

		String email = null;
		String fullName = null;
		String firstName = null;
		String lastName = null;
		String gender = null;
		String phoneNumber = null;
		String birthday = null;

		if (email == null) {
			email = googleProfile.getAccountEmail();
		}
		if (fullName == null) {
			fullName = googleProfile.getDisplayName();
		}
		if (firstName == null) {
			firstName = googleProfile.getGivenName();
		}
		if (lastName == null) {
			lastName = googleProfile.getFamilyName();
		}
		if (gender == null) {
			gender = googleProfile.getGender();
		}
		if (phoneNumber == null) {
		}
		if (birthday == null) {
			birthday = googleProfile.getBirthday() == null ? null : googleProfile.getBirthday().toString();
		}

		if (isNotBlank(firstName)) {
			firstName = firstName.replace("?", "").replace(".", "").trim();
			if (isNotBlank(firstName)) {
				firstName = capitalize(firstName);
			}
			else {
				firstName = "Friend";
			}
		}
		else {
			firstName = "Friend";
		}

		if (isNotBlank(lastName)) {
			lastName = lastName.replace("?", "").replace(".", "").trim();
			if (isNotBlank(lastName)) {
				lastName = capitalize(lastName);
			}
		}

		PersonInContactDTO personInContact = new PersonInContactDTO(email, fullName, firstName, lastName, gender, phoneNumber, birthday);
		repository.save(personInContact);
	}

	/**
	 * 1. if passed person exists in person_in_contact table then do nothing 2.
	 * else save person in person_not_in_contact
	 */
	public void savePersonContactinPersonNotInContact(String email, String fullName, String firstName, String lastName, String gender, String phoneNumber,
			String birthday) {
		if (isNotBlank(email)) {
			PersonInContactDTO person = new PersonInContactDTO(email, fullName, firstName, lastName, gender, phoneNumber, birthday, true);
			repository.savePersonContact(person);
		}
	}

	public void savePersonWithNoEmail(String phoneNumber, String fullName, String firstName, String lastName, String gender, String birthday, String email) {
		if (phoneNumber != null) {
			PersonWithNoEmailDTO personWithNoEmailDTO = new PersonWithNoEmailDTO(phoneNumber, fullName, firstName, lastName, gender, birthday, email);
			repository.save(personWithNoEmailDTO);
		}
	}
}
