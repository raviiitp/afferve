/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home;

public class ProductException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ProductException(){
		super();
	}
	
	public ProductException(String message){
		super(message);
	}
	
	public ProductException(Throwable throwable){
		super(throwable);
	}
	
	public ProductException(String message, Throwable throwable){
		super(message, throwable);
	}

}
