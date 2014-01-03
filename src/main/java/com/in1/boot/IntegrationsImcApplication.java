package com.in1.boot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableSpringConfigured
@EnableJpaRepositories
@EnableAutoConfiguration(exclude = {
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class, 
		SecurityAutoConfiguration.class,
		ThymeleafAutoConfiguration.class,
		ErrorMvcAutoConfiguration.class, 
		MessageSourceAutoConfiguration.class,
		WebSocketAutoConfiguration.class })
@Configuration
@ComponentScan(basePackages={"com.in1"})
public class IntegrationsImcApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(IntegrationsImcApplication.class, args);
	}
}