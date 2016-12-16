/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.web.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.domain.User;
import com.shoptell.frontoffice.service.UserBankAccountService;
import com.shoptell.repository.UserRepository;
import com.shoptell.service.MailService;
import com.shoptell.service.UserService;
import com.shoptell.web.rest.dto.UserDTO;

/**
 * REST controller for managing the current user's account.
 */
@Scope("session")
@RestController
@RequestMapping("/api")
public class AccountResource {

	@Inject
	private UserService userService;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private MailService mailService;

	@Inject
	private UserBankAccountService userBankAccountService;

	/**
	 * GET /account : get the current user.
	 */
	@RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserDTO> getAccount(HttpServletRequest request, HttpServletResponse response) {
		// SecurityUtils.getCurrentLogin() -> providerUserId
		
		User user = userService.getUserWithAuthorities();
		
		if (user != null) {
			UserDTO userDto = new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getImageUrl(), user.isActivated(),
					user.getLangKey(), user.getReferId(), user.isBroadcastNotification(), user.isMonthlyAccountStatementNotification(), user.getPersonReferred(),
					StringUtils.isNotBlank(user.getReferredBy()), user.getPhoneNumber(), user.getDob());

			return new ResponseEntity<>(userDto, HttpStatus.OK);

		}else{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param userId
	 * @param email
	 *            : email id that user needs to activate
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setDeActivatedEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> setDeactivatedEmail(@RequestParam("userId") String userId, @RequestParam("email") String email, HttpServletRequest request) {
		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		email = StringUtils.trim(email);
		if (StringUtils.isBlank(email)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		userService.setDeActivatedEmail(userId, email);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/useReferIdCtrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> useReferIdCtrl(@RequestParam("userId") String userId, @RequestParam("referId") String referId, HttpServletRequest request) {
		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		referId = StringUtils.trim(referId);
		if (StringUtils.isBlank(referId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Map<String, String> statusMap = new HashMap<String, String>();

		String referStatus = userService.setReferredBy(userId, referId);
		statusMap.put("referStatus", referStatus);

		if (StringUtils.equalsIgnoreCase(referStatus, ReferCodeEnum.OK.toString())) {
			return new ResponseEntity<>(statusMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(statusMap, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/mailPartnerURLToUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> mailPartnerURLToUser(@RequestParam("userid") String userid, @RequestParam("url") String url, HttpServletRequest request) {
		mailService.sendRequestUrl(userid, url);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/publicNotificationCtrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> publicNotificationCtrl(@RequestParam("userId") String userId, @RequestParam("publicNotification") boolean publicNotification,
			HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		userService.setPublicNotification(userId, publicNotification);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/monthlyAccountStatementCtrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> monthlyAccountStatementCtrl(@RequestParam("userId") String userId, @RequestParam("monthlyAccountStatementNotification") boolean monthlyAccountStatementNotification,
			HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		userService.setMonthlyAccountStatementCtrl(userId, monthlyAccountStatementNotification);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/setPhoneNumber", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> setPhoneNumber(@RequestParam("userId") String userId, @RequestParam("phoneNumber") String phoneNumber, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		phoneNumber = StringUtils.trim(phoneNumber);
		if (StringUtils.isBlank(phoneNumber)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else if (phoneNumber.length() != 10 || !StringUtils.isNumeric(phoneNumber) || phoneNumber.startsWith("0")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String oldPhoneNumber = userService.setPhoneNumber(userId, phoneNumber);
		
		List<User> users = userRepository.findUsersById(userId);
		if (users != null && users.size() > 0) {
			UserAccountDTO userAccountDTO = new UserAccountDTO();
			userAccountDTO.setAccountHolderName(users.get(0).getFirstName());
			userAccountDTO.setAccountNumber(phoneNumber);
			userAccountDTO.setBankName("AFFERVE");
			userAccountDTO.setIfscCode(phoneNumber);
			userAccountDTO.setUserId(userId);
			userAccountDTO.setUserName(users.get(0).getFirstName());
			userBankAccountService.saveBankAccount(userAccountDTO, phoneNumber, oldPhoneNumber);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/setDOB", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> setDOB(@RequestParam("userId") String userId, @RequestParam("dob") long dob, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Date dt = new Date(dob);

		userService.setDOB(userId, dt);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/setReferCodeCtrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> setReferCode(@RequestParam("userId") String userId, @RequestParam("referCode") String referCode,
			@RequestParam("save") boolean save, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		referCode = referCode.replaceAll("\\s", "").trim();
		if (StringUtils.isBlank(referCode) || referCode.length() < 4 || referCode.length() > 15) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		ReferCodeEnum referCodeEnum = userService.setReferCode(userId, referCode, save);
		
		Map<String, String> statusMap = new HashMap<String, String>();
		statusMap.put("referStatus", referCodeEnum.name());
		
		return new ResponseEntity<>(statusMap, HttpStatus.OK);
	}
	
}
