/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.security.social;

import com.shoptell.security.SecurityUtils;
import org.springframework.social.UserIdSource;


/**
 * A UserIdSource that delegates to {@link com.mycompany.myapp.security.SecurityUtils#getCurrentLogin()}.
 * UserIdSource is used by Spring Security Social to link the
 * {@link org.springframework.social.connect.Connection Connections} stored in the
 * {@link org.springframework.social.connect.UsersConnectionRepository UsersConnectionRepository} to the
 * {@link com.mycompany.myapp.domain.User#getLogin() internal logins}.
 */
public class SecurityUtilsUserIdSource implements UserIdSource {
    @Override
    public String getUserId() {
        return SecurityUtils.getCurrentLogin();
    }
}
