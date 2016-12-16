/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.recaptcha;

public class ReCaptchaResponse {

    private boolean success;
    private String errorCode;

    protected ReCaptchaResponse(boolean success, String errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    /**
     * The reCaptcha error message.
     *
     * not-reachable
     * missing-input-secret
     * invalid-input-secret
     * missing-input-response
     * invalid-input-response
     *
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * True if captcha is "success".
     * @return
     */
    public boolean isSuccess() {
        return success;
    }
}
