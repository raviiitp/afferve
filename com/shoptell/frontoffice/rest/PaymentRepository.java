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
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.frontoffice.service.PaymentService;
import com.shoptell.frontoffice.service.UserBankAccountService;
import com.shoptell.service.MailService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
@RequestMapping("/api/setting")
public class PaymentRepository {

	@Inject
	private UserService userService;
	
	@Inject
	private PaymentService paymentService;
	
	@Inject
	private UserBankAccountService userBankAccountService;
	
	@Inject
	private MailService mail;
	
	@RequestMapping(value = "/getPaymentReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getPaymentReport(@RequestParam("userId") String userId, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<CBPaymentDTO> paymentReportList = paymentService.getPaymentReport(userId);
		if(paymentReportList == null){
			paymentReportList = new LinkedList<CBPaymentDTO>();
		}
		return new ResponseEntity<>(paymentReportList, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/transferMoneyToAccount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> transferMoneyToAccount(@RequestParam("userId") String userId, @RequestParam("accountNumber") String accountNumber, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		UserAccountDTO transferToAccount = userBankAccountService.getBankAccount(userId, accountNumber);
		if(transferToAccount != null){
			CBPaymentDTO payment = paymentService.transferMoneyToAccount(userId, transferToAccount);
			mail.transferMoneyToAccount(payment);
			return new ResponseEntity<>(HttpStatus.OK);
		} else{
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
