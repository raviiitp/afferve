/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.scheduling.annotation.Async;

import com.shoptell.domain.User;
import com.shoptell.repository.UserRepository;

@Named(value = "AlterRegisteredUser")
public class AlterRegisteredUser {

	@Inject
	private UserRepository userRepo;
	
	@Async
	public void init(){
		List<User> userList = userRepo.getAllUsers();
		
		for(User user : userList){
	        user.setReferId(null);
		}
		
		userRepo.batchSaveUsers(userList, false);
	}
	
	
}
