package com.in1.boot.config;

import java.io.IOException;

import javax.sql.DataSource;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;


@Configuration
public class RootMethodSecurityConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
    private static String ACL_CACHE_NAME = "aclCache";

	
    @Bean(name="aclAnonymousAuthenticationProvider")
    public AnonymousAuthenticationProvider aclAnonymousAuthenticationProvider() {
        return new AnonymousAuthenticationProvider("in1anonumous");
    }
    
    
    @Bean(name="aclDaoAuthenticationProvider")
    public AuthenticationProvider aclDaoAuthenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService());
        return p;
    }

    @Bean
    public UserDetailsService userDetailsService() {
    	JdbcUserDetailsManager m = new JdbcUserDetailsManager();
    	m.setDataSource(dataSource);
    	return m;
    }	
	
	@Bean
	public MethodSecurityExpressionHandler aclExpressionHandler(){
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		try {
			expressionHandler.setPermissionEvaluator(aclPermissionEvaluator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		expressionHandler.setRoleHierarchy(aclRoleHierarchy());
		return expressionHandler;
	}

    
    //~~~~~~~~~~~~~~~ ACL CONFIG ~~~~~~~~~~~~~~~ //
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean(name = "cacheManager")
    public CacheManager ehCacheManager() throws IOException {
        //CacheManager manager = CacheManager.getCacheManager(CacheManager.DEFAULT_NAME);
        //if(manager!=null){
        //    return manager;
        //}
        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setCacheManagerName(CacheManager.DEFAULT_NAME);
        factory.setShared(true);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "ehcache")
    public Ehcache ehCacheFactory() throws CacheException, IOException {
        EhCacheFactoryBean factory = new EhCacheFactoryBean();
        factory.setCacheManager(ehCacheManager());
        factory.setCacheName(ACL_CACHE_NAME);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public PermissionGrantingStrategy aclPermissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(aclAuditLogger());
    }

    @Bean
    public AclCache aclCache() throws CacheException, IOException {
        return new EhCacheBasedAclCache(ehCacheFactory(), aclPermissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    @Bean
    public AuditLogger aclAuditLogger() {
        return new ConsoleAuditLogger();
    }

    @Bean
    public LookupStrategy aclLookupStrategy() throws CacheException, IOException {
        BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), aclPermissionGrantingStrategy());
        lookupStrategy.setPermissionFactory(aclPermissionFactory());
        return lookupStrategy;
    }

    @Bean//done
    public MutableAclService aclService() throws CacheException, IOException {
    	
    	JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, aclLookupStrategy(), aclCache());
        aclService.setClassIdentityQuery("SELECT @@IDENTITY");
        aclService.setSidIdentityQuery("SELECT @@IDENTITY");
        return aclService;
    }

    @Bean
    public PermissionEvaluator aclPermissionEvaluator() throws CacheException, IOException {
        return new AclPermissionEvaluator(aclService());
    }

    @Bean
    public DefaultPermissionFactory aclPermissionFactory() {
        return new DefaultPermissionFactory();
    }

    @Bean
    public RoleHierarchy aclRoleHierarchy() {
        RoleHierarchyImpl rh = new RoleHierarchyImpl();
        rh.setHierarchy("ROLE_ADMIN > ROLE_PROPERTY");
        return rh;
    }

    /**
     * This is a separate transaction manager for ACL
     *
     * @return
     */
    @Bean
    public DataSourceTransactionManager aclTransactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager(dataSource);
        return tm;
    }  

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Configuration
    protected static class ActualMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

        @Autowired
        @Qualifier("aclDaoAuthenticationProvider")
        private AuthenticationProvider aclDaoAuthenticationProvider;

        @Autowired
        @Qualifier("aclAnonymousAuthenticationProvider")
        private AnonymousAuthenticationProvider aclAnonymousAuthenticationProvider;

        @Autowired
        @Qualifier("aclExpressionHandler")
        private MethodSecurityExpressionHandler aclExpressionHandler;

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.authenticationProvider(aclDaoAuthenticationProvider);
            auth.authenticationProvider(aclAnonymousAuthenticationProvider);
        }

        @Override
        public MethodSecurityExpressionHandler createExpressionHandler() {
            return aclExpressionHandler;
        }
    }

}