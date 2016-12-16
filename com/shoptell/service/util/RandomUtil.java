/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.service.util;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

	private static final int DEF_COUNT = 20;
	
	private static AtomicLong idCounter = new AtomicLong(0);

	private RandomUtil() {
	}

	/**
	 * Generates a password.
	 *
	 * @return the generated password
	 */
	public static String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
	}

	/**
	 * Generates an activation key.
	 *
	 * @return the generated activation key
	 */
	public static String generateActivationKey() {
		return RandomStringUtils.randomNumeric(DEF_COUNT);
	}

	/**
	 * Generates a reset key.
	 *
	 * @return the generated reset key
	 */
	public static String generateResetKey() {
		return RandomStringUtils.randomNumeric(DEF_COUNT);
	}

	/**
	 * Generates a refer code.
	 *
	 * @return the generated refer code
	 */
	public static String generateReferCode() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		return getMeUniqueString(cal);
	}

	private static String getMeUniqueString(Calendar cal) {
		StringBuilder unq = new StringBuilder();
		//unq.append(RandomStringUtils.randomAlphanumeric(2));
		//unq.append(Character.toString((char) ('A' + (cal.get(Calendar.YEAR) % 100 - 16))));
		unq.append(Character.toString((char) ('n' + (cal.get(Calendar.MONTH) % 100))));
		unq.append(Character.toString((char) ('@' + (cal.get(Calendar.DAY_OF_MONTH) % 100))));
		unq.append(Character.toString((char) ('c' + (cal.get(Calendar.HOUR_OF_DAY) % 100))));
		unq.append(getMeChar(cal.get(Calendar.MINUTE) % 100));
		unq.append(getMeChar(cal.get(Calendar.SECOND) % 100));
		unq.append(getMeChar(idCounter.getAndIncrement()));
		idCounter.compareAndSet(62, 0);
		
		/*
		unq.append(cal.get(Calendar.MILLISECOND));*/
		return unq.toString();
	}

	private static String getMeChar(long l) {
		if (l <= 9) {
			return Character.toString((char) ('0' + l));
		} else if (l >= 10 && l <= 35) {
			return Character.toString((char) ('a' + l - 10));
		} else if (l >= 36 && l <= 61) {
			return Character.toString((char) ('A' + l - 36));
		}
		return String.valueOf(l);
	}
}
