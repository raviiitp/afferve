/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.db.processlog;

public enum ProcessLogJobEnum {
	ALL,
	CATEGORY,
	PRODUCT,
	POPULAR_PRODUCT,
	REVIEW,
	MERGE,
	RANK,
	UPDATE,
	SYNC_PRODUCTS,
	HOT_UPDATE, DATA_CRUNCH;
}
