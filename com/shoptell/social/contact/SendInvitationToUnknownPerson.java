/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.social.contact;

import static com.shoptell.backoffice.BackofficeConstants.DISABLE_INVITE_SEND;
import static com.shoptell.backoffice.BackofficeConstants.SEND_INVITE_SIZE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;

import com.datastax.driver.core.ResultSet;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.SelectQuery;
import com.shoptell.service.SparkPostMailService;
import com.shoptell.util.stproperties.STProperties;

@Named(value = "SendInvitationToUnknownPerson")
public class SendInvitationToUnknownPerson {

	@Inject
	private STProperties stprop;

	@Inject
	private SparkPostMailService mailService;

	@Inject
	private SelectQuery selectQuery;

	@Inject
	private BatchRepository repository;

	public void start() {
		if (Boolean.valueOf(stprop.getValueOrDefault(DISABLE_INVITE_SEND, "true"))) {
			return;
		}
		init();
	}

	@Async
	public void init() {
		int size = Integer.parseInt(stprop.getValueOrDefault(SEND_INVITE_SIZE, "100"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("notInContact", true);
		ResultSet result = selectQuery.selectAll(TableEnum.person_in_contact, map, size);

		List<PersonInContactDTO> list = QueryMapper.personInContactDTO().map(result).all();
		if (list != null && list.size() > 0) {
			for (PersonInContactDTO unknownPerson : list) {
				String firstName = "Friend";
				if (StringUtils.isNotBlank(unknownPerson.getFirstName())) {
					firstName = unknownPerson.getFirstName();
				}
				mailService.sendInvitationToUnknownPerson(unknownPerson.getEmail(), firstName);
				unknownPerson.setNotInContact(false);
			}
			repository.batchSave(list);
		}
	}
}
