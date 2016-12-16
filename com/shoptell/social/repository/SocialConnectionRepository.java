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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.shoptell.social.repository.SocialRepository;

public class SocialConnectionRepository implements ConnectionRepository {

	private String userId;
	private ConnectionFactoryLocator connectionFactoryLocator;
	private TextEncryptor textEncryptor;
	
	//@Inject
	private SocialRepository socialRepository;

	public SocialConnectionRepository() {

	}

	public SocialConnectionRepository(String userId, ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor, SocialRepository socialRepository) {
		this.userId = userId;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.textEncryptor = textEncryptor;
		this.socialRepository = socialRepository;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		List<Connection<?>> resultList = connectionMapper.mapConnections(socialRepository.findConnectionsByUserId(userId));
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
		for (String registeredProviderId : registeredProviderIds) {
			connections.put(registeredProviderId, Collections.<Connection<?>> emptyList());
		}
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			if (connections.get(providerId).size() == 0) {
				connections.put(providerId, new LinkedList<Connection<?>>());
			}
			connections.add(providerId, connection);
		}
		return connections;
	}

	public List<Connection<?>> findConnections(String providerId) {
		return connectionMapper.mapConnections(socialRepository.findPrimaryConnections(userId, providerId));
	}

	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(getProviderId(apiType));
		return (List<Connection<A>>) connections;
	}

	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
		if (providerUsers == null || providerUsers.isEmpty()) {
			throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
		}

		List<Connection<?>> resultList = connectionMapper
				.mapConnections(getAllUserConnections(userId, providerUsers));
		
		MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
		for (Connection<?> connection : resultList) {
			String providerId = connection.getKey().getProviderId();
			List<String> userIds = providerUsers.get(providerId);
			List<Connection<?>> connections = connectionsForUsers.get(providerId);
			if (connections == null) {
				connections = new ArrayList<Connection<?>>(userIds.size());
				for (int i = 0; i < userIds.size(); i++) {
					connections.add(null);
				}
				connectionsForUsers.put(providerId, connections);
			}
			String providerUserId = connection.getKey().getProviderUserId();
			int connectionIndex = userIds.indexOf(providerUserId);
			connections.set(connectionIndex, connection);
		}
		return connectionsForUsers;
	}

	public List<SocialUserConnection> getAllUserConnections(String userId, MultiValueMap<String, String> providerUsers) {
		List<SocialUserConnection> remoteUsers = new ArrayList<SocialUserConnection>();
		for (Map.Entry<String, List<String>> providerUsersEntry : providerUsers.entrySet()) {
			String providerId = providerUsersEntry.getKey();
			for (String providerUserId : providerUsersEntry.getValue()) {
				SocialUserConnection userConnection = socialRepository.getConnection(userId,
						providerId, providerUserId);
				if (userConnection != null) {
					remoteUsers.add(userConnection);
				}
			}
		}
		return remoteUsers;
	}
	
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		try {
			return connectionMapper.mapConnection(socialRepository.getConnection(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId()));
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchConnectionException(connectionKey);
		}
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
		if (connection == null) {
			throw new NotConnectedException(providerId);
		}
		return connection;
	}

	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		String providerId = getProviderId(apiType);
		return (Connection<A>) findPrimaryConnection(providerId);
	}

	@Transactional
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();
			int rank = getRank(userId, data.getProviderId());
			
			SocialUserConnection socialUserConnection = new SocialUserConnection(userId, data.getProviderId(), data.getProviderUserId(),
					rank, data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(),
					encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
			
			socialRepository.saveConnection(socialUserConnection);
			
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}
	
	public int getRank(String userId, String providerId) {

		Integer maxRank = socialRepository.findMaxRankByUserIdAndProviderId(
				userId, providerId);
		return maxRank == null ? 1 : (maxRank.intValue() + 1);
	}

	@Transactional
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();
		socialRepository.updateConnection(data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()),
				encrypt(data.getRefreshToken()), data.getExpireTime(), userId, data.getProviderId(), data.getProviderUserId());
	}

	@Transactional
	public void removeConnections(String providerId) {
		socialRepository.removeConnections(userId, providerId);
	}

	@Transactional
	public void removeConnection(ConnectionKey connectionKey) {
		socialRepository.removeConnection(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
	}

	// internal helpers

	private Connection<?> findPrimaryConnection(String providerId) {
		List<Connection<?>> connections = connectionMapper.mapConnections(socialRepository.findPrimaryConnections(userId, providerId));
		if ((connections != null) && (connections.size() > 0)) {
			return connections.get(0);
		} else {
			return null;
		}
	}

	private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();

	private final class ServiceProviderConnectionMapper {

		public List<Connection<?>> mapConnections(List<SocialUserConnection> socialUserConnectionList) {
			List<Connection<?>> connections = new ArrayList<Connection<?>>();
			for (SocialUserConnection socialUserConnection : socialUserConnectionList) {
				connections.add(mapConnection(socialUserConnection));
			}
			return connections;
		}

		public Connection<?> mapConnection(SocialUserConnection socialUserConnection) {
			ConnectionData connectionData = mapConnectionData(socialUserConnection);
			ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
			return connectionFactory.createConnection(connectionData);
		}

		private ConnectionData mapConnectionData(SocialUserConnection socialUserConnection) {
			return new ConnectionData(socialUserConnection.getProviderId(), socialUserConnection.getProviderUserId(), socialUserConnection.getDisplayName(),
					socialUserConnection.getProfileUrl(), socialUserConnection.getImageUrl(), decrypt(socialUserConnection.getAccessToken()),
					decrypt(socialUserConnection.getSecret()), decrypt(socialUserConnection.getRefreshToken()), socialUserConnection.getExpireTime());
		}

		private String decrypt(String encryptedText) {
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
	}

	private <A> String getProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}
}
