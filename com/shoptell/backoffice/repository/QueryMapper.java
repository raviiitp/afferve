/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.CategoryNodeDTO;
import com.shoptell.backoffice.repository.dto.FeedbackDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.MergeProductPropertiesDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.db.messagelog.MessageLog;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.domain.UserNotificationDTO;
import com.shoptell.scrap.mobiles.BrandLinkDTO;
import com.shoptell.scrap.mobiles.ProductInfoDTO;
import com.shoptell.social.contact.PersonInContactDTO;

@Named
public class QueryMapper {
	
	@Inject
	private Session session;
	
	private static MappingManager mappingManager;
	
	private static Map<Class<?>, Mapper<?>> map = new HashMap<Class<?>, Mapper<?>>();
	
	@PostConstruct
	public void init() {
		mappingManager = new MappingManager(session);
		map.clear();
	}
	
	public static Mapper<?> getMapper(Class<?> klass){
		if (!map.containsKey(klass)){
			map.put(klass, mappingManager.mapper(klass));
		}
		return map.get(klass);
	}
	
	@SuppressWarnings("unchecked")
	public static Mapper<MergeProductPropertiesDTO> mergeProductPropertiesDTO(){
		return (Mapper<MergeProductPropertiesDTO>) getMapper(MergeProductPropertiesDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<UserNotificationDTO> userNotificationDTO() {
		return (Mapper<UserNotificationDTO>) getMapper(UserNotificationDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<HomeProductInfoDTO> homeProductInfoDTO() {
		return (Mapper<HomeProductInfoDTO>) getMapper(HomeProductInfoDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<ReviewedProductInfoDTO> reviewedProductInfoDTO() {
		return (Mapper<ReviewedProductInfoDTO>) getMapper(ReviewedProductInfoDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<BrandLinkDTO> brandLinkDTO() {
		return (Mapper<BrandLinkDTO>) getMapper(BrandLinkDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<MergedProductInfoDTO> mergedProductInfoDTO() {
		return (Mapper<MergedProductInfoDTO>) getMapper(MergedProductInfoDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<ProductInfoDTO> productInfoDTO() {
		return (Mapper<ProductInfoDTO>) getMapper(ProductInfoDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<CategoryNodeDTO> categoryNodeDTO() {
		return (Mapper<CategoryNodeDTO>) getMapper(CategoryNodeDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<MessageLog> messageLog() {
		return (Mapper<MessageLog>) getMapper(MessageLog.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<FeedbackDTO> feedbackDTO() {
		return (Mapper<FeedbackDTO>) getMapper(FeedbackDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<TicketDTO> ticketDTO() {
		return (Mapper<TicketDTO>) getMapper(TicketDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<CBPaymentDTO> cBPaymentDTO() {
		return (Mapper<CBPaymentDTO>) getMapper(CBPaymentDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<ProcessLog> processLog() {
		return (Mapper<ProcessLog>) getMapper(ProcessLog.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<CBReportDTO> cBReportDTO() {
		return (Mapper<CBReportDTO>) getMapper(CBReportDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<UserTransactionDTO> userTransactionDTO() {
		return (Mapper<UserTransactionDTO>) getMapper(UserTransactionDTO.class);
	}

	@SuppressWarnings("unchecked")
	public static Mapper<PersonInContactDTO> personInContactDTO() {
		return (Mapper<PersonInContactDTO>) getMapper(PersonInContactDTO.class);
	}
}
