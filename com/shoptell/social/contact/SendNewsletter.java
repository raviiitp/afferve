/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.social.contact;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.domain.User;
import com.shoptell.service.SparkPostMailService;

@Named(value = "SendNewsletter")
public class SendNewsletter {

	@Inject
	private SparkPostMailService mailService;
	
	@Inject
	private BatchRepository repository;

	private List<User> users;
	
	@Async
	public void init() {
		preProcess();
		execute();
		postProcess();
	}

	@SuppressWarnings("unchecked")
	private void preProcess() {
		users = (List<User>) repository.selectAll(TableEnum.user.name(), User.class, null, null, null);
	}

	private void execute() {
		if (users != null){
			for (User user : users){
				//mailService.sendNewsletter(user.getId());
			}
		}
	}
	
	private void postProcess() {}
}
