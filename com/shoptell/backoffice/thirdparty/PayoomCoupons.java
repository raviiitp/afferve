/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.thirdparty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.shoptell.backoffice.BackofficeUtil;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;
import com.shoptell.frontoffice.service.PartnerCoupons;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "PayoomCoupons")
public class PayoomCoupons extends coupons{
	
	private static final Logger log = LoggerFactory.getLogger(PayoomCoupons.class);

	@Inject
	private PartnerCoupons util;
	
	// Create the CSVFormat object
	private CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

	private CSVParser parser;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public String process(MultipartFile file) {
		String msg = "File Upload Complete";
		try {
			InputStream is = file.getInputStream();
			if (is != null) {
				Reader reader = new InputStreamReader(is);
				parser = new CSVParser(reader, format);
				Map<String, Integer> head = parser.getHeaderMap();
				if (head != null && head.size() != 6){
					return "Wrong File";
				}
				for (CSVRecord element : parser) {
					String campaign = element.get(0);
					if (StringUtils.isNotBlank(campaign)) {
						String tmp = campaign.toUpperCase().replaceAll("[^\\p{L}\\p{Z}\\p{N}]", "").replaceAll("CP[S|A]", "").trim();
						HomeEnum home = HomeEnum.getHome(tmp);
						if (home != null) {
							PartnerCouponsDTO dto = new PartnerCouponsDTO();
							dto.setHome(home.name());
							String couponTitle = element.get(1);
							if (StringUtils.isNotBlank(couponTitle)) {
								couponTitle = couponTitle.replace("amp;", "").replaceAll("\\s+", " ").replace("T and C ", "T&C");
								dto.setBody_text(couponTitle);
							}
							String couponCode = element.get(2);
							if (StringUtils.isNotBlank(couponCode)) {
								couponCode = couponCode.replace("amp;", "").replaceAll("\\s+", " ");
								// dto.setFooter_text("Coupon Code: " +
								// couponCode);
								dto.setCouponCode(couponCode);
							}

							// String startDate = element.get(3);
							String endDate = element.get(4);
							if (StringUtils.isNotBlank(endDate)) {
								Date date = simpleDateFormat.parse(endDate);
								dto.setExpireDate(BackofficeUtil.getEndOfDay(date));
							}

							String landingPage = element.get(5);
							if (StringUtils.isNotBlank(landingPage)) {
								int indx = landingPage.indexOf("&url=");
								if (indx != -1) {
									String tmp_url = landingPage.substring(indx + 5);
									indx = tmp_url.indexOf("%3F");
									if (indx != -1) {
										tmp_url = tmp_url.substring(0, indx);
									}
									dto.setUrl(tmp_url);
								}
							}
							dto.setType("M");
							List<String> belongsTo = new LinkedList<String>();
							belongsTo.add(dto.getHome());
							dto.setBelongsTo(belongsTo);
							
							generateCashbackString(home, dto);

							util.saveCoupon(dto);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("EXECUTION ERROR", e);
			msg = e.getMessage();
		} finally {
			try {
				parser.close();
			} catch (IOException e) {
				log.error("ERROR", e);
			}
		}
		log.info("Execution Complete.");
		return msg;
	}
}
