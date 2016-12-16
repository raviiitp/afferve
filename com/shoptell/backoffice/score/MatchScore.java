/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.backoffice.score;

import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.simmetrics.StringMetric;
import org.simmetrics.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import com.shoptell.backoffice.repository.dto.MergedProductInfoDTO;
import com.shoptell.backoffice.repository.dto.ReviewedProductInfoDTO;

/**
 * @author abhishekagarwal
 *
 */
@Named(value = "MatchScore")
public class MatchScore {

	public void init() {
		preprocess();
		execute();
		postprocess();
	}

	private void preprocess() {
		// TODO Auto-generated method stub

	}

	private void execute() {
		// TODO Auto-generated method stub

	}

	private void postprocess() {
		// TODO Auto-generated method stub

	}

	public float compare(ReviewedProductInfoDTO a, MergedProductInfoDTO b) {
		float score = 0;
		if (StringUtils.isNotBlank(a.getName()) && StringUtils.isNotBlank(b.getName())) {
			score = compare(a.getName().replaceAll("\\s", ""), b.getName().replaceAll("\\s", ""), 'C');
			if (score == 1) {
				return 1;
			}
			score = compare(a.getName(), b.getName(), 'C');
			if (score == 1) {
				return 1;
			}
			else if (score < 0.5) {
				return 0;
			}
		}
		if (StringUtils.equalsIgnoreCase(a.getProductBrand(), b.getProductBrand())
				|| (StringUtils.isBlank(a.getProductBrand()) && StringUtils.isBlank(b.getProductBrand()))) {
			if (StringUtils.equalsIgnoreCase(a.getProductSubBrand(), b.getProductSubBrand())
					|| (StringUtils.isBlank(a.getProductSubBrand()) && StringUtils.isBlank(b.getProductSubBrand()))) {
				String aStr = "", bStr = "";
				
				if (StringUtils.isNotBlank(a.getSeries())) {
					aStr = a.getSeries() + " ";
				}
				if (StringUtils.isNotBlank(b.getSeries())) {
					bStr = b.getSeries() + " ";
				}
				if (StringUtils.isNotBlank(a.getModel())) {
					aStr += a.getModel() + " ";
				}
				if (StringUtils.isNotBlank(b.getModel())) {
					bStr += b.getModel() + " ";
				}
				aStr = aStr.trim();
				bStr = bStr.trim();
				
				if (StringUtils.isNotBlank(aStr) && StringUtils.isNotBlank(bStr)) {
					return compare(aStr, bStr, 'L');
				}
				else if (StringUtils.isEmpty(aStr) && StringUtils.isEmpty(bStr)) {
					return 1;
				}
			}
			else {
				String aa = a.getName();
				String bb = b.getName();

				int a_indx = a.getName().indexOf(' ');
				int b_indx = b.getName().indexOf(' ');

				if (a_indx > 0 && b_indx > 0) {
					aa = a.getName().substring(a_indx);
					bb = b.getName().substring(b_indx);
				}
				if (StringUtils.isNotBlank(aa) && StringUtils.isNotBlank(bb)) {
					score = compare(aa, bb, 'C');
					if (score < 0.5) {
						return 0;
					}
					return compare(aa, bb, 'L');
				}
			}
		}
		return 0;
	}

	public float compare(String a, String b, char type) {
		StringMetric metric = null;
		a = a.trim();
		b = b.trim();
		
		if (type == 'L') {
			metric = StringMetricBuilder.with(new Levenshtein()).simplify(Simplifiers.removeDiacritics()).simplify(Simplifiers.toLowerCase()).build();
			return metric.compare(a, b);
		}
		else if (type == 'C') {
			metric = StringMetricBuilder.with(new CosineSimilarity<String>()).simplify(Simplifiers.removeDiacritics()).simplify(Simplifiers.toLowerCase())
					.tokenize(Tokenizers.whitespace()).build();
			return metric.compare(a, b);
		}
		return 0;
	}

	public float getScore(String name, String search_key) {
		float score = compare(name, search_key, 'C') + compare(name, search_key, 'L');
		if (score >= 0.5){
			return 3-score;
		}
		return 0;
	}
	
	/*public static void main(String[] args) {
		MatchScore m = new MatchScore();
		System.out.println(m.compare("Microsoft Nintendo", "Microsoft", 'C'));
		System.out.println(m.compare("Microsoft Nintendo", "Nintendo", 'C'));
		System.out.println(m.compare("Microsoft Nintendo", "Microsoft", 'L'));
		System.out.println(m.compare("Microsoft Nintendo", "Nintendo", 'L'));
		System.out.println(m.compare("Nintendo Microsoft", "Microsoft", 'C'));
		System.out.println(m.compare("Nintendo Microsoft", "Nintendo", 'C'));
		System.out.println(m.compare("Nintendo Microsoft", "Microsoft", 'L'));
		System.out.println(m.compare("Nintendo Microsoft", "Nintendo", 'L'));
	}*/
}
