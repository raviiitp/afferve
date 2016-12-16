/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.config.elasticsearch;

import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * @author abhishekagarwal
 *
 */
@Named
public class ElasticSearchClient {

	private static final Logger log = LoggerFactory.getLogger(ElasticSearchClient.class);

	private TransportClient client;
	
	@Inject
	private Environment env;

	static final String COLON = ":";
	static final String COMMA = ",";
	private String clusterNodes;
	private String clusterName;
	private Boolean clientTransportSniff = true;
	private Boolean clientIgnoreClusterName = Boolean.FALSE;
	private String clientPingTimeout = "5s";
	private String clientNodesSamplerInterval = "5000s";
	
	public void onLoad(){
		clusterName = env.getProperty("es.cluster.name", "elasticsearch");
		//clusterNodes = env.getProperty("es.cluster.nodes", "192.168.1.111:9300,192.168.1.5:9300");
		clusterNodes = env.getProperty("es.cluster.nodes", "127.0.0.1:9300");
	}

	@PreDestroy
	public void destroy() throws Exception {
		try {
			log.info("Closing elasticSearch  client");
			if (client != null) {
				client.close();
			}
		} catch (final Exception e) {
			log.error("Error closing ElasticSearch client: ", e);
		}
	}

	@PostConstruct
	public void start() {
		onLoad();
		try {
			buildClient();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public TransportClient getObject() throws Exception {
		return client;
	}

	protected void buildClient() throws Exception {
		client = new TransportClient(settings());
		for (String clusterNode : split(clusterNodes, COMMA)) {
			String hostName = substringBefore(clusterNode, COLON);
			String port = substringAfter(clusterNode, COLON);
			log.info("adding transport node : " + clusterNode);
			client.addTransportAddress(new InetSocketTransportAddress(hostName, Integer.valueOf(port)));
		}
		client.connectedNodes();
	}

	private Settings settings() {
		/*
		 * if (properties != null) { return
		 * settingsBuilder().put(properties).build(); }
		 */
		return settingsBuilder().put("cluster.name", clusterName).put("client.transport.sniff", clientTransportSniff)
				.put("client.transport.ignore_cluster_name", clientIgnoreClusterName).put("client.transport.ping_timeout", clientPingTimeout)
				.put("client.transport.nodes_sampler_interval", clientNodesSamplerInterval).build();
	}

}
