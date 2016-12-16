/***************************************************************************
 * Copyright (c) 2015 AFFERVE.COM - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * 
 * Contributors:
 *     Abhishek Agarwal 	- abhishek@afferve.com
 *     Ravi Kumar 			- ravi@afferve.com
 ****************************************************************************/
package com.shoptell.config.cassandra;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;

/**
 * Configuration properties for Cassandra.
 */
@ConfigurationProperties(prefix = "spring.data.cassandra")
public class CassandraProperties {

    private static final Log log = LogFactory.getLog(CassandraProperties.class);

    @Inject
    private Environment env;
    
    /**
     * Name of the Cassandra cluster.
     */
    private String clusterName = "Test Cluster";

    private int port = ProtocolOptions.DEFAULT_PORT;

    /**
     * Comma-separated list of cluster node addresses.
     */
    private String contactPoints = "localhost";

    /**
     * Protocol version supported by the Cassandra binary protocol: can be V1, V2, V3.
     */
    private String protocolVersion;

    /**
     * Compression supported by the Cassandra binary protocol: can be NONE, SNAPPY, LZ4.
     */
    private String compression = ProtocolOptions.Compression.NONE.name();

    /**
     * Class name of the load balancing policy.
     */
    private String loadBalancingPolicy;

    /**
     * Queries consistency level.
     */
    private String consistency;

    /**
     * Queries serial consistency level.
     */
    private String serialConsistency;

    /**
     * Queries default fetch size.
     */
    private int fetchSize = QueryOptions.DEFAULT_FETCH_SIZE;

    /**
     * Class name of the reconnection policy.
     */
    private String reconnectionPolicy;

    /**
     * Class name of the retry policy.
     */
    private String retryPolicy;

    /**
     * Socket option: connection time out.
     */
    private int connectTimeoutMillis = SocketOptions.DEFAULT_CONNECT_TIMEOUT_MILLIS;

    /**
     * Socket option: read time out.
     */
    private int readTimeoutMillis = 60000;//SocketOptions.DEFAULT_READ_TIMEOUT_MILLIS; Changed to 60 seconds

    /**
     * Enable SSL support.
     */
    private boolean ssl = true;

    /**
     * Keyspace name to use.
     */
    private String keyspaceName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(String contactPoints) {
        this.contactPoints = contactPoints;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getLoadBalancingPolicy() {
        return loadBalancingPolicy;
    }

    public void setLoadBalancingPolicy(String loadBalancingPolicy) {
        this.loadBalancingPolicy = loadBalancingPolicy;
    }

    public String getConsistency() {
        return consistency;
    }

    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }

    public String getSerialConsistency() {
        return serialConsistency;
    }

    public void setSerialConsistency(String serialConsistency) {
        this.serialConsistency = serialConsistency;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getReconnectionPolicy() {
        return reconnectionPolicy;
    }

    public void setReconnectionPolicy(String reconnectionPolicy) {
        this.reconnectionPolicy = reconnectionPolicy;
    }

    public String getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(String retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public Cluster createCluster() {
        Cluster.Builder builder = Cluster.builder()
                .withClusterName(this.getClusterName())
                .withPort(this.getPort())
                .withCredentials(env.getProperty("cqlshUserName"), env.getProperty("cqlshPassword"));

        if (ProtocolVersion.V1.name().equals(this.getProtocolVersion())) {
            builder.withProtocolVersion(ProtocolVersion.V1);
        } else if  (ProtocolVersion.V2.name().equals(this.getProtocolVersion())) {
            builder.withProtocolVersion(ProtocolVersion.V2);
        } else if  (ProtocolVersion.V3.name().equals(this.getProtocolVersion())) {
            builder.withProtocolVersion(ProtocolVersion.V3);
        }

        // Manage compression protocol
        if (ProtocolOptions.Compression.SNAPPY.equals(this.getCompression())) {
            builder.withCompression(ProtocolOptions.Compression.SNAPPY);
        } else if (ProtocolOptions.Compression.LZ4.equals(this.getCompression())) {
            builder.withCompression(ProtocolOptions.Compression.LZ4);
        } else {
            builder.withCompression(ProtocolOptions.Compression.NONE);
        }

        // Manage the load balancing policy
        if (!StringUtils.isEmpty(this.getLoadBalancingPolicy())) {
            try {
                Class<?> loadBalancingPolicyClass = ClassUtils.forName(this.getLoadBalancingPolicy(), null);
                Object loadBalancingPolicyInstance = loadBalancingPolicyClass.newInstance();
                LoadBalancingPolicy userLoadBalancingPolicy = (LoadBalancingPolicy) loadBalancingPolicyInstance;
                builder.withLoadBalancingPolicy(userLoadBalancingPolicy);
            } catch (ClassNotFoundException e) {
                log.warn("The load balancing policy could not be loaded, falling back to the default policy", e);
            } catch (InstantiationException e) {
                log.warn("The load balancing policy could not be instanced, falling back to the default policy", e);
            } catch (IllegalAccessException e) {
                log.warn("The load balancing policy could not be created, falling back to the default policy", e);
            } catch (ClassCastException e) {
                log.warn("The load balancing policy does not implement the correct interface, falling back to the default policy", e);
            }
        }

        // Manage query options
        QueryOptions queryOptions = new QueryOptions();
        if (this.getConsistency() != null) {
            ConsistencyLevel consistencyLevel = ConsistencyLevel.valueOf(this.getConsistency());
            queryOptions.setConsistencyLevel(consistencyLevel);
        }
        if (this.getSerialConsistency() != null) {
            ConsistencyLevel serialConsistencyLevel = ConsistencyLevel.valueOf(this.getSerialConsistency());
            queryOptions.setSerialConsistencyLevel(serialConsistencyLevel);
        }
        queryOptions.setFetchSize(this.getFetchSize());
        builder.withQueryOptions(queryOptions);

        // Manage the reconnection policy
        if (!StringUtils.isEmpty(this.getReconnectionPolicy())) {
            try {
                Class<?> reconnectionPolicyClass = ClassUtils.forName(this.getReconnectionPolicy(), null);
                Object reconnectionPolicyInstance = reconnectionPolicyClass.newInstance();
                ReconnectionPolicy userReconnectionPolicy = (ReconnectionPolicy) reconnectionPolicyInstance;
                builder.withReconnectionPolicy(userReconnectionPolicy);
            } catch (ClassNotFoundException e) {
                log.warn("The reconnection policy could not be loaded, falling back to the default policy", e);
            } catch (InstantiationException e) {
                log.warn("The reconnection policy could not be instanced, falling back to the default policy", e);
            } catch (IllegalAccessException e) {
                log.warn("The reconnection policy could not be created, falling back to the default policy", e);
            } catch (ClassCastException e) {
                log.warn("The reconnection policy does not implement the correct interface, falling back to the default policy", e);
            }
        }

        // Manage the retry policy
        if (!StringUtils.isEmpty(this.getRetryPolicy())) {
            try {
                Class<?> retryPolicyClass = ClassUtils.forName(this.getRetryPolicy(), null);
                Object retryPolicyInstance = retryPolicyClass.newInstance();
                RetryPolicy userRetryPolicy = (RetryPolicy) retryPolicyInstance;
                builder.withRetryPolicy(userRetryPolicy);
            } catch (ClassNotFoundException e) {
                log.warn("The retry policy could not be loaded, falling back to the default policy", e);
            } catch (InstantiationException e) {
                log.warn("The retry policy could not be instanced, falling back to the default policy", e);
            } catch (IllegalAccessException e) {
                log.warn("The retry policy could not be created, falling back to the default policy", e);
            } catch (ClassCastException e) {
                log.warn("The retry policy does not implement the correct interface, falling back to the default policy", e);
            }
        }

        // Manage socket options
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(this.getConnectTimeoutMillis());
        socketOptions.setReadTimeoutMillis(this.getReadTimeoutMillis());
        builder.withSocketOptions(socketOptions);

        /*String truststorePath = env.getProperty("truststorePath");
        String truststorePassword = env.getProperty("truststorePassword");
        String keystorePath = env.getProperty("keystorePath");
        String keystorePassword = env.getProperty("keystorePassword");

        SSLContext context = null;
		try {
			context = getSSLContext(truststorePath, 
			        truststorePassword, 
			        keystorePath, 
			        keystorePassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        String[] cipherSuites = { "TLS_RSA_WITH_AES_128_CBC_SHA", 
        "TLS_RSA_WITH_AES_256_CBC_SHA" };
        
        // Manage SSL
        if (this.isSsl()) {
            builder.withSSL(new SSLOptions(context, cipherSuites));
        }*/

        // Manage the contact points
        builder.addContactPoints(StringUtils.commaDelimitedListToStringArray(this.getContactPoints()));

        return builder.build();
    }
    
	@SuppressWarnings("unused")
	private static SSLContext getSSLContext(String truststorePath, String truststorePassword, String keystorePath, String keystorePassword) throws Exception {
		FileInputStream tsf = new FileInputStream(truststorePath);
		FileInputStream ksf = new FileInputStream(keystorePath);
		SSLContext ctx = SSLContext.getInstance("SSL");

		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(tsf, truststorePassword.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ts);

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(ksf, keystorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keystorePassword.toCharArray());

		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		return ctx;
	}
}
