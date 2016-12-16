/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice;

import static com.shoptell.backoffice.BackofficeUtil.getEndOfDay;
import static com.shoptell.backoffice.BackofficeUtil.getStartOfDay;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.utils.UUIDs;
import com.shoptell.backoffice.backup.BackupProcessor;
import com.shoptell.backoffice.enums.CBStatusEnum;
import com.shoptell.backoffice.enums.CategoryEnum;
import com.shoptell.backoffice.enums.HomeEnum;
import com.shoptell.backoffice.enums.TableEnum;
import com.shoptell.backoffice.repository.BatchRepository;
import com.shoptell.backoffice.repository.QueryMapper;
import com.shoptell.backoffice.repository.UpdateQuery;
import com.shoptell.backoffice.repository.dto.BankDiscountDTO;
import com.shoptell.backoffice.repository.dto.CBPaymentDTO;
import com.shoptell.backoffice.repository.dto.CBRateDTO;
import com.shoptell.backoffice.repository.dto.CBReportDTO;
import com.shoptell.backoffice.repository.dto.HomeProductInfoDTO;
import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.TicketDTO;
import com.shoptell.backoffice.repository.dto.TicketMessageDTO;
import com.shoptell.backoffice.repository.dto.UserAccountDTO;
import com.shoptell.backoffice.repository.dto.UserTransactionDTO;
import com.shoptell.backoffice.repository.util.CashbackUtil;
import com.shoptell.backoffice.thirdparty.PayoomCoupons;
import com.shoptell.backoffice.thirdparty.VcommCoupons;
import com.shoptell.config.elasticsearch.ElasticSearchResponse;
import com.shoptell.config.elasticsearch.ElasticSearchUtil;
import com.shoptell.domain.User;
import com.shoptell.frontoffice.service.BankDiscountsService;
import com.shoptell.frontoffice.service.CBRateService;
import com.shoptell.frontoffice.service.TicketMessageService;
import com.shoptell.frontoffice.service.TicketService;
import com.shoptell.frontoffice.service.WebmastersCommandLine;
import com.shoptell.service.MailService;
import com.shoptell.util.stproperties.STProperties;

@Scope("session")
@Controller(value = "MainBackOfficeController")
@RequestMapping("/backoffice")
public class MainBackOfficeController {
	private static final Logger log = LoggerFactory.getLogger(MainBackOfficeController.class);

	@Inject
	private UpdateQuery updateQuery;

	@Inject
	private CashbackUtil cashbackUtil;

	private String[] cbReportStatus = { CBStatusEnum.PENDING.toString(), CBStatusEnum.RECEIVED.toString(), CBStatusEnum.WITHDRAWN.toString() };
	private String[] cbPaymentStatus = { CBStatusEnum.REQUESTED.toString(), CBStatusEnum.PAID.toString() };

	@Autowired
	private ApplicationContext ctx;

	@Inject
	private SitemapGenerator sitemap;

	@Inject
	private BackofficeProcessor processor;

	@Inject
	private BackupProcessor backup;

	@Inject
	private STProperties stprop;

	@Inject
	private BatchRepository batchRepository;

	@Inject
	private ElasticSearchUtil indexRepo;

	@Inject
	private TicketService ts;

	@Inject
	private TicketMessageService tms;

	@Inject
	private CBRateService rate;

	@Inject
	private Session session;

	@Inject
	private BankDiscountsService bds;

	@Inject
	private MailService mail;

	private List<String> homes = new LinkedList<String>();
	private List<String> category = new LinkedList<String>();

	List<MergedProductInfoDTO> mergedProductInfoList;
	int pgSize = 20;

	private String keyspace;

	@PostConstruct
	public void start() {
		for (HomeEnum home : HomeEnum.values()) {
			homes.add(home.name());
		}
		for (CategoryEnum cat : CategoryEnum.values()) {
			category.add(cat.name());
		}
	}

	Map<String, ReviewedProductInfoDTO> itemMap = new HashMap<String, ReviewedProductInfoDTO>();

	@RequestMapping(value = "/google")
	public String code(@RequestParam(value = "code", required = false) String code, Model model) {
		log.info("code() Enter");

		// creating sitemap.xml
		sitemap.init();

		String resp = "redirect:/backoffice/debug";
		String url = null;

		if (StringUtils.isNotBlank(code)) {
			WebmastersCommandLine.postProcess(code);
		}
		else {
			try {
				url = WebmastersCommandLine.execute();
				url = URLEncoder.encode(url, "UTF-8");
				resp += "?google_url=" + url;
			} catch (IOException e) {
				log.error("", e);
			}
		}
		log.info("code() Exit");
		return resp;
	}

	// HomeProductInfo DTO
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/products")
	public String getProducts(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "home", required = false) String home, @RequestParam(value = "ismerged", defaultValue = "false") boolean ismerged,
			@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "rd", required = false) boolean rd, @RequestParam(value = "msg", required = false) String msg) {
		Map<String, Object> map = processor.getProducts(rd, page, pgSize, home, id, tags, ismerged);
		int lastpg = (int) map.getOrDefault("lastpg", 1);
		int pagenum = (int) map.getOrDefault("pagenum", 1);
		List<HomeProductInfoDTO> sublist = (List<HomeProductInfoDTO>) map.getOrDefault("sublist", new LinkedList<HomeProductInfoDTO>());
		ismerged = (boolean) map.getOrDefault("ismerged", false);
		String error_msg = (String) map.get("error_msg");
		if (StringUtils.isNotBlank(msg)) {
			model.addAttribute("error_msg", msg);
		}
		else if (StringUtils.isNotBlank(error_msg)) {
			model.addAttribute("error_msg", error_msg);
		}

		model.addAttribute("ismerged", ismerged);
		model.addAttribute("obj", sublist);
		model.addAttribute("lastpg", lastpg);
		model.addAttribute("homes", homes);
		model.addAttribute("page", pagenum);
		return "products";
	}

	@RequestMapping(value = "/database")
	public String getProperties(@RequestParam(value = "item", required = false) String item, HttpServletRequest request, Model model) {
		Map<String, String[]> list = stprop.getAllProperties();
		model.addAttribute("props", list);
		model.addAttribute("viewer", "database");
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/addProperty")
	public String addProperties(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "pro", required = false) String item,
			HttpServletRequest request, Model model) {
		if ("on".equalsIgnoreCase(delete)) {
			stprop.delete(request.getParameter("key"));
		}
		else {
			String key = request.getParameter("key");
			String value = request.getParameter("value");
			String update = request.getParameter("update");
			String desc = request.getParameter("desc");

			stprop.Insert(key, value, desc, Boolean.valueOf(update));
		}

		String resp = "redirect:/backoffice/database";
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "?pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/discounts")
	public String getDiscounts(@RequestParam(value = "pro", required = false) String item, HttpServletRequest request, Model model) {
		List<BankDiscountDTO> list = bds.getList();
		model.addAttribute("discounts", list);
		String types[] = { "OFF", "CASHBACK" };
		model.addAttribute("types", types);
		model.addAttribute("homes", homes);
		model.addAttribute("category", category);
		model.addAttribute("viewer", "offer");
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/add")
	public String addDiscount(@RequestParam(value = "first", required = false) String first, @RequestParam(value = "item", required = false) String item,
			@ModelAttribute(value = "prod") BankDiscountDTO request, BindingResult bindingResult, Model model,
			@RequestParam(value = "delete", required = false) String delete) {
		if ("on".equalsIgnoreCase(delete)) {
			bds.delete(request);
		}
		else {
			if (StringUtils.isNotBlank(first)) {
				String offset = stprop.getValueOrDefault(BackofficeConstants.IST_TIME_OFFSET, String.valueOf(BackofficeConstants.IST_OFFSET));
				if (request.getStartDate() != null) {
					request.setStartDate(new Date(request.getStartDate().getTime() - Long.parseLong(offset)));
				}
				if (request.getEndDate() != null) {
					request.setEndDate(new Date(request.getEndDate().getTime() - Long.parseLong(offset)));
				}
			}
			bds.add(request);
		}
		String resp = "redirect:/backoffice/discounts";
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "?pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/delete")
	public String deleteDiscount(@RequestParam(value = "item", required = false) String item, @ModelAttribute(value = "prod") BankDiscountDTO request,
			BindingResult bindingResult, Model model) {
		bds.delete(request);
		String resp = "redirect:/backoffice/discounts";
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "?pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/cashback")
	public String getCashbackRates(@RequestParam(value = "pro", required = false) String item, HttpServletRequest request, Model model) {
		List<CBRateDTO> list = rate.getList();
		model.addAttribute("rates", list);
		model.addAttribute("homes", homes);
		model.addAttribute("category", category);
		model.addAttribute("viewer", "cashback");
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/addCashback")
	public String addRates(@RequestParam(value = "delete", required = false) String delete, @RequestParam(value = "item", required = false) String item,
			@ModelAttribute(value = "prod") CBRateDTO request, BindingResult bindingResult, Model model) {
		if ("on".equalsIgnoreCase(delete)) {
			rate.delete(request);
		}
		else {
			rate.add(request);
		}
		String resp = "redirect:/backoffice/cashback";
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "?pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/open")
	public String openTicket(HttpServletRequest request, Model model) {
		String userId = request.getParameter("userId");
		String message = request.getParameter("message");
		tms.openTicket(userId, true, message);
		return "redirect:/backoffice/tickets?userid=" + userId;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tickets")
	public String getOpenTickets(HttpServletRequest request, Model model) {
		String userId = request.getParameter("userid");
		String email = request.getParameter("email");
		if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(email)) {
			Where statement = QueryBuilder.select("id", "email").from(keyspace, TableEnum.user_by_email.name()).where(QueryBuilder.eq("email", email));
			ResultSet row = session.execute(statement);
			if (row != null) {
				List<Row> rows = row.all();
				if (rows.size() > 0) {
					userId = rows.get(0).getString("id");
					email = rows.get(0).getString("email");
				}
			}
		}
		List<TicketDTO> tickets = null;
		if (userId == null) {
			tickets = ts.getOpenTickets();
		}
		else {
			tickets = ts.getTicketsForUser(userId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", userId);
			List<User> user = (List<User>) batchRepository.selectAll(TableEnum.user.name(), User.class, null, map, "1");
			if (user != null && user.size() > 0)
				model.addAttribute("user", user.get(0));
			model.addAttribute("userId", userId);
			model.addAttribute("email", email);
		}
		if (tickets != null && tickets.size() > 0) {
			model.addAttribute("tickets", tickets);
			String ticketId = request.getParameter("ticketId");
			if (StringUtils.isNotBlank(ticketId)) {
				model.addAttribute("selectid", ticketId);
			}
		}
		model.addAttribute("viewer", "ticket");
		return "dashboard";
	}

	@RequestMapping(value = "/message")
	public String addMsg(@RequestParam(value = "userid", required = false) String userid, HttpServletRequest request, Model model) {
		String ticketId = request.getParameter("ticketId");
		String msg = request.getParameter("message");
		tms.saveTicket(null, ticketId, false, msg);
		String resp = "redirect:/backoffice/get?ticketId=" + ticketId;
		if (StringUtils.isNotBlank(userid)) {
			resp += "&userid=" + userid;
		}
		return resp;
	}

	@RequestMapping(value = "/get")
	public String getTickets(@RequestParam(value = "ticketId", required = false) String ticketId,
			@RequestParam(value = "userid", required = false) String userid, HttpServletRequest request, Model model) {
		List<TicketMessageDTO> messages = tms.getMessages(ticketId);
		model.addAttribute("messages", messages);
		model.addAttribute("id", ticketId);
		String resp = "forward:/backoffice/tickets";
		if (StringUtils.isNotBlank(userid)) {
			resp += "?userid=" + userid;
		}
		if (StringUtils.isNotBlank(ticketId)) {
			resp += "&ticketId=" + ticketId;
		}
		return resp;
	}

	@RequestMapping(value = "/close")
	public String closeTickets(@RequestParam(value = "userid", required = false) String userid, HttpServletRequest request, Model model) {
		String ticketId = request.getParameter("ticketId");
		String time = request.getParameter("time");
		ts.closeTicket(ticketId, UUID.fromString(time), false);
		String resp = "redirect:/backoffice/tickets";
		if (StringUtils.isNotBlank(userid)) {
			resp += "?userid=" + userid;
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/user")
	public String userInfo(@RequestParam(value = "pro", required = false) String item, HttpServletRequest request, Model model) {
		String userId = request.getParameter("userid");
		String email = request.getParameter("email");
		if (StringUtils.isEmpty(userId) && !StringUtils.isEmpty(email)) {
			Where statement = QueryBuilder.select("id").from(keyspace, TableEnum.user_by_email.name()).where(QueryBuilder.eq("email", email));
			ResultSet row = session.execute(statement);
			if (row != null) {
				List<Row> rows = row.all();
				if (rows.size() > 0) {
					userId = rows.get(0).getString("id");
				}
			}
		}
		if (userId != null) {
			model.addAttribute("userid", userId);
			Map<String, Object> map = new HashMap<String, Object>();

			// Get Info of the user
			map.put("id", userId);
			List<User> user = (List<User>) batchRepository.selectAll(TableEnum.user.name(), User.class, null, map, null);
			if (user != null && user.size() > 0) {
				model.addAttribute("user", user.get(0));
				model.addAttribute("email", user.get(0).getEmail());
			}

			// Get Info of User's Account
			map.clear();
			map.put("userId", userId);
			List<UserAccountDTO> account = (List<UserAccountDTO>) batchRepository
					.selectAll(TableEnum.useraccount.name(), UserAccountDTO.class, null, map, null);
			if (account != null && account.size() > 0) {
				model.addAttribute("accounts", account);
			}

			// Get its Cashback Transactions
			List<CBReportDTO> report = (List<CBReportDTO>) batchRepository.selectAll(TableEnum.cbreport.name(), CBReportDTO.class, null, map, null);
			if (report != null && report.size() > 0) {
				model.addAttribute("reports", report);
			}

			// Get its Cashback Payments
			List<CBPaymentDTO> payment = (List<CBPaymentDTO>) batchRepository.selectAll(TableEnum.cbpayment.name(), CBPaymentDTO.class, null, map, null);
			if (payment != null && payment.size() > 0) {
				model.addAttribute("payments", payment);
			}

			// All Transactions
			List<UserTransactionDTO> transaction = new LinkedList<UserTransactionDTO>();
			List<UserTransactionDTO> resultset = (List<UserTransactionDTO>) batchRepository.selectAll(TableEnum.usertransactions.name(),
					UserTransactionDTO.class, null, map, null);
			for (UserTransactionDTO tmp : resultset) {
				if (tmp.date().after(new Date(System.currentTimeMillis() - (31 * BackofficeConstants.ONE_DAY)))) {
					transaction.add(tmp);
				}
			}
			Collections.sort(transaction, BackofficeUtil.compareUserTransaction);
			if (transaction != null && transaction.size() > 0) {
				model.addAttribute("transactions", transaction);
			}
		}

		String message = request.getParameter("message");
		if (StringUtils.isNotBlank(message)) {
			model.addAttribute("error_msg", message);
		}
		model.addAttribute("homes", homes);
		model.addAttribute("viewer", "user");
		model.addAttribute("cbReportStatus", cbReportStatus);
		model.addAttribute("cbPaymentStatus", cbPaymentStatus);
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		return "dashboard";
	}

	@RequestMapping(value = "/user_role")
	public String roleChange(@RequestParam(value = "ROLE_USER", defaultValue = "false") boolean ROLE_USER,
			@RequestParam(value = "ROLE_THIRDPARTY", defaultValue = "false") boolean ROLE_THIRDPARTY,
			@RequestParam(value = "ROLE_ADMIN", defaultValue = "false") boolean ROLE_ADMIN, @ModelAttribute(value = "user") User user,
			BindingResult bindingResult, Model model, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", user.getId());
		map.put("login", user.getLogin());
		user.setAuthorities(new HashSet<String>());
		if (ROLE_USER) {
			user.getAuthorities().add("ROLE_USER");
		}
		if (ROLE_THIRDPARTY) {
			user.getAuthorities().add("ROLE_THIRDPARTY");
		}
		if (ROLE_ADMIN) {
			user.getAuthorities().add("ROLE_ADMIN");
		}
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("authorities", user.getAuthorities());
		updateQuery.updateQuery(TableEnum.user, values, map);
		String resp = "redirect:/backoffice/user?userid=" + user.getId();
		return resp;
	}

	@RequestMapping(value = "/payment")
	public String paymentChange(@RequestParam(value = "dnm", required = false) String dnm, @RequestParam(value = "delete", required = false) String delete,
			@RequestParam(value = "item", required = false) String item, @ModelAttribute(value = "prod") CBPaymentDTO prod, BindingResult bindingResult,
			Model model, HttpServletRequest request) {
		if ("on".equalsIgnoreCase(delete)) {
			batchRepository.delete(prod);
		}
		else {
			prod.setStatus(CBStatusEnum.PAID.name());
			prod.setModifiedOn(new Date());
			batchRepository.save(prod);
			if (!"on".equalsIgnoreCase(dnm)) {
				mail.transferMoneyToAccount(prod);
			}
		}
		String resp = "redirect:/backoffice/user?userid=" + prod.getUserId();

		if (StringUtils.isNotBlank(item)) {
			resp = resp + "&pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/report")
	public String addCB(@RequestParam(value = "options", required = false) String options,
			@RequestParam(value = "dncut", defaultValue = "false") boolean doNotCut, @RequestParam(value = "receive", required = false) String receive,
			@RequestParam(value = "item", required = false) String item,
			@ModelAttribute(value = "prod") CBReportDTO dto, BindingResult bindingResult, HttpServletRequest request) {
		String resp = "redirect:/backoffice/user?userid=" + dto.getUserId();
		CBReportDTO prod = null;
		if (StringUtils.isNotBlank(options) && "Delete".equalsIgnoreCase(options)) {
			prod = QueryMapper.cBReportDTO().get(dto.getUserId(), dto.getTime());
			prod.setStatus(CBStatusEnum.DELETED.name());
		}
		else if (StringUtils.isNotBlank(options) && "Cancel".equalsIgnoreCase(options)) {
			prod = QueryMapper.cBReportDTO().get(dto.getUserId(), dto.getTime());
			prod.setStatus(CBStatusEnum.CANCELLED.name());
		}
		else if (StringUtils.isNotBlank(receive) && Boolean.valueOf(receive)) {
			prod = QueryMapper.cBReportDTO().get(dto.getUserId(), dto.getTime());
			if (StringUtils.isNotBlank(options) && "Amount Change".equalsIgnoreCase(options)) {
				prod.setAmount(dto.getAmount());
				cashbackUtil.amountChange(prod, doNotCut);
			}
			prod.setStatus(CBStatusEnum.RECEIVED.name());
			prod.setExpectedConfirmationDate(new Date(System.currentTimeMillis()));
			prod.setDoNotMail(dto.isDoNotMail());
		}
		else {
			prod = dto;
			prod.setStatus(CBStatusEnum.PENDING.name());
			prod.setExpectedConfirmationDate(BackofficeUtil.getExpectedCBDate());
			if (doNotCut) {
				prod.setCashBackAmount(BackofficeUtil.roundOff(prod.getAmount()));
			}
			else {
				prod.setCashBackAmount(BackofficeUtil.roundOff(BackofficeConstants.CONVERSION_RATE * prod.getAmount()));
			}
			if (StringUtils.isBlank(prod.getDescription()) && StringUtils.isNotBlank(prod.getProduct())) {
				prod.setDescription("Product Bought - " + prod.getProduct());
			}
		}
		prod.setModifiedOn(new Date(System.currentTimeMillis()));
		batchRepository.save(prod);

		if (prod.isActive()) {
			mail.sendCashbackEmail(prod, false, null);
		}
		
		if (!doNotCut && prod.isEligibleForCut()) {
			cashbackUtil.pendingBonusUpdate(prod);
		}

		resp += "&message=Added";
		if (StringUtils.isNotBlank(item)) {
			resp = resp + "&pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "/payout")
	public String decrypt(@RequestParam(value = "item", required = false) String item, @RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "startDate", required = false) Date startDate,
			@RequestParam(value = "endDate", required = false) Date endDate, @RequestParam(value = "save", required = false) String save,
			@ModelAttribute(value = "prod") CBReportDTO prod, BindingResult bindingResult, Model model, HttpServletRequest request) {
		try {
			if (StringUtils.isNotBlank(save)) {
				CBReportDTO dto = QueryMapper.cBReportDTO().get(prod.getUserId(), prod.getTime());
				if (StringUtils.isNotBlank(act) && (CBStatusEnum.CANCELLED.name().equals(act) || CBStatusEnum.DELETED.name().equals(act))) {
					dto.setStatus(act);
				}
				else {
					dto.setStatus(CBStatusEnum.RECEIVED.name());
				}
				dto.setExpectedConfirmationDate(new Date(System.currentTimeMillis()));
				dto.setModifiedOn(new Date(System.currentTimeMillis()));
				dto.setDoNotMail(dto.isDoNotMail());
				batchRepository.save(dto);

				if (dto.isActive()) {
					mail.sendCashbackEmail(dto, false, null);
				}
				
				if (dto.isEligibleForCut()) {
					cashbackUtil.pendingBonusUpdate(dto);
				}
			}
			if (startDate == null) {
				startDate = new Date(System.currentTimeMillis());
			}
			if (endDate == null) {
				endDate = new Date(System.currentTimeMillis() + BackofficeConstants.ONE_DAY);
			}

			if (startDate.before(endDate)) {
				UUID low = UUIDs.startOf(getStartOfDay(startDate).getTime());
				UUID high = UUIDs.endOf(getEndOfDay(endDate).getTime());
				Where statement = QueryBuilder.select().all().from(keyspace, TableEnum.cbreport.name()).allowFiltering().where(QueryBuilder.gt("time", low))
						.and(QueryBuilder.lt("time", high));
				statement.setFetchSize(BackofficeConstants.FETCHSIZE);
				ResultSet rs = session.execute(statement);
				List<CBReportDTO> list = QueryMapper.cBReportDTO().map(rs).all();
				if (list != null && list.size() > 0) {
					Collections.sort(list, BackofficeUtil.compareCBReport);
					model.addAttribute("reports", list);
				}
			}
			else {
				model.addAttribute("error_msg", "Check Dates");
			}
		} catch (Exception e) {
			log.error("", e);
			model.addAttribute("error_msg", e.getMessage());
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("viewer", "payout");
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		return "dashboard";
	}

	public List<CBReportDTO> selectCBReport(Map<String, Object> map) {
		String table = TableEnum.cbreport.name();
		try {
			Statement select = null;
			boolean first = true;
			if (map == null || map.size() == 0) {
				select = QueryBuilder.select().all().from(keyspace, table);
			}
			else {
				for (Entry<String, Object> en : map.entrySet()) {
					if (first) {
						first = false;
						select = QueryBuilder.select().all().from(keyspace, table).where(QueryBuilder.eq(en.getKey(), en.getValue()));
					}
					else {
						select = ((Where) select).and(QueryBuilder.eq(en.getKey(), en.getValue()));
					}
				}
			}
			select.setFetchSize(BackofficeConstants.FETCHSIZE);
			ResultSet rs = session.execute(select);
			return QueryMapper.cBReportDTO().map(rs).all();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	// To Save MergeProductInfoDto
	@RequestMapping(value = "/shot")
	public String saveMergeProducts(@ModelAttribute(value = "prod") MergedProductInfoDTO prod, BindingResult bindingResult, Model model,
			HttpServletRequest request) {
		String key = prod.getId().toString();
		MergedProductInfoDTO prev = dbmap.get(key);

		boolean changed = false;

		if (StringUtils.isNotBlank(prod.getProductBrand()) && !StringUtils.equalsIgnoreCase(prod.getProductBrand(), prev.getProductBrand())) {
			prev.setProductBrand(prod.getProductBrand().trim().toUpperCase());
			changed = true;
		}

		if (StringUtils.isNotBlank(prod.getProductSubBrand()) && !StringUtils.equalsIgnoreCase(prod.getProductSubBrand(), prev.getProductSubBrand())) {
			prev.setProductSubBrand(prod.getProductSubBrand().trim().toUpperCase());
			changed = true;
		}
		else if (StringUtils.isBlank(prod.getProductSubBrand()) && StringUtils.isNotBlank(prev.getProductSubBrand())) {
			prev.setProductSubBrand(null);
			changed = true;
		}

		if (StringUtils.isNotBlank(prod.getSeries()) && !StringUtils.equalsIgnoreCase(prod.getSeries(), prev.getSeries())) {
			prev.setSeries(prod.getSeries().trim().toUpperCase());
			changed = true;
		}
		else if (StringUtils.isBlank(prod.getSeries()) && StringUtils.isNotBlank(prev.getSeries())) {
			prev.setSeries(null);
			changed = true;
		}

		if (StringUtils.isNotBlank(prod.getModel()) && !StringUtils.equalsIgnoreCase(prod.getModel(), prev.getModel())) {
			prev.setModel(prod.getModel().trim().toUpperCase());
			changed = true;
		}
		else if (StringUtils.isBlank(prod.getModel()) && StringUtils.isNotBlank(prev.getModel())) {
			prev.setModel(null);
			changed = true;
		}

		if (prod.isDisabled()) {
			prev.setDisabled(true);
		}
		batchRepository.save(prev);

		if (changed) {
			indexRepo.updateName(prev.getId().toString(), prev.getName());
		}
		return "forward:/backoffice/db";
	}

	Map<String, MergedProductInfoDTO> dbmap = new HashMap<String, MergedProductInfoDTO>();

	MergedProductInfoDTO element = null;

	@RequestMapping(value = "/debug")
	public String mainBackOfficeServlet(HttpServletRequest request, Model model, @RequestParam(value = "clazz", required = false) String clazz,
			@RequestParam(value = "methoz", defaultValue = "init") String methoz, @RequestParam(value = "error_msg", required = false) String message,
			@RequestParam(value = "pro", required = false) String item, @RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "subCategory", required = false) String subCategory, @RequestParam(value = "google_url", required = false) String google_url)
			throws Exception {
		if (StringUtils.isNotBlank(clazz)) {
			try {
				Class<?> classType = Class.forName(clazz);
				Object beanOfClass = ctx.getBean(classType.getSimpleName());
				Object obj = classType.cast(beanOfClass);
				Method classMethod = obj.getClass().getMethod(methoz);
				classMethod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtils.isNotBlank(key)) {
			try {
				CategoryEnum tmp = CategoryEnum.getCategory(subCategory);
				List<ElasticSearchResponse> rs = indexRepo.Search(key, tmp);
				model.addAttribute("es_result", rs);
				model.addAttribute("key", key);
				if (tmp != null) {
					model.addAttribute("subCategory", tmp.name());
				}
			} catch (Exception e) {
				message = e.getMessage();
			}
		}
		model.addAttribute("viewer", "debug");
		model.addAttribute("clazz", null);
		model.addAttribute("methoz", null);
		if (StringUtils.isNotBlank(message)) {
			model.addAttribute("error_msg", message);
		}
		if (StringUtils.isNotBlank(item)) {
			model.addAttribute("pro", item.split(",")[0]);
		}
		if (StringUtils.isNotBlank(google_url)) {
			model.addAttribute("google_url", google_url);
		}
		model.addAttribute("category", category);
		return "dashboard";
	}

	@RequestMapping(value = "esedit")
	public String elasticSearch(@RequestParam(value = "item", required = false) String item, @RequestParam(value = "delete", required = false) String delete,
			@ModelAttribute(value = "prod") ElasticSearchResponse request, BindingResult bindingResult, Model model) {
		String resp = "redirect:/backoffice/debug?";

		if (request != null) {
			if ("on".equalsIgnoreCase(delete)) {
				indexRepo.deleteIndexById(request.getId());
			}
			else {
				indexRepo.update(request);
			}
			resp += "key=" + request.getName() + "&";
		}

		if (StringUtils.isNotBlank(item)) {
			resp = resp + "pro=" + item;
		}
		return resp;
	}

	@RequestMapping(value = "sendMailer")
	public String sendMail(@RequestParam(value = "name", defaultValue = "Friend") String name,
			@RequestParam(value = "content", required = false) String content, @RequestParam(value = "subject", required = false) String subject,
			@RequestParam(value = "to", defaultValue = "ALL") String to, Model model) {
		Map<String, String> emails = new HashMap<String, String>();
		if (StringUtils.isNotBlank(to) && StringUtils.isNotBlank(content)) {
			if ("all".equalsIgnoreCase(to)) {
				Select statement = QueryBuilder.select("email", "firstname").from(keyspace, TableEnum.user.name());
				statement.setFetchSize(BackofficeConstants.FETCHSIZE);
				ResultSet rs = session.execute(statement);
				List<Row> rows = rs.all();
				for (Row row : rows) {
					String tmp = row.getString("email");
					String fname = row.getString("firstname");
					if (StringUtils.isNotBlank(tmp)) {
						emails.put(tmp, fname);
					}
				}
			}
			else {
				emails.put(to, name);
			}
		}
		for (Entry<String, String> entry : emails.entrySet()) {
			String eml = entry.getKey();
			String text = content.replace("{firstname}", entry.getValue());
			mail.sendEmail(eml, subject, text, false, true);
		}
		model.addAttribute("msg", "Sent Mail to " + emails.size() + " subscribers");
		return "sendmail";
	}

	private List<UserTransactionDTO> list = null;

	@RequestMapping(value = "convert")
	public String user_coversions(@RequestParam(value = "sendmail", required = false) String sendmail,
			@RequestParam(value = "product", required = false) String product, @RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "home", defaultValue = "AMAZON") String home, @RequestParam(value = "startDate", required = false) Date date, Model model) {
		if (StringUtils.isNotBlank(sendmail) && Boolean.valueOf(sendmail)) {
			boolean sent = false;
			String message = "MAIL SENT";
			if (StringUtils.isNotBlank(userId)) {
				CBReportDTO prod = new CBReportDTO(userId, UUIDs.timeBased(), CBStatusEnum.TRACKED.name(), product, home);
				mail.sendCashbackEmail(prod, false, null);
				sent = true;
			}
			if (!sent) {
				message = "MAIL NOT SENT";
			}
			model.addAttribute("error_msg", message);
		}
		else {
			if (date == null) {
				date = new Date(System.currentTimeMillis() - BackofficeConstants.ONE_DAY);
			}
			if (System.currentTimeMillis() >= date.getTime()) {
				UUID low = UUIDs.startOf(getStartOfDay(date).getTime());
				UUID high = UUIDs.endOf(getEndOfDay(date).getTime());
				Where statement = QueryBuilder.select().all().from(keyspace, TableEnum.usertransactions.name()).allowFiltering()
						.where(QueryBuilder.eq("home", home.toUpperCase())).and(QueryBuilder.gt("time", low)).and(QueryBuilder.lt("time", high));
				statement.setFetchSize(BackofficeConstants.FETCHSIZE);
				ResultSet rs = session.execute(statement);
				list = QueryMapper.userTransactionDTO().map(rs).all();
			}
			else {
				model.addAttribute("error_msg", "Wrong Date");
			}
		}
		if (list != null && list.size() > 0) {
			Collections.sort(list, BackofficeUtil.compareUserTransaction);
		}
		model.addAttribute("obj", list);
		model.addAttribute("homes", homes);
		model.addAttribute("home", home);
		model.addAttribute("startDate", date);
		model.addAttribute("viewer", "transaction");
		return "dashboard";
	}

	@RequestMapping(value = "recover")
	public String recover(@RequestParam(value = "expireDate", required = false) Date date) {
		log.info("Recovery Start");
		backup.ReadAndPersistToTables(date);
		return "redirect:/backoffice/debug";
	}

	@RequestMapping(value = "addmoney")
	public String addMoney(@ModelAttribute(value = "prod") CBReportDTO prod, BindingResult bindingResult, Model model, HttpServletRequest request) {
		batchRepository.save(prod);
		String resp = "redirect:/backoffice/user?userid=" + prod.getUserId();
		return resp;
	}

	@Inject
	private PayoomCoupons couponUpload;

	@Inject
	private VcommCoupons couponUploadV;

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public String uploadFile(@RequestParam(value = "party") String party, @RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
		String msg = "";
		if (file != null) {
			String[] tmp = file.getOriginalFilename().split("\\.");
			String ext = tmp[tmp.length - 1];
			if ("csv".equalsIgnoreCase(ext)) {
				if ("PAYOOM".equalsIgnoreCase(party)) {
					msg = couponUpload.process(file);
				}
				else {
					msg = couponUploadV.process(file);
				}
			}
			else {
				msg = "CSV File Not Found";
			}
		}
		else {
			msg = "Corrupted File";
		}
		return "redirect:/backoffice/debug?error_msg=" + msg;
	}

	@RequestMapping(value = "mergeDelete")
	public String mergeDelete(@RequestParam(value = "subcategoryname", required = false) String subcategoryname,
			@RequestParam(value = "deletedata", required = false) String deletedata) {
		log.info("mergeDelete() Enter");
		boolean delete = false;
		if ("on".equalsIgnoreCase(deletedata)) {
			delete = true;
		}
		BatchStatement bs = new BatchStatement();
		CategoryEnum tmp = CategoryEnum.getCategory(subcategoryname);
		if (tmp != null) {
			Where stmt = QueryBuilder.select("id").from(keyspace, TableEnum.merged_product_info.name()).where(QueryBuilder.eq("subcategoryname", tmp.name()));
			stmt.setFetchSize(BackofficeConstants.FETCHSIZE);
			ResultSet rs = session.execute(stmt);
			Iterator<Row> it = rs.iterator();
			while (it.hasNext()) {
				Row row = it.next();
				UUID id = row.getUUID("id");
				bs.add(QueryBuilder.delete().all().from(keyspace, TableEnum.merged_product_info.name()).where(QueryBuilder.eq("id", id)));
				if (bs.size() > BackofficeConstants.BATCHSIZE) {
					session.execute(bs);
					bs.clear();
				}
			}
			session.execute(bs);
			bs.clear();
			stmt = QueryBuilder.select("id", "subcategoryname", "home").from(keyspace, TableEnum.reviewed_product_info.name()).allowFiltering()
					.where(QueryBuilder.eq("subcategoryname", tmp.name()));
			stmt.setFetchSize(BackofficeConstants.FETCHSIZE);
			rs = session.execute(stmt);
			it = rs.iterator();
			while (it.hasNext()) {
				Row row = it.next();
				String _id = row.getString("id");
				String subcategory = row.getString("subcategoryname");
				String home = row.getString("home");
				if (delete) {
					bs.add(QueryBuilder.delete().all().from(keyspace, TableEnum.reviewed_product_info.name()).where(QueryBuilder.eq("id", _id))
							.and(QueryBuilder.eq("subcategoryname", subcategory)).and(QueryBuilder.eq("home", home)));
				}
				else {
					bs.add(QueryBuilder.update(keyspace, TableEnum.reviewed_product_info.name()).with(QueryBuilder.set("ismerged", false))
							.where(QueryBuilder.eq("id", _id)).and(QueryBuilder.eq("subcategoryname", subcategory)).and(QueryBuilder.eq("home", home)));
				}
				if (bs.size() > BackofficeConstants.BATCHSIZE) {
					session.execute(bs);
					bs.clear();
				}
			}
			session.execute(bs);
			bs.clear();
		}
		log.info("mergeDelete() Exit");
		return "redirect:/backoffice/debug?error_msg=complete";
	}
}
