/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.service;

import static com.shoptell.backoffice.enums.TableEnum.newuser;
import static com.shoptell.backoffice.repository.QueryOperations.LTE;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.BackofficeConstants;
import com.shoptell.backoffice.repository.DeleteQuery;
import com.shoptell.backoffice.repository.QueryOperations;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.domain.User;
import com.shoptell.frontoffice.service.MergeUserService;
import com.shoptell.repository.UserRepository;
import com.shoptell.security.AuthoritiesConstants;
import com.shoptell.security.SecurityUtils;
import com.shoptell.service.util.RandomUtil;
import com.shoptell.web.rest.ReferCodeEnum;

/**
 * Service class for managing users.
 */
@Service (value="UserService")
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MergeUserService mergeUserService;
    
    @Inject
    private PasswordEncoder passwordEncoder;
    
    @Inject
    private MailService mailService;
    
    @Inject
    private SelectQuery selectQuery;
    
    @Inject
    private DeleteQuery deleteQuery;
    
    @PostConstruct
    public void init(){
    	/*User _tmpUser = userRepository.findUserByIdAndLogin("19f795e6-512c-4b78-b2f6-38814ec523a3", "109108064158043835901");
    	mailService.sendActivationEmail(_tmpUser, BackofficeConstants.BASEURL_EMAIL);
    	mailService.sendWelcomeEmail(_tmpUser, BackofficeConstants.BASEURL);*/
    }

    public void setDeActivatedEmail(String userId, String email){
    	User _tmpUser = userRepository.setDeActivatedEmail(userId, email);
    	if(_tmpUser != null){
    		mailService.sendActivationEmail(_tmpUser, BackofficeConstants.BASEURL_EMAIL);
    	}
    }
    
    public boolean activateEmail(String userId, String key) {
        log.debug("Activating email for [userId, key]: {} {}", userId, key);
        
        User _tmpUser = null;
        List<User> _userList = userRepository.findUsersById(userId);
        
        /*
         * finalUserId: user id to which current user has to be merged; merge tickets, messages, transactions etc.
         */
        String finalUserId = null;
        if(_userList.size() > 0){
        	_tmpUser = _userList.get(0);
        	if(!_tmpUser.isActivated() && StringUtils.equals(_tmpUser.getActivationKey(), key)){
        		userRepository.delete(_tmpUser);
            	_tmpUser.setActivated(true);
            	_tmpUser.setActivationKey(null);
            	finalUserId = userRepository.save(_tmpUser);
            	if(StringUtils.isNotBlank(finalUserId)){
            		mergeUserService.mergeUser(userId, finalUserId);
            	}else{
            		mailService.sendWelcomeEmail(_tmpUser.getId());
            	}
            	return true;
            }
        }
        return false;
    }
    

	public boolean saveUser(UserProfile profile, String loginId, String imageUrl){
		boolean isNewUser = false;
		String _id = userRepository.findIdByLogin(loginId);
		if(_id == null){
			isNewUser = true;
			User newUser = createNewUser(profile, loginId, imageUrl);
			userRepository.save(newUser);
			if(profile.getEmail() != null){
				//AddMailContact.addEmail(newUser);
				mailService.addNewContact(newUser.getId());
			}
		}
		return isNewUser;
    }
	
	public User createNewUser(UserProfile profile, String loginId, String imageUrl){
		
        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.USER);
        
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        
        User newUser = new User(UUID.randomUUID().toString(), loginId, null, null, encryptedPassword, profile.getEmail(), imageUrl, true, null, "en", null
				, null, true, true, null, null, 0, new Date(), authorities);
        
        if(StringUtils.isNotBlank(profile.getFirstName())){
        	newUser.setFirstName(StringUtils.capitalize(profile.getFirstName()));
        }
        if(StringUtils.isNotBlank(profile.getLastName())){
        	newUser.setLastName(StringUtils.capitalize(profile.getLastName()));
        }
        
        if(StringUtils.isBlank(profile.getEmail())){
        	newUser.setEmail(BackofficeConstants.NULL_EMAIL);
    		newUser.setActivated(false);
    		newUser.setActivationKey(RandomUtil.generateActivationKey());
    	}
        
        return newUser;
	}

    public User getUserWithAuthorities() {
        User currentUser = userRepository.findUserByLogin(SecurityUtils.getCurrentLogin());
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }
    
    /**
     * to make sure that logged in user has made request with it's own user ID. Big thing is JSESSIONID security.
     * @param userId
     * @return
     */
    public boolean isLoggedInUserRequested(String userId){
    	return StringUtils.equals(getUserWithAuthorities().getId(), userId);
    }
    
    /*
     * if user does not exist: return invalid_code
     * else if user is already referred: return invalid_code
     * else if user has referred himself: return fraud_code
     * else if referredBy user does not exist: return invalid_code
     * else update user list
     */
	public String setReferredBy(String userId, String referId) {
		List<User> userList = userRepository.findUsersById(userId);
		User user = null;
		if(userList.size() > 0){
			user = userList.get(0);
			if(StringUtils.isNotBlank(user.getReferredBy())){
				return ReferCodeEnum.INVALID_CODE.toString();
			}else if(StringUtils.equals(user.getReferId(), referId)){
				return ReferCodeEnum.FRAUD_CODE.toString();
			}else{
				String referredBy = userRepository.findIdByReferId(referId);
				if(StringUtils.isBlank(referredBy)){
					return ReferCodeEnum.INVALID_CODE.toString();
				}else{
					for(User _user : userList){
						_user.setReferredBy(referredBy);
					}
					userRepository.batchSaveUsers(userList, false);
					List<User> refereeUserList = userRepository.findUsersById(referredBy);
					for(User refereeUser : refereeUserList){
						refereeUser.setPersonReferred(refereeUser.getPersonReferred() + 1);
					}
					userRepository.batchSaveUsers(refereeUserList, false);
					return ReferCodeEnum.OK.toString();
				}
			}
		}else{
			return ReferCodeEnum.INVALID_CODE.toString();
		}
	}
	
	public void setPublicNotification(String userId, boolean publicNotification) {
		List<User> userList = userRepository.findUsersById(userId);
		for(User user : userList){
			user.setBroadcastNotification(publicNotification);
		}
		
		userRepository.batchSaveUsers(userList, false);
	}
	
	public void setMonthlyAccountStatementCtrl(String userId, boolean monthlyAccountStatementNotification) {
		List<User> userList = userRepository.findUsersById(userId);
		for(User user : userList){
			user.setMonthlyAccountStatementNotification(monthlyAccountStatementNotification);
		}
		
		userRepository.batchSaveUsers(userList, false);
	}

	public String setPhoneNumber(String userId, String phoneNumber) {
		String oldPhoneNumber = null;
		List<User> userList = userRepository.findUsersById(userId);
		for(User user : userList){
			oldPhoneNumber = user.getPhoneNumber();
			user.setPhoneNumber(phoneNumber);
		}
		
		userRepository.batchSaveUsers(userList, false);
		return oldPhoneNumber;
	}

	public void setDOB(String userId, Date dob) {
		List<User> userList = userRepository.findUsersById(userId);
		for(User user : userList){
			user.setDob(dob);
		}
		
		userRepository.batchSaveUsers(userList, false);
	}
	
	public ReferCodeEnum setReferCode(String userId, String referCode, boolean save) {
		ReferCodeEnum referCodeEnum = ReferCodeEnum.ALREADY_EXISTS;
		if(StringUtils.isBlank(userRepository.findIdByReferId(referCode))){
			if(save){
				referCodeEnum = ReferCodeEnum.OK;
				List<User> userList = userRepository.findUsersById(userId);
				for(User user : userList){
					user.setReferId(referCode);
				}
				userRepository.batchSaveUsers(userList, true);
			}else{
				referCodeEnum = ReferCodeEnum.NEW_CODE;
			}
		}else{
			referCodeEnum = ReferCodeEnum.ALREADY_EXISTS;
		}
		return referCodeEnum;
	}
	
    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    //@Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
/*        LocalDate now = new LocalDate();
        List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1));
        for (PersistentToken token : tokens) {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        }*/
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
/*        DateTime now = new DateTime();
        List<User> users = userRepository.findNotActivatedUsersByCreationDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }*/
    }
    
    /*remove this code in future*/
/*    public void setMonthlyAccountStatementNotificationToTrue(){
    	List<User> userList = userRepository.getAllUsers();
    	for(User user : userList){
    		user.setMonthlyAccountStatementNotification(true);
    	}
    	userRepository.batchSaveUsers(userList, false);
    	userList.clear();
    }*/
    
    /*
	 * Send Welcome Emails Hourly
	 */
	public void sendWelcomeEmail() {
		List<String> list = new LinkedList<String>();
		UUID high = UUIDs.startOf(System.currentTimeMillis());
		
		String[] keys = { "time" };
		Object[] values = { high };
		QueryOperations[] operations = { LTE };
		ResultSet rs = selectQuery.selectWithOperations(newuser, true, keys, operations, values);
		Iterator<Row> itr = rs.iterator();
		while (itr.hasNext()) {
			Row row = itr.next();
			String userId = row.getString("userId");
			UUID time = row.getUUID("time");
			list.add(userId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("time", time);
			deleteQuery.deleteQuery(newuser, map);
		}
		
		if (list.size() > 0) {
			for (String userId : list) {
				mailService.sendWelcomeEmail(userId);
			}
		}
	}
}
