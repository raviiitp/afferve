/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.repository;

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;
import static com.shoptell.backoffice.BackofficeConstants.NULL_EMAIL;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.domain.ExternalAccountProvider;
import com.shoptell.domain.User;

/**
 * Cassandra repository for the User entity.
 */
@Repository
public class UserRepository {

	private final Logger log = LoggerFactory.getLogger(UserRepository.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	private Mapper<User> mapper;

	/* private PreparedStatement findOneByActivationKeyStmt; */

	/* private PreparedStatement findOneByResetKeyStmt; */

	/* private PreparedStatement insertByActivationKeyStmt; */

	/* private PreparedStatement insertByResetKeyStmt; */

	/* private PreparedStatement deleteByActivationKeyStmt; */

	/* private PreparedStatement deleteByResetKeyStmt; */

	private PreparedStatement findOneByLoginStmt;

	private PreparedStatement insertByLoginStmt;

	private PreparedStatement deleteByLoginStmt;

	private PreparedStatement findOneByEmailStmt;

	private PreparedStatement insertByEmailStmt;

	private PreparedStatement findOneByReferIdStmt;

	private PreparedStatement insertByReferIdStmt;

	/* private PreparedStatement deleteByEmailStmt; */

	private String keyspace, table;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		table = TableEnum.user.toString();

		mapper = new MappingManager(session).mapper(User.class);

		/*
		 * findOneByActivationKeyStmt = session.prepare( "SELECT id " +
		 * "FROM user_by_activation_key " +
		 * "WHERE activation_key = :activation_key");
		 * 
		 * findOneByResetKeyStmt = session.prepare( "SELECT id " +
		 * "FROM user_by_reset_key " + "WHERE reset_key = :reset_key");
		 */

		/*
		 * insertByActivationKeyStmt = session.prepare(
		 * "INSERT INTO user_by_activation_key (activation_key, id) " +
		 * "VALUES (:activation_key, :id)");
		 * 
		 * insertByResetKeyStmt = session.prepare(
		 * "INSERT INTO user_by_reset_key (reset_key, id) " +
		 * "VALUES (:reset_key, :id)");
		 * 
		 * deleteByActivationKeyStmt = session.prepare(
		 * "DELETE FROM user_by_activation_key " +
		 * "WHERE activation_key = :activation_key");
		 * 
		 * deleteByResetKeyStmt = session.prepare(
		 * "DELETE FROM user_by_reset_key " + "WHERE reset_key = :reset_key");
		 */

		findOneByLoginStmt = session.prepare("SELECT id " + "FROM user_by_login " + "WHERE login = :login");

		insertByLoginStmt = session.prepare("INSERT INTO user_by_login (login, id) " + "VALUES (:login, :id)");

		deleteByLoginStmt = session.prepare("DELETE FROM user_by_login " + "WHERE login = :login");

		findOneByEmailStmt = session.prepare("SELECT id " + "FROM user_by_email " + "WHERE email = :email");

		insertByEmailStmt = session.prepare("INSERT INTO user_by_email (email, id) " + "VALUES (:email, :id)");

		findOneByReferIdStmt = session.prepare("SELECT id " + "FROM user_by_referId " + "WHERE referId = :referId");

		insertByReferIdStmt = session.prepare("INSERT INTO user_by_referId (referId, id) " + "VALUES (:referId, :id)");

		/*
		 * deleteByEmailStmt = session.prepare( "DELETE FROM user_by_email " +
		 * "WHERE email = :email");
		 */
	}

	/*
	 * public Optional<User> findOneByActivationKey(String activationKey) {
	 * BoundStatement stmt = findOneByActivationKeyStmt.bind();
	 * stmt.setString("activation_key", activationKey); return
	 * findOneFromIndex(stmt); }
	 */

	/*
	 * public Optional<User> findOneByResetKey(String resetKey) { BoundStatement
	 * stmt = findOneByResetKeyStmt.bind(); stmt.setString("reset_key",
	 * resetKey); return findOneFromIndex(stmt); }
	 */

	/*
	 * public Optional<User> findOneByEmail(String email) { BoundStatement stmt
	 * = findOneByEmailStmt.bind(); stmt.setString("email", email); return
	 * findOneFromIndex(stmt); }
	 * 
	 * public Optional<User> findOneByLogin(String login) { BoundStatement stmt
	 * = findOneByLoginStmt.bind(); stmt.setString("login", login); return
	 * findOneFromIndex(stmt); }
	 */

	public Optional<User> findOneByExternalAccount(ExternalAccountProvider externalProvider, String externalId) {
		return Optional.empty();
	}

	public String save(User user) {
		log.debug("Start saving user profile...");

		String _id = null;
		List<User> _userList = findUsersByEmail(user.getEmail());
		if (_userList != null && _userList.size() > 0) {
			_id = _userList.get(0).getId();
			user.setId(_id);
			user.setReferId(_userList.get(0).getReferId());
			user.setPhoneNumber(_userList.get(0).getPhoneNumber());
			user.setDob(_userList.get(0).getDob());
			user.setPersonReferred(_userList.get(0).getPersonReferred());
			user.setReferredBy(_userList.get(0).getReferredBy());
			user.setRegisteredOn(_userList.get(0).getRegisteredOn());
		}

		BatchStatement batch = new BatchStatement();
		batch.add(mapper.saveQuery(user));

		batch.add(insertByLoginStmt.bind().setString("login", user.getLogin()).setString("id", user.getId()));

		if ((_id == null) && (!StringUtils.equals(user.getEmail(), NULL_EMAIL))) {
			batch.add(insertByEmailStmt.bind().setString("email", user.getEmail()).setString("id", user.getId()));
		}

		if ((_id == null) && (StringUtils.isNotBlank(user.getReferId()))) {
			batch.add(insertByReferIdStmt.bind().setString("referId", user.getReferId()).setString("id", user.getId()));
		}

		log.debug("Executing session...");
		session.execute(batch);
		batch.clear();
		log.debug("Session executed");
		return _id;
	}
	
	public void updateUser(User user){
		mapper.save(user);
	}

	public void deleteAndSave(User user) {
		mapper.delete(user);
		mapper.save(user);
	}

	public void delete(User user) {
		BatchStatement batch = new BatchStatement();
		batch.add(mapper.deleteQuery(user));

		/*
		 * if (!StringUtils.isEmpty(user.getActivationKey())) {
		 * batch.add(deleteByActivationKeyStmt
		 * .bind().setString("activation_key", user.getActivationKey())); }
		 */
		/*
		 * if (!StringUtils.isEmpty(user.getResetKey())) {
		 * batch.add(deleteByResetKeyStmt.bind().setString("reset_key",
		 * user.getResetKey())); }
		 */
		/*
		 * batch.add(deleteByEmailStmt.bind().setString("email",
		 * user.getEmail()));
		 */

		batch.add(deleteByLoginStmt.bind().setString("login", user.getLogin()));
		session.execute(batch);
		batch.clear();
	}

	public List<User> findUsersById(String id) {
		Statement _select = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq("id", id));
		ResultSet rs = session.execute(_select);
		return mapper.map(rs).all();
	}

	public User findUserByIdAndLogin(String id, String login) {
		return mapper.get(id, login);
	}

	public User findUserByLogin(String login) {
		User _user = null;
		String id = findIdByLogin(login);
		if (id != null) {
			_user = findUserByIdAndLogin(id, login);
		}
		return _user;
	}

	/**
	 * All users of this email
	 * 
	 * @param email
	 * @return list<User>
	 */
	public List<User> findUsersByEmail(String email) {
		List<User> _userList = null;
		String id = findIdByEmail(email);
		if (id != null) {
			_userList = findUsersById(id);
		}
		return _userList;
	}

	public List<User> findUsersByReferId(String referId) {
		List<User> _userList = null;
		String id = findIdByReferId(referId);
		if (id != null) {
			_userList = findUsersById(id);
		}
		return _userList;
	}

	/**
	 * 
	 * @param login
	 * @return Either user id or null
	 */
	public String findIdByLogin(String login) {
		BoundStatement stmt = findOneByLoginStmt.bind();
		stmt.setString("login", login);
		ResultSet rs = session.execute(stmt);
		String id = null;
		if (!rs.isExhausted()) {
			id = rs.one().getString("id");
		}
		return id;
	}

	/**
	 * 
	 * @param email
	 * @return either user id or null
	 */
	public String findIdByEmail(String email) {
		BoundStatement stmt = findOneByEmailStmt.bind();
		stmt.setString("email", email);
		ResultSet rs = session.execute(stmt);
		String id = null;
		if (!rs.isExhausted()) {
			id = rs.one().getString("id");
		}
		return id;
	}

	public String findIdByReferId(String referId) {
		BoundStatement stmt = findOneByReferIdStmt.bind();
		stmt.setString("referId", referId);
		ResultSet rs = session.execute(stmt);
		String id = null;
		if (!rs.isExhausted()) {
			id = rs.one().getString("id");
		}
		return id;
	}

	/*
	 * private Optional<User> findOneFromIndex(BoundStatement stmt) { ResultSet
	 * rs = session.execute(stmt); if (rs.isExhausted()) { return
	 * Optional.empty(); } return Optional.ofNullable(rs.one().getString("id"))
	 * .map(id -> Optional.ofNullable(mapper.get(id))) .get(); }
	 */

	public String findReferIdById(String id) {
		List<User> userList = findUsersById(id);
		for (User user : userList) {
			if (StringUtils.isNotBlank(user.getReferId())) {
				return user.getReferId();
			}
		}
		return null;
	}

	public User setDeActivatedEmail(String userId, String email) {
		List<User> userList = findUsersById(userId);
		for (User user : userList) {
			user.setEmail(email);
			mapper.save(user);
		}
		User _tmpUser = null;
		if (userList.size() > 0) {
			_tmpUser = userList.get(0);
			_tmpUser.setEmail(email);
		}
		return _tmpUser;
	}

	public List<User> getAllUsers() {
		Select allUsers = QueryBuilder.select().all().from(keyspace, table);
		ResultSet rs = session.execute(allUsers);
		return mapper.map(rs).all();
	}

	public void batchSaveUsers(List<User> userList, boolean alterOtherTables) {
		BatchStatement batch = new BatchStatement();
		if (userList == null || userList.size() < 1){
			return;
		}
		for (User user : userList) {
			batch.add(mapper.saveQuery(user));
		}
		
		if(alterOtherTables){
			if(StringUtils.isNotBlank(userList.get(0).getReferId())){
				batch.add(insertByReferIdStmt.bind().setString("referId", userList.get(0).getReferId()).setString("id", userList.get(0).getId()));
			}
		}

		session.execute(batch);
		batch.clear();
	}

	/*public void saveReferCodeOfUser(String userId, String referCode) {

		BatchStatement batch = new BatchStatement();
		List<User> userList = findUsersById(userId);
		for (User user : userList) {
			
			batch.add(mapper.saveQuery(user));

			if (alterOtherTables && StringUtils.isNotBlank(user.getReferId())) {
				batch.add(insertByReferIdStmt.bind().setString("referId", user.getReferId()).setString("id", user.getId()));
			}
		}

		session.execute(batch);
		batch.clear();
	}*/
}
