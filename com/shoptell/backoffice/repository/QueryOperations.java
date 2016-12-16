/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository;

import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public enum QueryOperations {
	EQ, LTE, GTE, GT, LT;

	public Clause getQuery(String key, Object value) {
		if (this == LTE){
			return QueryBuilder.lte(key, value);
		}
		else if (this == EQ){
			return QueryBuilder.eq(key, value);
		}
		else if (this == GTE){
			return QueryBuilder.gte(key, value);
		}
		else if (this == LT){
			return QueryBuilder.lt(key, value);
		}
		else if (this == GT){
			return QueryBuilder.gt(key, value);
		}
		return null;
	}
}
