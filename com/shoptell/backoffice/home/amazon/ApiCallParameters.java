/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.home.amazon;

import java.util.List;

public interface ApiCallParameters<RequestType> {

	public void setAWSAccessKeyId(String awsAccessKeyId);

	public void setAssociateTag(String associateTag);

	public List<RequestType> getRequest();

}
