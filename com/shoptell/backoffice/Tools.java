/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

@Named(value="Tools")
public class Tools {

	private static final Logger log = LoggerFactory.getLogger(Tools.class);
	
	@Inject
	private BatchRepository repository;
	
	// Television color remove
	@Async
	public void removeTelevisionColorForFlipkart(){
		log.info("removeTelevisionColorForFlipkart() Enter");
		Iterator<ReviewedProductInfoDTO> rows = repository.getDataForReview("FLIPKART", CategoryEnum.TELEVISION.name(), true);
		if(rows !=  null){
			List<ReviewedProductInfoDTO> updatedRows = new LinkedList<ReviewedProductInfoDTO>();
			while (rows.hasNext()) {
				ReviewedProductInfoDTO field = rows.next();
				if (field.getColor() != null){
					field.setColor(null);
					updatedRows.add(field);
					if (updatedRows.size() > BackofficeConstants.BATCHSIZE){
						repository.batchSave(updatedRows);
						updatedRows.clear();
					}
				}
			}
			if (updatedRows.size() > 0){
				repository.batchSave(updatedRows);
				updatedRows.clear();
			}
		}
		log.info("removeTelevisionColorForFlipkart() Exit");
	}
}
