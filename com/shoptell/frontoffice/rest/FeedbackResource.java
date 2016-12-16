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
import com.shoptell.frontoffice.service.FeedbackService;

@Scope("session")
@RestController
@RequestMapping("/feedback")
public class FeedbackResource {
	
	@Inject
	private FeedbackService feedbackService;

	@RequestMapping(value = "/saveFeedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> saveFeedback(@RequestParam("email") String email, 
			@RequestParam("mobileNumber") String mobileNumber,
			@RequestParam("message") String message, HttpServletRequest request) {

		email = StringUtils.trim(email);
		message = StringUtils.trim(message);
		if(StringUtils.isNotBlank(email) && StringUtils.isNotBlank(message)){
			feedbackService.saveFeedback(email, mobileNumber, message);
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
}
