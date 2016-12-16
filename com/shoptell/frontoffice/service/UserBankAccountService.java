/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.frontoffice.service;

import static com.shoptell.backoffice.BackofficeConstants.KEYSPACENAME_VAR;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.domain.User;
import com.shoptell.repository.UserRepository;
import com.shoptell.security.EncryptDecryptUtil;

@Named(value="UserBankAccountService")
public class UserBankAccountService {
	
	private static final Logger log = LoggerFactory.getLogger(UserBankAccountService.class);

	@Inject
	private Session session;

	@Inject
	private Environment env;

	@Inject
	private EncryptDecryptUtil encryptDecryptUtil;
	
	@Inject
	private UserRepository userRepository;

	private String keyspace;
	private String tableName;

	private Mapper<UserAccountDTO> mapper;

	@PostConstruct
	public void init() {
		keyspace = env.getProperty(KEYSPACENAME_VAR);
		tableName = TableEnum.useraccount.name();
		mapper = new MappingManager(session).mapper(UserAccountDTO.class);
	}

	public boolean saveBankAccount(UserAccountDTO userAccountDTO, String reEnteredAccountNumber, String oldAccountNumber) {
		boolean status = true;
			try {
				if (StringUtils.isNotBlank(oldAccountNumber) && !StringUtils.equalsIgnoreCase(oldAccountNumber, "null")) {
					deleteBankAccount(userAccountDTO.getUserId(), oldAccountNumber);
				}else if(!StringUtils.equalsIgnoreCase(userAccountDTO.getBankName(), "AFFERVE") && (getBankAccountListById(userAccountDTO.getUserId()).size() > 3)){
					return false;
				}
				userAccountDTO.setAccountNumber(encryptDecryptUtil.encrypt(userAccountDTO.getAccountNumber()));
				mapper.save(userAccountDTO);
			} catch (Exception e) {
				status = false;
				log.error("", e);
			}
		return status;
	}

	public List<UserAccountDTO> getBankAccountListById(String userId) {
		Where select = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("userId", userId));
		List<UserAccountDTO> results = mapper.map(session.execute(select)).all();
		for (int ii = 0; ii < results.size(); ii++) {
			try {
				results.get(ii).setAccountNumber(encryptDecryptUtil.decrypt(results.get(ii).getAccountNumber()));
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return results;
	}
	
	public UserAccountDTO getBankAccount(String userId, String accountNumber) {
		try {
			return mapper.get(userId, encryptDecryptUtil.encrypt(accountNumber));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteBankAccount(String userId, String accountNumber) {
		try {
			mapper.delete(userId, encryptDecryptUtil.encrypt(accountNumber));
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public void mergeUserAccount(String userId, String finalUserId) {
		BatchStatement batch = new BatchStatement();

		Where select = QueryBuilder.select().all().from(keyspace, tableName).where(QueryBuilder.eq("userId", userId));
		List<UserAccountDTO> userAccountDTOList = mapper.map(session.execute(select)).all();

		for (UserAccountDTO userAccountDTO : userAccountDTOList) {
			batch.add(mapper.deleteQuery(userAccountDTO));
			userAccountDTO.setUserId(finalUserId);
			batch.add(mapper.saveQuery(userAccountDTO));
		}
		
		if (userAccountDTOList.size() > 0) {
			session.execute(batch);
			batch.clear();
		}
	}
	
	/* remove this code in future; add phone number as account number in user account */
	@Async
	public void phoneNumberToAccountNumber() {
		List<User> users = userRepository.getAllUsers();
		for (User user : users) {
			if (StringUtils.isNotBlank(user.getPhoneNumber())) {
				UserAccountDTO userAccountDTO = new UserAccountDTO();
				userAccountDTO.setAccountHolderName(user.getFirstName());
				userAccountDTO.setAccountNumber(user.getPhoneNumber());
				userAccountDTO.setBankName("AFFERVE");
				userAccountDTO.setIfscCode(user.getPhoneNumber());
				userAccountDTO.setUserId(user.getId());
				userAccountDTO.setUserName(user.getFirstName());
				saveBankAccount(userAccountDTO, user.getPhoneNumber(), user.getPhoneNumber());
			}
		}
	}
}
