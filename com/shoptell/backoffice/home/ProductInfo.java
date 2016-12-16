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

import static com.shoptell.db.messagelog.MessageEnum.INFO;

import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogJobEnum;

@Named
public abstract class ProductInfo extends Info {
	private static final Logger log = LoggerFactory.getLogger(ProductInfo.class);
	
	protected String getHome() {
		return home.name();
	}

	public void checkout(List<ProcessLog> list) {
		process = processUtil.start(getHome(), ProcessLogJobEnum.PRODUCT.name());
		list.add(process);
		preprocess();
		try {
			execute();
		} catch (Exception e) {
			msgLog.add(INFO, "KILLED DATA THREAD", "KILLED DATA THREAD");
		}
		postprocess();
		list.remove(process);
		processUtil.end(process);
	}
	
	public void updater(List<ProcessLog> list) {
		process = processUtil.start(getHome(), ProcessLogJobEnum.UPDATE.name());
		list.add(process);
		if (!HomeEnum.SHOPCLUES.name().equalsIgnoreCase(getHome())) {
			preprocess();
		}
		try {
			priceUpdater();
		} catch (Exception e) {
			log.error("Error while updating", e);
			msgLog.add(INFO, "KILLED UPDATE THREAD", "KILLED UPDATE THREAD");
		} finally {
			//batchRepository.saveUpdatePrice();
		}
		list.remove(process);
		processUtil.end(process);
	}

	protected abstract void preprocess();

	protected abstract void execute() throws InterruptedException;
	
	protected abstract void priceUpdater() throws InterruptedException;

	protected abstract void postprocess();
}
