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
import java.util.UUID;

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
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.frontoffice.service.TicketService;
import com.shoptell.service.UserService;

@Scope("session")
@RestController
@RequestMapping("/api/setting")
public class TicketResource {
	
	@Inject
	private UserService userService;
	
	@Inject
	private TicketService ticketService;

	@RequestMapping(value = "/getTicketList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getTicketList(@RequestParam("userId") String userId, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<TicketDTO> ticketList = ticketService.getTicketsForUser(userId);
		if(ticketList == null){
			ticketList = new LinkedList<TicketDTO>();
		}
		return new ResponseEntity<>(ticketList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/closeTicket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> closeTicket(@RequestParam("userId") String userId, @RequestParam("ticketId") String ticketId, @RequestParam("time") UUID time, HttpServletRequest request) {
		
		userId = StringUtils.trim(userId);
		if(StringUtils.isBlank(userId) || !userService.isLoggedInUserRequested(userId)){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ticketService.closeTicket(ticketId, time, true);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
