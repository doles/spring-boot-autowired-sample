package com.in1.boot.security;


import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Order(Ordered.LOWEST_PRECEDENCE-2)
public class ActuatorWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	public ActuatorWebSecurityConfig(boolean disableDefaults){
		super(disableDefaults);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("xyz")
				.password("xyz").roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.antMatcher("/mgmt/**")//this is most important
		.headers().and() 
		.exceptionHandling().and()			
		//run creator to build NullSecurityContextRepository and NullRequestCache
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		//we need this, repository will be from session configuration
		.securityContext().and()
		//we need this
		.requestCache().and()
		.authorizeRequests().antMatchers("/mgmt/**").hasRole("ADMIN").and()
		.httpBasic().realmName("in1-imc").and();
	}	
	
}