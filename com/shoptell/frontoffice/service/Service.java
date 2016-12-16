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

import org.springframework.core.env.Environment;

import com.datastax.driver.core.Session;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.util.stproperties.STProperties;

@Named
public abstract class Service {

	@Inject
	protected Session session;
	@Inject
	protected BatchRepository repository;
	@Inject
	protected Environment env;
	@Inject
	protected MessageLogUtil msgLog;
	@Inject
	protected STProperties stprop;
	@Inject
	protected ProcessLogUtil processUtil;
	protected String keyspace;
	protected String tableName;
	protected ProcessLog process;
}
