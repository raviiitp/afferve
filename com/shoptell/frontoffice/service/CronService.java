/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.scheduling.annotation.Scheduled;

import com.shoptell.backoffice.repository.util.CashbackUtil;
import com.shoptell.db.messagelog.PreRenderLog;
import com.shoptell.service.UserService;
import com.shoptell.social.contact.SendInvitationToUnknownPerson;

@Named(value = "CronService")
public class CronService {

	@Inject
	private TicketMessageService ticketMessageService;
	
	@Inject
	private TicketService ticketService;

	@Inject
	private UserService userService;

	@Inject
	private CashbackUtil cashbackUtil;

	@Inject
	private SendInvitationToUnknownPerson sendInvitationToUnknownPerson;
	
	@Inject
	private PreRenderLog preRenderLog;

	/**
	 * Service which run every hour
	 */
	@Scheduled(cron = "0 0 0/1 * * ?")
	public void callToService() {
		cashbackUtil.bonusGenerate();
		ticketMessageService.sendMail();
		userService.sendWelcomeEmail();
		sendInvitationToUnknownPerson.start();
		preRenderLog.checkPrerenderService();
	}
	
	/**
	 * Service which run weekly
	 */
	@Scheduled(cron="0 0 14 * * WED")
	public void weeklyJobs(){
		ticketService.autoClose();
	}
}
