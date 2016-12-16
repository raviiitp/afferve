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

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;

@Named
public class SocialRepository {
	
	private static final Logger log = LoggerFactory.getLogger(SocialRepository.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;
	
	//private Logger log = LoggerFactory.getLogger(SocialRepository.class);

	private String keyspace;
	private String table;

	private Mapper<SocialUserConnection> mapper;
	private MappingManager mappingManager;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		table = TableEnum.social_user_connection.name();
		mappingManager = new MappingManager(session);
		mapper = mappingManager.mapper(SocialUserConnection.class);
	}

	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		ResultSet rs = null;
		List<String> localUserIds = null;
		ConnectionKey key = connection.getKey();
		Statement getUserIds = null;
		try {
			getUserIds = QueryBuilder.select().column("userid").from(keyspace, table).allowFiltering().where(QueryBuilder.eq("providerId", key.getProviderId()))
					.and(QueryBuilder.eq("providerUserId", key.getProviderUserId()));

			rs = session.execute(getUserIds);
			List<Row> rows = rs.all();
			if (rows.size() > 0) {
				localUserIds = new LinkedList<String>();
			}
			for (Row row : rows) {
				localUserIds.add(row.getString("userId"));
			}
			return localUserIds;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		ResultSet rs = null;
		Set<String> localUserIds = null;
		Statement getUserIds = null;
		try {
			getUserIds = QueryBuilder.select().column("userid").from(keyspace, table).where(QueryBuilder.eq("providerId", providerId))
					.and(QueryBuilder.in("providerUserId", providerUserIds));

			rs = session.execute(getUserIds);
			if (rs.all().size() > 0) {
				localUserIds = new HashSet<String>();
			}
			for (Row row : rs.all()) {
				localUserIds.add(row.getString("userId"));
			}
			return localUserIds;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public List<SocialUserConnection> findPrimaryConnections(String userId, String providerId) {
		try {
			Statement selectConnections = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("userid", userId))
					.and(QueryBuilder.eq("providerid", providerId))/*.orderBy(QueryBuilder.desc("rank"))*/;

			ResultSet rs = session.execute(selectConnections);
			return mapper.map(rs).all();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public SocialUserConnection getConnection(String userId, String providerId, String providerUserId) {
		return mapper.get(userId, providerId, providerUserId);
	}
	
	public void removeConnection(String userId, String providerId, String providerUserId) {
		try {
			SocialUserConnection socialUserConnection = getConnection(userId, providerId, providerUserId);
			if (socialUserConnection != null) {
				mapper.delete(socialUserConnection);
			}
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public void removeConnections(String userId, String providerId) {
		for (SocialUserConnection socialUserConnection : findPrimaryConnections(userId, providerId)) {
			mapper.delete(socialUserConnection);
		}
	}
	
	public void saveConnection(SocialUserConnection socialUserConnection){
		try{
			mapper.save(socialUserConnection);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void updateConnection(String displayName, String profileUrl, String imageUrl, String accessToken, String secret, String refreshToken,
			Long expireTime, String userId, String providerId, String providerUserId) {
		try{
			Statement updateConn = QueryBuilder.update(keyspace, table)
					.with(QueryBuilder.set("displayName", displayName))
					.and(QueryBuilder.set("profileUrl", profileUrl))
					.and(QueryBuilder.set("imageUrl", imageUrl))
					.and(QueryBuilder.set("accessToken", accessToken))
					.and(QueryBuilder.set("secret", secret))
					.and(QueryBuilder.set("refreshToken", refreshToken))
					.and(QueryBuilder.set("expireTime", expireTime))
					.where(QueryBuilder.eq("userId", userId))
					.and(QueryBuilder.eq("providerId", providerId))
					.and(QueryBuilder.eq("providerUserId", providerUserId));
			session.execute(updateConn);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public List<SocialUserConnection> findConnectionsByUserId(String userId) {
		try {
			Statement selectConnections = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("userid", userId));

			ResultSet rs = session.execute(selectConnections);
			return mapper.map(rs).all();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public Integer findMaxRankByUserIdAndProviderId(String userId, String providerId) {
		List<SocialUserConnection> socialUserConnectionList = findPrimaryConnections(userId, providerId);
		if((socialUserConnectionList != null) && (socialUserConnectionList.size() > 0)){
			return socialUserConnectionList.get(0).getRank();
		}
		return null;
	}
}
