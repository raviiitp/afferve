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


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.web.DisconnectController;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import com.shoptell.security.social.SecurityUtilsUserIdSource;
import com.shoptell.signin.ImplicitSignInAdapter;
import com.shoptell.social.repository.SocialRepository;
import com.shoptell.social.repository.SocialUsersConnectionRepository;

/**
 * Basic Spring Social configuration.  Creates the beans necessary to manage Connections to social services and
 * link accounts from those services to internal Users.
 */
@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {
	
    private final Logger log = LoggerFactory.getLogger(SocialConfiguration.class);

    @Inject
    private Environment env;
    
    @Inject
    private ConnectionSignUp signup;
    
    @Inject
	private SocialRepository socialRepository;
    
    private CharSequence password;
    private CharSequence salt;
    
    @PostConstruct
    public void init(){
    	password = env.getProperty("Encryptors.password");
    	salt = env.getProperty("Encryptors.salt");
    }
    
    /* (non-Javadoc)
     * @see org.springframework.social.config.annotation.SocialConfigurer#addConnectionFactories(org.springframework.social.config.annotation.ConnectionFactoryConfigurer, org.springframework.core.env.Environment)
     */
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        // google configuration
        String googleClientId = environment.getProperty("google.client.id");
        String googleClientSecret = environment.getProperty("google.client.secret");
        if (googleClientId != null && googleClientSecret != null) {
            log.debug("Configuring GoogleConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                    new GoogleConnectionFactory(
                            googleClientId,
                            googleClientSecret
                    )
            );
        }

        // facebook configuration
        String facebookClientId = environment.getProperty("facebook.app.id");
        String facebookClientSecret = environment.getProperty("facebook.app.secret");
        if (facebookClientId != null && facebookClientSecret != null) {
            log.debug("Configuring FacebookConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                    new FacebookConnectionFactory(
                            facebookClientId,
                            facebookClientSecret
                    )
            );
        }
        
     // twitter configuration; right now there is no sign in with Twitter cause Twitter won't give email address
/*        String twitterClientId = environment.getProperty("twitter.app.id");
        String twitterClientSecret = environment.getProperty("twitter.app.secret");
        if (twitterClientId != null && twitterClientSecret != null) {
            log.debug("Configuring TwitterConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                    new TwitterConnectionFactory(
                    		twitterClientId,
                    		twitterClientSecret
                    )
            );
        }*/
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new SecurityUtilsUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        // TODO: should this be converted to JdbcUsersConnectionRepository?  Doing so would allow a user to return to
        // any node in a cluster after the redirect back from an external OAuth2 authentication.  However, it would break support
        // for users that opt for a NoSQL store rather than traditional RDBMS.
        
		SocialUsersConnectionRepository repo = new SocialUsersConnectionRepository(connectionFactoryLocator, Encryptors.queryableText(password, salt), socialRepository);
        //TODO Change Encryptors
        
        // register our ConnectionSignUp so that UsersConnectionRepository can resolve external account ids
        // to internal Users
        repo.setConnectionSignUp(signup);
        return repo;
    }
    


	@Bean
	public SignInAdapter signInAdapter() {
		return new ImplicitSignInAdapter(new HttpSessionRequestCache());
	}
	
  /**
  * This bean manages the connection flow between the account provider and
  * the example application.
  */
	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		//connectController.addInterceptor(new PostToWallAfterConnectInterceptor());
		//connectController.addInterceptor(new TweetAfterConnectInterceptor());
		return connectController;
	}
	
	@Bean
	public DisconnectController disconnectController(UsersConnectionRepository usersConnectionRepository, Environment environment) {
		return new DisconnectController(usersConnectionRepository, environment.getProperty("facebook.app.secret"));
	}

	@Bean
	public ReconnectFilter apiExceptionHandler(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		return new ReconnectFilter(usersConnectionRepository, userIdSource);
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Twitter twitter(ConnectionRepository repository) {
		Connection<Twitter> connection = repository.findPrimaryConnection(Twitter.class);
		return connection != null ? connection.getApi() : null;
	}
	
	/**
	 * A proxy to a request-scoped object representing the current user's primary google account.
	 * @throws NotConnectedException if the user is not connected to google.
	 */
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Google google(ConnectionRepository repository) {
		Connection<Google> connection = repository.findPrimaryConnection(Google.class);
		return connection != null ? connection.getApi() : null;
	}

	
}