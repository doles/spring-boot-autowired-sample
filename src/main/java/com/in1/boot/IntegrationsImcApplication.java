package com.in1.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ErrorMvcAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@EnableAspectJAutoProxy //equivalent of <context:aspectj-autoproxy/>
@EnableSpringConfigured
@EnableAutoConfiguration(exclude = {
	    DataSourceTransactionManagerAutoConfiguration.class,
	    HibernateJpaAutoConfiguration.class,
	    JpaRepositoriesAutoConfiguration.class,
	    SecurityAutoConfiguration.class,
	    ThymeleafAutoConfiguration.class,
	    ErrorMvcAutoConfiguration.class,
	    MessageSourceAutoConfiguration.class,
	    WebSocketAutoConfiguration.class
	})
	@Configuration
	@ComponentScan
public class IntegrationsImcApplication {
	
	public static final int ROOT_DATA_SOURCE_CONFIG_ORDER = Integer.MAX_VALUE - 60;
    public static final int ROOT_METHOD_SECURITY_CONFIG_ORDER = Integer.MAX_VALUE - 50;
    public static final int ROOT_MVC_CONFIG_ORDER = Integer.MAX_VALUE - 40;
    public static final int ROOT_SECURITY_CONFIG_ORDER = Integer.MAX_VALUE - 30;
    
    
	public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(IntegrationsImcApplication.class, args);
    }
}