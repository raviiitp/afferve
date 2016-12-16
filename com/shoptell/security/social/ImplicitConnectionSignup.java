/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.security.social;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.google.api.Google;
import org.springframework.stereotype.Component;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.service.UserService;
import com.shoptell.social.contact.PersonContactService;
import com.shoptell.social.repository.SocialRepository;

@Component
public class ImplicitConnectionSignup implements ConnectionSignUp {

	private final Logger log = LoggerFactory.getLogger(ImplicitConnectionSignup.class);

	@Inject
	private UserService userService;

	@Inject
	private PersonContactService personContactService;

	@Inject
	private SocialRepository socialRepository;

	@Inject
	protected MessageLogUtil msgLog;

	public ImplicitConnectionSignup() {

	}

	@PostConstruct
	private void init() {
		// hack for the login of facebook. video_upload_limits, address removed.
		// https://github.com/spring-projects/spring-social-facebook/issues/181
		try {
			String[] fieldsToMap = { "id", "about", "age_range", "bio", "birthday", "context", "cover", "currency", "devices", "education", "email",
					"favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type",
					"is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political",
					"quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group",
					"timezone", "third_party_id", "updated_time", "verified", "viewer_can_send_gift", "website", "work" };

			Field field = Class.forName("org.springframework.social.facebook.api.UserOperations").getDeclaredField("PROFILE_FIELDS");
			field.setAccessible(true);

			Field modifiers = field.getClass().getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, fieldsToMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String execute(Connection<?> connection) {
		try {
			UserProfile profile = connection.fetchUserProfile();
			String loginId = connection.getKey().getProviderUserId();

			/*
			 * isNewUser: true -> new user | isNewUser: false -> old user
			 */
			if(profile != null){
				boolean isNewUser = userService.saveUser(profile, loginId, connection.getImageUrl());
				if (isNewUser) {
					if (StringUtils.equalsIgnoreCase(connection.getKey().getProviderId(), "google")) {
						persistPersonNotInContact(connection);
					}
				} else {
					List<String> localUserIds = socialRepository.findUserIdsWithConnection(connection);
					if ((localUserIds == null || localUserIds.size() == 0)) {
						if (StringUtils.equalsIgnoreCase(connection.getKey().getProviderId(), "google")) {
							persistPersonNotInContact(connection);
						}
					}
				}
			}
			return loginId;
		} catch (Exception e) {
			log.error("SOCIAL LOGIN ERROR", e);
			msgLog.addError(e);
			return null;
		}
	}

	private void persistPersonNotInContact(Connection<?> connection) {

		Google google = (Google) connection.getApi();

		ContactsService service = new ContactsService("Afferve");

		service.setPrivateHeader("Authorization", "Bearer " + google.getAccessToken());

		personContactService.persistAuthenticatedUserinPersonInContact(google.plusOperations().getGoogleProfile());

		try {
			saveBasicContacts(service);
		} catch (ServiceException | IOException e) {
			e.printStackTrace();
		}
	}

	public void saveBasicContacts(ContactsService myService) throws ServiceException, IOException {
		URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
		Query myQuery = new Query(feedUrl);
		myQuery.setMaxResults(Integer.MAX_VALUE);
		ContactFeed resultFeed = myService.getFeed(myQuery, ContactFeed.class);

		for (ContactEntry entry : resultFeed.getEntries()) {
			List<Email> emailList = null;
			String fullName = null;
			String firstName = null;
			String lastName = null;
			String gender = null;
			String phoneNumber = null;
			String birthday = null;

			if (entry.hasName()) {
				Name name = entry.getName();
				if (name.hasFullName()) {
					fullName = name.getFullName().getValue();
					if (StringUtils.isNotBlank(fullName)){
						fullName = StringUtils.capitalize(fullName);
					}
				}
				if (name.hasGivenName()) {
					firstName = name.getGivenName().getValue();
					if (StringUtils.isNotBlank(firstName)) {
						firstName = firstName.replace("?", "").replace(".", "").trim();
						if (StringUtils.isNotBlank(firstName)) {
							firstName = StringUtils.capitalize(firstName);
						}
					}
				}
				if (name.hasFamilyName()) {
					lastName = name.getFamilyName().getValue();
					if (StringUtils.isNotBlank(lastName)) {
						lastName = lastName.replace("?", "").replace(".", "").trim();
						if (StringUtils.isNotBlank(lastName)) {
							lastName = StringUtils.capitalize(lastName);
						}
					}
				}
			}
			
			if (StringUtils.isBlank(firstName)) {
				if (StringUtils.isNotBlank(fullName)){
					firstName = fullName;
				}
				else {
					firstName = "Friend";
				}
			}

			if (entry.hasEmailAddresses()) {
				emailList = entry.getEmailAddresses();
			}

			if (entry.hasBirthday()) {
				birthday = entry.getBirthday().getValue();
			}

			if (entry.hasGender()) {
				gender = entry.getGender().getValue().toString();
			}
			if (entry.hasPhoneNumbers()) {
				phoneNumber = entry.getPhoneNumbers().get(0).getPhoneNumber();
			}
			
			String emailAddress = null;
			boolean persisted = false;
			
			if (emailList != null) {
				for(Email email : emailList){
					emailAddress = email.getAddress();
					if(StringUtils.isNotBlank(emailAddress)){
						emailAddress = emailAddress.trim().toLowerCase();
						if(emailAddress.contains("gmail") || emailAddress.contains("rediff") || emailAddress.contains("yahoo.com") || emailAddress.contains("yahoo.co.in") || emailAddress.contains("hotmail")){
							personContactService.savePersonContactinPersonNotInContact(emailAddress, fullName, firstName, lastName, gender, phoneNumber, birthday);
							persisted = true;
						}
					}
				}
			}
			
			if(phoneNumber != null && !persisted){
				phoneNumber = phoneNumber.replaceAll("(\\s+|-)", "");
				if(phoneNumber.length() > 9){
					personContactService.savePersonWithNoEmail(phoneNumber, fullName, firstName, lastName, gender, birthday, emailAddress);
				}
			}
		}
	}
}