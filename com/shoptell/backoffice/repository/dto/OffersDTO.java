/***************************************************************************
 * Copyright (c) 2016 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.repository.dto;

import java.io.Serializable;

import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "offers")
public class OffersDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean published;
	private String home;
	private String thirdparty;
	private String id;
	private String name;
	private String description;
	private String require_approval;
	private String require_terms_and_conditions;
	private String terms_and_conditions;
	private String preview_url;
	private String currency;
	private String default_payout;
	private String status;
	private String expiration_date;
	private String payout_type;
	private String percent_payout;
	private String featured;
	private String conversion_cap;
	private String monthly_conversion_cap;
	private String payout_cap;
	private String monthly_payout_cap;
	private String allow_website_links;
	private String allow_direct_links;
	private String show_custom_variables;
	private String show_mail_list;
	private String dne_list_id;
	private String email_instructions;
	private String email_instructions_from;
	private String email_instructions_subject;
	private String has_goals_enabled;
	private String default_goal_name;
	private String use_target_rules;
	private String is_expired;
	private String dne_download_url;
	private String dne_unsubscribe_url;
	private boolean dne_third_party_list;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the require_approval
	 */
	public String getRequire_approval() {
		return require_approval;
	}
	/**
	 * @param require_approval the require_approval to set
	 */
	public void setRequire_approval(String require_approval) {
		this.require_approval = require_approval;
	}
	/**
	 * @return the require_terms_and_conditions
	 */
	public String getRequire_terms_and_conditions() {
		return require_terms_and_conditions;
	}
	/**
	 * @param require_terms_and_conditions the require_terms_and_conditions to set
	 */
	public void setRequire_terms_and_conditions(String require_terms_and_conditions) {
		this.require_terms_and_conditions = require_terms_and_conditions;
	}
	/**
	 * @return the terms_and_conditions
	 */
	public String getTerms_and_conditions() {
		return terms_and_conditions;
	}
	/**
	 * @param terms_and_conditions the terms_and_conditions to set
	 */
	public void setTerms_and_conditions(String terms_and_conditions) {
		this.terms_and_conditions = terms_and_conditions;
	}
	/**
	 * @return the preview_url
	 */
	public String getPreview_url() {
		return preview_url;
	}
	/**
	 * @param preview_url the preview_url to set
	 */
	public void setPreview_url(String preview_url) {
		this.preview_url = preview_url;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the default_payout
	 */
	public String getDefault_payout() {
		return default_payout;
	}
	/**
	 * @param default_payout the default_payout to set
	 */
	public void setDefault_payout(String default_payout) {
		this.default_payout = default_payout;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the expiration_date
	 */
	public String getExpiration_date() {
		return expiration_date;
	}
	/**
	 * @param expiration_date the expiration_date to set
	 */
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}
	/**
	 * @return the payout_type
	 */
	public String getPayout_type() {
		return payout_type;
	}
	/**
	 * @param payout_type the payout_type to set
	 */
	public void setPayout_type(String payout_type) {
		this.payout_type = payout_type;
	}
	/**
	 * @return the percent_payout
	 */
	public String getPercent_payout() {
		return percent_payout;
	}
	/**
	 * @param percent_payout the percent_payout to set
	 */
	public void setPercent_payout(String percent_payout) {
		this.percent_payout = percent_payout;
	}
	/**
	 * @return the featured
	 */
	public String getFeatured() {
		return featured;
	}
	/**
	 * @param featured the featured to set
	 */
	public void setFeatured(String featured) {
		this.featured = featured;
	}
	/**
	 * @return the conversion_cap
	 */
	public String getConversion_cap() {
		return conversion_cap;
	}
	/**
	 * @param conversion_cap the conversion_cap to set
	 */
	public void setConversion_cap(String conversion_cap) {
		this.conversion_cap = conversion_cap;
	}
	/**
	 * @return the monthly_conversion_cap
	 */
	public String getMonthly_conversion_cap() {
		return monthly_conversion_cap;
	}
	/**
	 * @param monthly_conversion_cap the monthly_conversion_cap to set
	 */
	public void setMonthly_conversion_cap(String monthly_conversion_cap) {
		this.monthly_conversion_cap = monthly_conversion_cap;
	}
	/**
	 * @return the payout_cap
	 */
	public String getPayout_cap() {
		return payout_cap;
	}
	/**
	 * @param payout_cap the payout_cap to set
	 */
	public void setPayout_cap(String payout_cap) {
		this.payout_cap = payout_cap;
	}
	/**
	 * @return the monthly_payout_cap
	 */
	public String getMonthly_payout_cap() {
		return monthly_payout_cap;
	}
	/**
	 * @param monthly_payout_cap the monthly_payout_cap to set
	 */
	public void setMonthly_payout_cap(String monthly_payout_cap) {
		this.monthly_payout_cap = monthly_payout_cap;
	}
	/**
	 * @return the allow_website_links
	 */
	public String getAllow_website_links() {
		return allow_website_links;
	}
	/**
	 * @param allow_website_links the allow_website_links to set
	 */
	public void setAllow_website_links(String allow_website_links) {
		this.allow_website_links = allow_website_links;
	}
	/**
	 * @return the allow_direct_links
	 */
	public String getAllow_direct_links() {
		return allow_direct_links;
	}
	/**
	 * @param allow_direct_links the allow_direct_links to set
	 */
	public void setAllow_direct_links(String allow_direct_links) {
		this.allow_direct_links = allow_direct_links;
	}
	/**
	 * @return the show_custom_variables
	 */
	public String getShow_custom_variables() {
		return show_custom_variables;
	}
	/**
	 * @param show_custom_variables the show_custom_variables to set
	 */
	public void setShow_custom_variables(String show_custom_variables) {
		this.show_custom_variables = show_custom_variables;
	}
	/**
	 * @return the show_mail_list
	 */
	public String getShow_mail_list() {
		return show_mail_list;
	}
	/**
	 * @param show_mail_list the show_mail_list to set
	 */
	public void setShow_mail_list(String show_mail_list) {
		this.show_mail_list = show_mail_list;
	}
	/**
	 * @return the dne_list_id
	 */
	public String getDne_list_id() {
		return dne_list_id;
	}
	/**
	 * @param dne_list_id the dne_list_id to set
	 */
	public void setDne_list_id(String dne_list_id) {
		this.dne_list_id = dne_list_id;
	}
	/**
	 * @return the email_instructions
	 */
	public String getEmail_instructions() {
		return email_instructions;
	}
	/**
	 * @param email_instructions the email_instructions to set
	 */
	public void setEmail_instructions(String email_instructions) {
		this.email_instructions = email_instructions;
	}
	/**
	 * @return the email_instructions_from
	 */
	public String getEmail_instructions_from() {
		return email_instructions_from;
	}
	/**
	 * @param email_instructions_from the email_instructions_from to set
	 */
	public void setEmail_instructions_from(String email_instructions_from) {
		this.email_instructions_from = email_instructions_from;
	}
	/**
	 * @return the email_instructions_subject
	 */
	public String getEmail_instructions_subject() {
		return email_instructions_subject;
	}
	/**
	 * @param email_instructions_subject the email_instructions_subject to set
	 */
	public void setEmail_instructions_subject(String email_instructions_subject) {
		this.email_instructions_subject = email_instructions_subject;
	}
	/**
	 * @return the has_goals_enabled
	 */
	public String getHas_goals_enabled() {
		return has_goals_enabled;
	}
	/**
	 * @param has_goals_enabled the has_goals_enabled to set
	 */
	public void setHas_goals_enabled(String has_goals_enabled) {
		this.has_goals_enabled = has_goals_enabled;
	}
	/**
	 * @return the default_goal_name
	 */
	public String getDefault_goal_name() {
		return default_goal_name;
	}
	/**
	 * @param default_goal_name the default_goal_name to set
	 */
	public void setDefault_goal_name(String default_goal_name) {
		this.default_goal_name = default_goal_name;
	}
	/**
	 * @return the use_target_rules
	 */
	public String getUse_target_rules() {
		return use_target_rules;
	}
	/**
	 * @param use_target_rules the use_target_rules to set
	 */
	public void setUse_target_rules(String use_target_rules) {
		this.use_target_rules = use_target_rules;
	}
	/**
	 * @return the is_expired
	 */
	public String getIs_expired() {
		return is_expired;
	}
	/**
	 * @param is_expired the is_expired to set
	 */
	public void setIs_expired(String is_expired) {
		this.is_expired = is_expired;
	}
	/**
	 * @return the dne_download_url
	 */
	public String getDne_download_url() {
		return dne_download_url;
	}
	/**
	 * @param dne_download_url the dne_download_url to set
	 */
	public void setDne_download_url(String dne_download_url) {
		this.dne_download_url = dne_download_url;
	}
	/**
	 * @return the dne_unsubscribe_url
	 */
	public String getDne_unsubscribe_url() {
		return dne_unsubscribe_url;
	}
	/**
	 * @param dne_unsubscribe_url the dne_unsubscribe_url to set
	 */
	public void setDne_unsubscribe_url(String dne_unsubscribe_url) {
		this.dne_unsubscribe_url = dne_unsubscribe_url;
	}
	/**
	 * @return the dne_third_party_list
	 */
	public boolean isDne_third_party_list() {
		return dne_third_party_list;
	}
	/**
	 * @param dne_third_party_list the dne_third_party_list to set
	 */
	public void setDne_third_party_list(boolean dne_third_party_list) {
		this.dne_third_party_list = dne_third_party_list;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allow_direct_links == null) ? 0 : allow_direct_links.hashCode());
		result = prime * result + ((allow_website_links == null) ? 0 : allow_website_links.hashCode());
		result = prime * result + ((conversion_cap == null) ? 0 : conversion_cap.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((default_goal_name == null) ? 0 : default_goal_name.hashCode());
		result = prime * result + ((default_payout == null) ? 0 : default_payout.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((dne_download_url == null) ? 0 : dne_download_url.hashCode());
		result = prime * result + ((dne_list_id == null) ? 0 : dne_list_id.hashCode());
		result = prime * result + (dne_third_party_list ? 1231 : 1237);
		result = prime * result + ((dne_unsubscribe_url == null) ? 0 : dne_unsubscribe_url.hashCode());
		result = prime * result + ((email_instructions == null) ? 0 : email_instructions.hashCode());
		result = prime * result + ((email_instructions_from == null) ? 0 : email_instructions_from.hashCode());
		result = prime * result + ((email_instructions_subject == null) ? 0 : email_instructions_subject.hashCode());
		result = prime * result + ((expiration_date == null) ? 0 : expiration_date.hashCode());
		result = prime * result + ((featured == null) ? 0 : featured.hashCode());
		result = prime * result + ((has_goals_enabled == null) ? 0 : has_goals_enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((is_expired == null) ? 0 : is_expired.hashCode());
		result = prime * result + ((monthly_conversion_cap == null) ? 0 : monthly_conversion_cap.hashCode());
		result = prime * result + ((monthly_payout_cap == null) ? 0 : monthly_payout_cap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((payout_cap == null) ? 0 : payout_cap.hashCode());
		result = prime * result + ((payout_type == null) ? 0 : payout_type.hashCode());
		result = prime * result + ((percent_payout == null) ? 0 : percent_payout.hashCode());
		result = prime * result + ((preview_url == null) ? 0 : preview_url.hashCode());
		result = prime * result + ((require_approval == null) ? 0 : require_approval.hashCode());
		result = prime * result + ((require_terms_and_conditions == null) ? 0 : require_terms_and_conditions.hashCode());
		result = prime * result + ((show_custom_variables == null) ? 0 : show_custom_variables.hashCode());
		result = prime * result + ((show_mail_list == null) ? 0 : show_mail_list.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((terms_and_conditions == null) ? 0 : terms_and_conditions.hashCode());
		result = prime * result + ((use_target_rules == null) ? 0 : use_target_rules.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OffersDTO other = (OffersDTO) obj;
		if (allow_direct_links == null) {
			if (other.allow_direct_links != null)
				return false;
		}
		else if (!allow_direct_links.equals(other.allow_direct_links))
			return false;
		if (allow_website_links == null) {
			if (other.allow_website_links != null)
				return false;
		}
		else if (!allow_website_links.equals(other.allow_website_links))
			return false;
		if (conversion_cap == null) {
			if (other.conversion_cap != null)
				return false;
		}
		else if (!conversion_cap.equals(other.conversion_cap))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		}
		else if (!currency.equals(other.currency))
			return false;
		if (default_goal_name == null) {
			if (other.default_goal_name != null)
				return false;
		}
		else if (!default_goal_name.equals(other.default_goal_name))
			return false;
		if (default_payout == null) {
			if (other.default_payout != null)
				return false;
		}
		else if (!default_payout.equals(other.default_payout))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (dne_download_url == null) {
			if (other.dne_download_url != null)
				return false;
		}
		else if (!dne_download_url.equals(other.dne_download_url))
			return false;
		if (dne_list_id == null) {
			if (other.dne_list_id != null)
				return false;
		}
		else if (!dne_list_id.equals(other.dne_list_id))
			return false;
		if (dne_third_party_list != other.dne_third_party_list)
			return false;
		if (dne_unsubscribe_url == null) {
			if (other.dne_unsubscribe_url != null)
				return false;
		}
		else if (!dne_unsubscribe_url.equals(other.dne_unsubscribe_url))
			return false;
		if (email_instructions == null) {
			if (other.email_instructions != null)
				return false;
		}
		else if (!email_instructions.equals(other.email_instructions))
			return false;
		if (email_instructions_from == null) {
			if (other.email_instructions_from != null)
				return false;
		}
		else if (!email_instructions_from.equals(other.email_instructions_from))
			return false;
		if (email_instructions_subject == null) {
			if (other.email_instructions_subject != null)
				return false;
		}
		else if (!email_instructions_subject.equals(other.email_instructions_subject))
			return false;
		if (expiration_date == null) {
			if (other.expiration_date != null)
				return false;
		}
		else if (!expiration_date.equals(other.expiration_date))
			return false;
		if (featured == null) {
			if (other.featured != null)
				return false;
		}
		else if (!featured.equals(other.featured))
			return false;
		if (has_goals_enabled == null) {
			if (other.has_goals_enabled != null)
				return false;
		}
		else if (!has_goals_enabled.equals(other.has_goals_enabled))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (is_expired == null) {
			if (other.is_expired != null)
				return false;
		}
		else if (!is_expired.equals(other.is_expired))
			return false;
		if (monthly_conversion_cap == null) {
			if (other.monthly_conversion_cap != null)
				return false;
		}
		else if (!monthly_conversion_cap.equals(other.monthly_conversion_cap))
			return false;
		if (monthly_payout_cap == null) {
			if (other.monthly_payout_cap != null)
				return false;
		}
		else if (!monthly_payout_cap.equals(other.monthly_payout_cap))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (payout_cap == null) {
			if (other.payout_cap != null)
				return false;
		}
		else if (!payout_cap.equals(other.payout_cap))
			return false;
		if (payout_type == null) {
			if (other.payout_type != null)
				return false;
		}
		else if (!payout_type.equals(other.payout_type))
			return false;
		if (percent_payout == null) {
			if (other.percent_payout != null)
				return false;
		}
		else if (!percent_payout.equals(other.percent_payout))
			return false;
		if (preview_url == null) {
			if (other.preview_url != null)
				return false;
		}
		else if (!preview_url.equals(other.preview_url))
			return false;
		if (require_approval == null) {
			if (other.require_approval != null)
				return false;
		}
		else if (!require_approval.equals(other.require_approval))
			return false;
		if (require_terms_and_conditions == null) {
			if (other.require_terms_and_conditions != null)
				return false;
		}
		else if (!require_terms_and_conditions.equals(other.require_terms_and_conditions))
			return false;
		if (show_custom_variables == null) {
			if (other.show_custom_variables != null)
				return false;
		}
		else if (!show_custom_variables.equals(other.show_custom_variables))
			return false;
		if (show_mail_list == null) {
			if (other.show_mail_list != null)
				return false;
		}
		else if (!show_mail_list.equals(other.show_mail_list))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
			return false;
		if (terms_and_conditions == null) {
			if (other.terms_and_conditions != null)
				return false;
		}
		else if (!terms_and_conditions.equals(other.terms_and_conditions))
			return false;
		if (use_target_rules == null) {
			if (other.use_target_rules != null)
				return false;
		}
		else if (!use_target_rules.equals(other.use_target_rules))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OffersDTO [id=" + id + ", name=" + name + ", description=" + description + ", require_approval=" + require_approval
				+ ", require_terms_and_conditions=" + require_terms_and_conditions + ", terms_and_conditions=" + terms_and_conditions + ", preview_url="
				+ preview_url + ", currency=" + currency + ", default_payout=" + default_payout + ", status=" + status + ", expiration_date=" + expiration_date
				+ ", payout_type=" + payout_type + ", percent_payout=" + percent_payout + ", featured=" + featured + ", conversion_cap=" + conversion_cap
				+ ", monthly_conversion_cap=" + monthly_conversion_cap + ", payout_cap=" + payout_cap + ", monthly_payout_cap=" + monthly_payout_cap
				+ ", allow_website_links=" + allow_website_links + ", allow_direct_links=" + allow_direct_links + ", show_custom_variables="
				+ show_custom_variables + ", show_mail_list=" + show_mail_list + ", dne_list_id=" + dne_list_id + ", email_instructions=" + email_instructions
				+ ", email_instructions_from=" + email_instructions_from + ", email_instructions_subject=" + email_instructions_subject
				+ ", has_goals_enabled=" + has_goals_enabled + ", default_goal_name=" + default_goal_name + ", use_target_rules=" + use_target_rules
				+ ", is_expired=" + is_expired + ", dne_download_url=" + dne_download_url + ", dne_unsubscribe_url=" + dne_unsubscribe_url
				+ ", dne_third_party_list=" + dne_third_party_list + "]";
	}
	/**
	 * @return the thirdparty
	 */
	public String getThirdparty() {
		return thirdparty;
	}
	/**
	 * @param thirdparty the thirdparty to set
	 */
	public void setThirdparty(String thirdparty) {
		this.thirdparty = thirdparty;
	}
	/**
	 * @return the home
	 */
	public String getHome() {
		return home;
	}
	/**
	 * @param home the home to set
	 */
	public void setHome(String home) {
		this.home = home;
	}
	/**
	 * @return the published
	 */
	public boolean isPublished() {
		return published;
	}
	/**
	 * @param published the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
	}

}
