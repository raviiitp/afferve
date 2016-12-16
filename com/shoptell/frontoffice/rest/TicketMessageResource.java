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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.frontoffice.service.TicketMessageService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
@RequestMapping("/api/setting")
public class TicketMessageResource {

	@Inject
	private UserService userService;
	
	@Inject
	private TicketMessageService ticketMessageService;
	
	@RequestMapping(value = "/getMessageList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getMessageList(@RequestParam("userId") String userId, @RequestParam("ticketId") String ticketId, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<TicketMessageDTO> messageList = ticketMessageService.getMessages(ticketId);
		if(messageList == null){
			messageList = new LinkedList<TicketMessageDTO>();
		}
		return new ResponseEntity<>(messageList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/saveTicket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> saveTicket(@RequestParam("userId") String userId, 
			@RequestParam("ticketId") String ticketId, @RequestParam("isUser") boolean isUser,
			@RequestBody String message, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ticketId = StringUtils.trim(ticketId);
		if(StringUtils.isBlank(ticketId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		message = StringUtils.trim(message);
		if(StringUtils.isBlank(message)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ticketMessageService.saveTicket(userId, ticketId, isUser, message);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
