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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.backoffice.repository.CategoryNodeRepository;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

@Named
public abstract class CategoryInfo extends Info {
	private static final Logger log = LoggerFactory.getLogger(CategoryInfo.class);
	
	@Inject
	protected CategoryNodeRepository categoryNodeRepository;

	protected String getHome() {
		return home.name();
	}

	//@Async
	public/* Future<ProcessLog> */void checkout(List<ProcessLog> list) throws InterruptedException {
		try {
			process = processUtil.start(getHome(), ProcessLogJobEnum.CATEGORY.name());
			list.add(process);
			preprocess();
			execute();
			postprocess();
			list.remove(process);
			processUtil.end(process);
		} catch (Exception e) {
			log.error("Error while checkout", e);
			msgLog.addError(e);
		}
		// return new AsyncResult<ProcessLog>(process);
	}

	protected abstract void preprocess();

	protected abstract void execute();

	protected abstract void postprocess();
}
