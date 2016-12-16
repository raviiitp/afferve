/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.config;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import com.github.greengerong.PreRenderSEOFilter;
import com.shoptell.web.filter.CachingHttpHeadersFilter;
import com.shoptell.web.filter.StaticResourcesProductionFilter;
import com.shoptell.web.filter.gzip.GZipServletFilter;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

	private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

	@Inject
	private Environment env;

	@Autowired(required = false)
	private MetricRegistry metricRegistry;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
		EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
		if (!env.acceptsProfiles(Constants.SPRING_PROFILE_FAST)) {
			initMetrics(servletContext, disps);
		}
		if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
			initCachingHttpHeadersFilter(servletContext, disps);
			initStaticResourcesProductionFilter(servletContext, disps);
			initGzipFilter(servletContext, disps);
		}
		initCachingHttpHeadersFilter(servletContext, disps);
		// initStaticResourcesProductionFilter(servletContext, disps);
		initGzipFilter(servletContext, disps);
		initPrerenderFilter(servletContext, disps);
		log.info("Web application fully configured");
	}

	/**
	 * Set up Mime types.
	 */
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
		// IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
		mappings.add("html", "text/html;charset=utf-8");
		// CloudFoundry issue, see
		// https://github.com/cloudfoundry/gorouter/issues/64
		mappings.add("json", "text/html;charset=utf-8");
		container.setMimeMappings(mappings);
	}

	/**
	 * Initializes the GZip filter.
	 */
	private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
		log.debug("Registering GZip Filter");
		FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
		Map<String, String> parameters = new HashMap<>();
		compressingFilter.setInitParameters(parameters);
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.css");
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.json");
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.html");
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.js");
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.svg");
		compressingFilter.addMappingForUrlPatterns(disps, true, "*.ttf");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/images/*");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/metrics/*");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/searchctrl");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/searchListCtrl");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/productDescCtrl");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/getCoupons");
		compressingFilter.addMappingForUrlPatterns(disps, true, "/getRechargeCouponsWithCouponCode");

		compressingFilter.addMappingForUrlPatterns(disps, true, "/api/setting/getWalletReport");
		
		//compressingFilter.addMappingForUrlPatterns(disps, true, "/api/setting/getPaymentReport");
		//compressingFilter.addMappingForUrlPatterns(disps, true, "/api/setting/getTicketList");
		compressingFilter.setAsyncSupported(true);
	}

	/**
	 * Initializes the static resources production Filter.
	 */
	private void initStaticResourcesProductionFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {

		log.debug("Registering static resources production Filter");
		FilterRegistration.Dynamic staticResourcesProductionFilter = servletContext.addFilter("staticResourcesProductionFilter",
				new StaticResourcesProductionFilter());

		staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
		staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/index.html");
		staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
		staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
		staticResourcesProductionFilter.setAsyncSupported(true);
	}

	/**
	 * Initializes the cachig HTTP Headers Filter.
	 */
	private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
		log.debug("Registering Caching HTTP Headers Filter");
		FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter(env));

		cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
		cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
		cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "*.js");
		cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "*.css");
		cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "*.ico");
		cachingHttpHeadersFilter.setAsyncSupported(true);
	}

	/**
	 * Initializes Metrics.
	 */
	private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
		log.debug("Initializing Metrics registries");
		servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
		servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);

		log.debug("Registering Metrics Filter");
		FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter", new InstrumentedFilter());

		metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
		metricsFilter.setAsyncSupported(true);

		log.debug("Registering Metrics Servlet");
		ServletRegistration.Dynamic metricsAdminServlet = servletContext.addServlet("metricsServlet", new MetricsServlet());

		metricsAdminServlet.addMapping("/metrics/metrics/*");
		metricsAdminServlet.setAsyncSupported(true);
		metricsAdminServlet.setLoadOnStartup(2);
	}

	private void initPrerenderFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
		log.debug("Registering prerender Filter");
		FilterRegistration.Dynamic prerenderFilter = 
				servletContext.addFilter("prerender", new PreRenderSEOFilter());
		prerenderFilter.setInitParameter("prerenderToken", "oK6Bg58vHjbl8acZvey6");
		prerenderFilter.setInitParameter("prerenderServiceUrl", "http://localhost:3000/");

		prerenderFilter.addMappingForUrlPatterns(disps, true, "/*");
		prerenderFilter.setAsyncSupported(true);
	}
}
