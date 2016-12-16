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

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.domain.UserNotificationDTO;
import com.shoptell.frontoffice.service.WalletService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
@RequestMapping("/api/setting")
public class WalletResource {

	@Inject
	private UserService userService;

	@Inject
	private BatchRepository batchRepository;

	@Inject
	private WalletService walletService;

	@RequestMapping(value = "/getWalletReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getWalletReport(@RequestParam("userId") String userId, @RequestParam(value = "page", required = false) Integer page,
			HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		// userId, pagination
		List<CBReportDTO> walletReportList = walletService.getWalletReportWithPagination(userId, page);
		if (walletReportList == null) {
			walletReportList = new LinkedList<CBReportDTO>();
		}
		return new ResponseEntity<>(walletReportList, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateMyMoneyLastVisit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> updateMyMoneyLastVisit(@RequestParam("userId") String userId, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		UserNotificationDTO userNotification = batchRepository.getUserNotification(userId);
		if (userNotification == null) {
			userNotification = new UserNotificationDTO(userId);
		}
		else {
			userNotification.setMyMoneyLastVisit(UUIDs.timeBased());
		}

		batchRepository.save(userNotification);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/getMyMoneyLastVisit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getMyMoneyLastVisit(@RequestParam("userId") String userId, HttpServletRequest request) {

		userId = StringUtils.trim(userId);
		if (StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		UserNotificationDTO userNotification = batchRepository.getUserNotification(userId);
		if (userNotification == null) {
			userNotification = new UserNotificationDTO(userId);
		}

		Map<String, Date> map = new HashMap<String, Date>();
		map.put("myMoneyLastVisit_date", new Date(UUIDs.unixTimestamp(userNotification.getMyMoneyLastVisit())));

		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}
