/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.rest;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.frontoffice.service.UserBankAccountService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
@RequestMapping("/api/setting")
public class UserBankAccountResource {

	@Inject
	private UserService userService;

	@Inject
	private UserBankAccountService userBankAccountService;

	// private Logger log =
	// LoggerFactory.getLogger(UserBankAccountResource.class);

	@RequestMapping(value = "/saveBankAccount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> saveBankAccount(@RequestParam("oldAccountNumber") String oldAccountNumber,
			@RequestParam("reEnteredAccountNumber") String reEnteredAccountNumber, @Valid @RequestBody UserAccountDTO userAccountDTO, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		if (!userService.isLoggedInUserRequested(userAccountDTO.getUserId())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		oldAccountNumber = StringUtils.trim(oldAccountNumber);
		reEnteredAccountNumber = StringUtils.trim(reEnteredAccountNumber);

		boolean status = true;
		List<String> errorList = null;

		if (result.hasErrors()) {
			if (errorList == null) {
				errorList = new LinkedList<>();
			}
			for (ObjectError error : result.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			status = false;
			if (!StringUtils.isNumeric(userAccountDTO.getAccountNumber())) {
				errorList.add(HTTPErrorStatusEnum.AccountNumber.name());
			}
			if (!StringUtils.isNumeric(reEnteredAccountNumber)) {
				errorList.add(HTTPErrorStatusEnum.ReEnteredAccountNumber.name());
			}
			if (!StringUtils.equals(reEnteredAccountNumber, userAccountDTO.getAccountNumber())) {
				errorList.add(HTTPErrorStatusEnum.ACCOUNT_NUMBER_MISMATCH.name());
			}
		} else {
			if (!StringUtils.isNumeric(userAccountDTO.getAccountNumber())) {
				if (errorList == null) {
					errorList = new LinkedList<>();
				}
				errorList.add(HTTPErrorStatusEnum.AccountNumber.name());
				status = false;
			}
			if (!StringUtils.isNumeric(reEnteredAccountNumber)) {
				if (errorList == null) {
					errorList = new LinkedList<>();
				}
				errorList.add(HTTPErrorStatusEnum.ReEnteredAccountNumber.name());
				status = false;
			}
			if (!StringUtils.equals(reEnteredAccountNumber, userAccountDTO.getAccountNumber())) {
				if (errorList == null) {
					errorList = new LinkedList<>();
				}
				errorList.add(HTTPErrorStatusEnum.ACCOUNT_NUMBER_MISMATCH.name());
				status = false;
			}
			if(status){
				if (errorList == null) {
					errorList = new LinkedList<>();
				}
				status = userBankAccountService.saveBankAccount(userAccountDTO, reEnteredAccountNumber, oldAccountNumber);
				if (status) {
					errorList.add(HTTPErrorStatusEnum.OK.name());
				} else {
					errorList.add(HTTPErrorStatusEnum.SERVER_ERROR.name());
				}
			}
		}

		HTTPErrorStringList httPErrorStringList = new HTTPErrorStringList(errorList);
		if (status) {
			return new ResponseEntity<>(httPErrorStringList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(httPErrorStringList, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@RequestMapping(value = "/getBankAccount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getBankAccount(@RequestParam("userId") String userId, HttpServletRequest request) {
		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<UserAccountDTO> userAccountDTOList = userBankAccountService.getBankAccountListById(userId);
		if (userAccountDTOList == null) {
			userAccountDTOList = new LinkedList<UserAccountDTO>();
		}
		return new ResponseEntity<>(userAccountDTOList, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteBankAccount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> deleteBankAccount(@RequestParam("userId") String userId, @RequestParam("accountNumber") String accountNumber,
			HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		userBankAccountService.deleteBankAccount(userId, accountNumber);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
