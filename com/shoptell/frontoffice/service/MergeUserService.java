/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
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

@Named
public class MergeUserService {

	@Inject
	private TicketService ticketService;
	
	@Inject
	private UserTransactionService userTransactionService;
	
	@Inject
	private WalletService walletService;
	
	@Inject
	private PaymentService paymentService;
	
	@Inject
	private UserBankAccountService userBankAccountService;
	
    /**
     * 
     * @param userId: current userId
     * @param finalUserId: to which current user has to be merged; merge tickets, messages, transactions etc.
     */
    public void mergeUser(String userId, String finalUserId){
    	ticketService.mergeTicketTable(userId, finalUserId);
    	userTransactionService.mergeUserTransactionsTable(userId, finalUserId);
    	walletService.mergeCBReport(userId, finalUserId);
    	paymentService.mergeCBPayment(userId, finalUserId);
    	userBankAccountService.mergeUserAccount(userId, finalUserId);
    }
}
