/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.backup;

import static com.shoptell.backoffice.BackofficeConstants.DISABLE_DATA_BACKUP;
import static com.shoptell.backoffice.BackofficeConstants.ONE_DAY;
import static com.shoptell.backoffice.BackofficeUtil.getEndOfDay;
import static com.shoptell.backoffice.BackofficeUtil.getStartOfDay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.CBRateDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.PartnerCouponsDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.db.messagelog.MessageLogUtil;
import com.shoptell.db.processlog.ProcessLog;
import com.shoptell.db.processlog.ProcessLogUtil;
import com.shoptell.domain.User;
import com.shoptell.domain.UserByEmail;
import com.shoptell.domain.UserByLogin;
import com.shoptell.domain.UserByReferId;
import com.shoptell.social.contact.PersonInContactDTO;
import com.shoptell.social.contact.PersonWithNoEmailDTO;
import com.shoptell.social.repository.SocialUserConnection;
import com.shoptell.util.stproperties.STProperties;

@Named(value = "BackupProcessor")
public class BackupProcessor {

	private final static Logger log = LoggerFactory.getLogger(BackupProcessor.class);

	@Inject
	private STProperties stprop;

	@Inject
	private MessageLogUtil msgLog;

	@Inject
	private BatchRepository batchRepository;

	@Inject
	private Environment env;

	@Inject
	private ProcessLogUtil process_log;

	private String BASE_DIR;
	private String BACKUP_DIR;

	private SimpleDateFormat format = new SimpleDateFormat("_MM_dd");
	private FileOutputStream fos = null;

	@PostConstruct
	public void start() {
		BASE_DIR = env.getProperty("data.dir", "/usr/local/data");
		File file = new File(BASE_DIR);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	@Async
	@Scheduled(cron = "${backup.init.schedule}")
	public void init() {
		if (Boolean.valueOf(stprop.getValueOrDefault(DISABLE_DATA_BACKUP, "true"))) {
			return;
		}
		ProcessLog pLog = process_log.start("ALL", "BACKUP ENGINE");
		Date date = new Date(System.currentTimeMillis());
		File file = new File(BASE_DIR + "/AFFERVE" + format.format(date));
		if (!file.exists()) {
			if (file.mkdir()) {
				BACKUP_DIR = file.getPath();
				backupCBPayment();
				backupCBReport();
				//backupCBRate();
				//backupReviewProductInfo();
				//backupTicketMessage();
				//backupTickets();
				backupUser();
				//backupUserEmail();
				//backupUserLogin();
				//backupUserReferId();
				backupUserAccount();
				backupUserTransactions();
				backupSocialUserConnection();
				//backupPersonWithNoEmail();
				//backupPersonInContact();
				//backupPersonNotInContact();
				//backupPartnerCoupons();
			}
		}
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.MONTH, -1);
			File toBeDeleted = new File(BASE_DIR + "/AFFERVE" + format.format(c.getTime()));
			if (toBeDeleted.exists()) {
				FileUtils.deleteDirectory(toBeDeleted);
			}
		} catch (IOException e) {
			msgLog.addError(e);
		}
		process_log.end(pLog);
	}

	@SuppressWarnings("unchecked")
	public void backupReviewProductInfo() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/reviewed_product_info" + format.format(date) + ".txt";
		Class<ReviewedProductInfoDTO> class_type = ReviewedProductInfoDTO.class;
		String table = TableEnum.reviewed_product_info.name();
		try {
			fos = new FileOutputStream(file);
			List<ReviewedProductInfoDTO> list = (List<ReviewedProductInfoDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readReviewProductInfo(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/reviewed_product_info" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<ReviewedProductInfoDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<ReviewedProductInfoDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupTickets() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/tickets" + format.format(date) + ".txt";
		Class<TicketDTO> class_type = TicketDTO.class;
		String table = TableEnum.ticket.name();
		try {
			fos = new FileOutputStream(file);
			List<TicketDTO> list = (List<TicketDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readTickets(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/tickets" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<TicketDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<TicketDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupTicketMessage() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/ticket_message" + format.format(date) + ".txt";
		Class<?> class_type = TicketMessageDTO.class;
		String table = TableEnum.ticketMessage.name();
		try {
			fos = new FileOutputStream(file);
			List<TicketMessageDTO> list = (List<TicketMessageDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readTicketMessage(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/ticket_message" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<TicketMessageDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<TicketMessageDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupUser() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/user" + format.format(date) + ".txt";
		Class<?> class_type = User.class;
		String table = TableEnum.user.name();
		try {
			fos = new FileOutputStream(file);
			List<User> list = (List<User>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUser(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/user" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<User> list = new Gson().fromJson(data, new TypeToken<LinkedList<User>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupUserLogin() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/UserByLogin" + format.format(date) + ".txt";
		Class<UserByLogin> class_type = UserByLogin.class;
		String table = TableEnum.user_by_login.name();
		try {
			fos = new FileOutputStream(file);
			List<UserByLogin> list = (List<UserByLogin>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUserLogin(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/UserByLogin" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<UserByLogin> list = new Gson().fromJson(data, new TypeToken<LinkedList<UserByLogin>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupUserReferId() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/UserByReferId" + format.format(date) + ".txt";
		Class<UserByReferId> class_type = UserByReferId.class;
		String table = TableEnum.user_by_referId.name();
		try {
			fos = new FileOutputStream(file);
			List<UserByReferId> list = (List<UserByReferId>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUserReferId(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/UserByReferId" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<UserByReferId> list = new Gson().fromJson(data, new TypeToken<LinkedList<UserByReferId>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupUserEmail() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/UserByEmail" + format.format(date) + ".txt";
		Class<UserByEmail> class_type = UserByEmail.class;
		String table = TableEnum.user_by_email.name();
		try {
			fos = new FileOutputStream(file);
			List<UserByEmail> list = (List<UserByEmail>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUserEmail(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/UserByEmail" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<UserByEmail> list = new Gson().fromJson(data, new TypeToken<LinkedList<UserByEmail>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupUserAccount() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/User_Account" + format.format(date) + ".txt";
		Class<?> class_type = UserAccountDTO.class;
		String table = TableEnum.useraccount.name();
		try {
			fos = new FileOutputStream(file);
			List<UserAccountDTO> list = (List<UserAccountDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUserAccount(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/User_Account" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<UserAccountDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<UserAccountDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	//incremental save
	public void backupUserTransactions() {
		Date date = new Date(System.currentTimeMillis());
		String file = "/Users/abhishekagarwal/Desktop" + "/User_Transactions" + format.format(date) + ".txt";
		
		UUID low = UUIDs.startOf(getStartOfDay(new Date(System.currentTimeMillis()-ONE_DAY*8)).getTime());
		UUID high = UUIDs.endOf(getEndOfDay(new Date(System.currentTimeMillis()-ONE_DAY)).getTime());
		
		try {
			fos = new FileOutputStream(file);
			List<UserTransactionDTO> list = batchRepository.userTransactions(low, high);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readUserTransactions(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/User_Transactions" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<UserTransactionDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<UserTransactionDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupSocialUserConnection() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/SocialUserConnection" + format.format(date) + ".txt";
		Class<?> class_type = SocialUserConnection.class;
		String table = TableEnum.social_user_connection.name();
		try {
			fos = new FileOutputStream(file);
			List<SocialUserConnection> list = (List<SocialUserConnection>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readSocialUserConnection(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/SocialUserConnection" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<SocialUserConnection> list = new Gson().fromJson(data, new TypeToken<LinkedList<SocialUserConnection>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupCBReport() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/CB_REPORT" + format.format(date) + ".txt";
		Class<?> class_type = CBReportDTO.class;
		String table = TableEnum.cbreport.name();
		try {
			fos = new FileOutputStream(file);
			List<CBReportDTO> list = (List<CBReportDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readCBReport(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/CB_REPORT" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<CBReportDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<CBReportDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupCBRate() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/CB_RATE" + format.format(date) + ".txt";
		Class<?> class_type = CBRateDTO.class;
		String table = TableEnum.cbrate.name();
		try {
			fos = new FileOutputStream(file);
			List<CBRateDTO> list = (List<CBRateDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readCBRate(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/CB_RATE" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<CBRateDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<CBRateDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupCBPayment() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/CB_PAYMENT" + format.format(date) + ".txt";
		Class<?> class_type = CBPaymentDTO.class;
		String table = TableEnum.cbpayment.name();
		try {
			fos = new FileOutputStream(file);
			List<CBPaymentDTO> list = (List<CBPaymentDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readCBPayment(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/CB_PAYMENT" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<CBPaymentDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<CBPaymentDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupPersonInContact() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/PersonInContact" + format.format(date) + ".txt";
		Class<?> class_type = PersonInContactDTO.class;
		String table = TableEnum.person_in_contact.name();
		try {
			fos = new FileOutputStream(file);
			List<PersonInContactDTO> list = (List<PersonInContactDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readPersonInContact(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/PersonInContact" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<PersonInContactDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<PersonInContactDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupPersonWithNoEmail() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/PersonWithNoEmail" + format.format(date) + ".txt";
		Class<?> class_type = PersonWithNoEmailDTO.class;
		String table = TableEnum.person_with_no_email.name();
		try {
			fos = new FileOutputStream(file);
			List<PersonWithNoEmailDTO> list = (List<PersonWithNoEmailDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readPersonWithNoEmail(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/PersonWithNoEmail" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<PersonWithNoEmailDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<PersonWithNoEmailDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void backupPartnerCoupons() {
		Date date = new Date(System.currentTimeMillis());
		String file = BACKUP_DIR + "/PartnerCoupons" + format.format(date) + ".txt";
		Class<?> class_type = PartnerCouponsDTO.class;
		String table = TableEnum.partner_coupons.name();
		try {
			fos = new FileOutputStream(file);
			List<PartnerCouponsDTO> list = (List<PartnerCouponsDTO>) batchRepository.selectAll(table, class_type, null, null, null);
			if (list != null && list.size() > 0) {
				String json = new Gson().toJson(list);
				fos.write(json.getBytes());
			}
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	public void readPartnerCoupons(Date date) {
		String file = BASE_DIR + "/AFFERVE" + format.format(date) + "/PartnerCoupons" + format.format(date) + ".txt";
		try {
			String data = new String(Files.readAllBytes(Paths.get(file)));
			List<PartnerCouponsDTO> list = new Gson().fromJson(data, new TypeToken<LinkedList<PartnerCouponsDTO>>() {
			}.getType());

			batchRepository.batchSave(list);

		} catch (Exception e) {
			msgLog.addError(e);
		}
	}

	@Async
	public void ReadAndPersistToTables(Date date) {
		// Date date = new Date(System.currentTimeMillis());
		readCBPayment(date);
		readCBRate(date);
		readCBReport(date);
		readPartnerCoupons(date);
		readPersonInContact(date);
		readPersonWithNoEmail(date);
		// readReviewProductInfo(date);
		readSocialUserConnection(date);
		readTicketMessage(date);
		readTickets(date);
		readUser(date);
		readUserAccount(date);
		readUserEmail(date);
		readUserLogin(date);
		readUserReferId(date);
		readUserTransactions(date);
		log.info("Recovery End");
	}
}
