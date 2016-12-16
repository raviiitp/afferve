/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.ebay;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EbayLogHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger log = LoggerFactory.getLogger(EbayLogHandler.class);
	
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		log(context);
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		log(context);
		return true;
	}

	@Override
	public void close(MessageContext messageContext) {
	}

	private void log(SOAPMessageContext messageContext) {
		boolean request = ((Boolean) messageContext
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();
		if (request) {
			//log.debug("SOAP Request message:  ");
		} else {
			//log.debug("SOAP Response message:  ");
		}

		//SOAPMessage meg = messageContext.getMessage();
		try {
			//meg.writeTo(out);
			//out.println("");
		} catch (Exception e) {
			log.error("EBAY SOAP HANDLER", e);
		}
	}

}
