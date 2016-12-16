/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.messagelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.service.MailService;

@Named(value = "PreRenderLog")
public class PreRenderLog {
	private static final Logger log = LoggerFactory.getLogger(PreRenderLog.class);

	@Inject
	private MailService mailService;

	@Inject
	private MessageLogUtil messageLogUtil;

	private boolean isPrerenderRunning(StringBuilder message) {
		String process;
		BufferedReader input = null;
		boolean server_js_running = false;
		try {
			Process p = Runtime.getRuntime().exec("sudo forever list");
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) {
				if (process.contains("/usr/local/prerender/server.js")) {
					server_js_running = true;
				}
				message.append(process).append("\n");
			}
		} catch (Exception e) {
			log.error("Exception in PreRenderLog", e);
			messageLogUtil.addError(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
		return server_js_running;
	}

	@Async
	public void checkPrerenderService() {
		log.info("checkPrerenderService() Enter");
		try {
			String subject = StringUtils.EMPTY;
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			StringBuilder message = new StringBuilder();
			if (!isPrerenderRunning(message)) {
				boolean isStarted = reStartPrerender(message);
				if (!isStarted) {
					message.append("Prerender not running");
					subject = "Error! Prerender not running at " + localMachine.getHostName();
					mailService.sendEmail("ravi@afferve.com", subject, message.toString(), false, true);
				}
			}
		} catch (Exception err) {
		}
		log.info("checkPrerenderService() Exit");
	}

	private boolean reStartPrerender(StringBuilder message) {
		try {
			Runtime.getRuntime().exec("sudo pkill -f prerender");
			Thread.sleep(1000);
			Runtime.getRuntime().exec("nohup sudo forever start /usr/local/prerender/server.js &");
			Thread.sleep(5000);
		} catch (Exception e) {
			log.error("Exception in PreRenderLog", e);
			messageLogUtil.addError(e);
		} finally {
			if (isPrerenderRunning(message)) {
				return true;
			}
		}
		return false;
	}
}
