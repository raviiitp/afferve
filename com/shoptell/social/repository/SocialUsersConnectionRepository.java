/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.social.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

import com.shoptell.social.repository.SocialRepository;

//@Repository
public class SocialUsersConnectionRepository implements UsersConnectionRepository {

	//@Inject
	private SocialRepository socialRepository;
	
	private ConnectionFactoryLocator connectionFactoryLocator;
	
	private TextEncryptor textEncryptor;
	
	private ConnectionSignUp connectionSignUp;

	public SocialUsersConnectionRepository(){
	}
	
	public SocialUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor, SocialRepository socialRepository) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.socialRepository = socialRepository;
	}
	
	/**
	 * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
	 * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 * @param connectionSignUp an implementation of ConnectionSignUp for implicit sign-up
	 * @see #findUserIdsWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
	}
	
	@Override
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
	List<String> localUserIds = socialRepository.findUserIdsWithConnection(connection);		
	if ((localUserIds == null ||localUserIds.size() == 0) && connectionSignUp != null) {
		String newUserId = connectionSignUp.execute(connection);
		if (newUserId != null)
		{
			createConnectionRepository(newUserId).addConnection(connection);
			return Arrays.asList(newUserId);
		}
	}
	return localUserIds;
}

	@Override
	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		return socialRepository.findUserIdsConnectedTo(providerId, providerUserIds);
	}

	@Override
	public ConnectionRepository createConnectionRepository(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId cannot be null");
		}
		return new SocialConnectionRepository(userId, connectionFactoryLocator, textEncryptor, socialRepository);
	}
}
