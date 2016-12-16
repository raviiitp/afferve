/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.processlog;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.shoptell.backoffice.repository.BatchRepository;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class ProcessLogUtil {
	@Inject
	BatchRepository batchRepository;
	
	public static final String START_STATUS = "PROCESSING";
	public static final String END_STATUS = "COMPLETE";
	public static final String KILL_STATUS = "INCOMPLETE";

	public ProcessLog start(String home, String job) {
		ProcessLog log = new ProcessLog(home, job, START_STATUS);
		batchRepository.save(log);
		return log;
	}

	public void end(ProcessLog log) {
		log.setStatus(END_STATUS);
		log.setCompletedOn(new Date(System.currentTimeMillis()));
		batchRepository.save(log);
	}

	public void kill(List<ProcessLog> log) {
		if (log == null)
			return;
		try {
			for (ProcessLog process : log) {
				if (process != null) {
					process.setStatus(KILL_STATUS);
					process.setCompletedOn(new Date(System.currentTimeMillis()));
				}
			}
			batchRepository.batchSave(log);
		} catch (Exception e) {
		}
	}
}
