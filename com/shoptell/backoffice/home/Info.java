/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home;

import javax.inject.Inject;
import javax.inject.Named;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogUtil;

@Named
public abstract class Info {
	@Inject
	protected BatchRepository batchRepository;
	@Inject
	protected MessageLogUtil msgLog;
	@Inject
	protected ProcessLogUtil processUtil;

	protected HomeEnum home;
	protected ProcessLog process;
}
