/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.messagelog;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.shoptell.backoffice.BackofficeConstants;
import com.shoptell.backoffice.repository.BatchRepository;

/**
 * @author abhishekagarwal
 *
 */
@Named(value="MessageLogUtil")
public class MessageLogUtil {
	private static List<MessageLog> messageLogList = new LinkedList<MessageLog>();
	
	@Inject
	BatchRepository batchRepository;
	
	public synchronized void add(MessageEnum severity, String description, String message){
		StackTraceElement line = Thread.currentThread().getStackTrace()[3];
		MessageLog msg = new MessageLog(severity.name(), new Date(System.currentTimeMillis()), description, "Issue At "+line+" \n"+ message);
		messageLogList.add(msg);
		if (messageLogList.size() > BackofficeConstants.BATCHSIZE){
			persist();
		}
	}

//	public synchronized void add(MessageEnum severity, String description, String message, String...elements) {
//		message = formatMesaage(message,elements);
//		add(severity, description, message);
//	}
	
//	private String formatMesaage(String message, String[] elements) {
//		for(String element : elements){
//			message = message.replaceFirst("\\{\\}", element);
//		}
//		return message;
//	}

	@PreDestroy
	private synchronized void persist() {
		List<MessageLog> logList = messageLogList;
		messageLogList = new LinkedList<MessageLog>();
		
		batchRepository.batchSave(logList);
		logList.clear();
	}

//	public void add(MessageEnum severity, Exception e) {
//		add(severity, ExceptionUtils.getStackTrace(e), e.getMessage());
//	}
	
	public void addError(Exception e) {
		add(MessageEnum.ERROR, ExceptionUtils.getStackTrace(e), e.getMessage());
	}
}
