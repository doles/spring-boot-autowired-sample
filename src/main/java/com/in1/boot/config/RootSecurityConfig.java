package com.in1.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.in1.boot.IntegrationsImcApplication;
import com.in1.boot.security.ActuatorWebSecurityConfig;
import com.in1.boot.security.WebSecurityConfig;


@EnableWebSecurity(debug = false)
@Configuration
public class RootSecurityConfig implements Ordered {

	@Override
	public int getOrder() {		
		return IntegrationsImcApplication.ROOT_SECURITY_CONFIG_ORDER;
	}
    
    @Bean
	public WebSecurityConfigurerAdapter aclWebSecurityConfigurerAdapter(){
		return new WebSecurityConfig(false);
	}

	@Bean
	public WebSecurityConfigurerAdapter actuatorWebSecurityConfigurerAdapter() {
		return new ActuatorWebSecurityConfig(true); 
	}
	
}