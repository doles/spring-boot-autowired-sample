package com.in1.boot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;



@Order(Ordered.LOWEST_PRECEDENCE-1) //lowest of them all
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	AuthenticationProvider aclDaoAuthenticationProvider;
	
	@Autowired
	AnonymousAuthenticationProvider aclAnonymousAuthenticationProvider;

	public WebSecurityConfig(boolean disableDefaults){
		super(disableDefaults);
		
	}
	

	@Override
	public void configure(WebSecurity web) throws Exception {
		
		web
			.ignoring().antMatchers("/static/**").and()
			.ignoring().antMatchers("/web-inf/**/*.*").and()
			.ignoring().antMatchers("/login");
		
	}	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
	    SwitchUserFilter switchUserFilter = new SwitchUserFilter();
	    switchUserFilter.setUserDetailsService(userDetailsService);
	    switchUserFilter.setSwitchUserUrl("/static/j_spring_security_switch_user");
	    switchUserFilter.setExitUserUrl("/static/j_spring_security_exit_user");
	    switchUserFilter.setTargetUrl("/");
		
		http
			.addFilterAfter(switchUserFilter, FilterSecurityInterceptor.class)
			.csrf().disable()
			.authorizeRequests()
			    .antMatchers("/**").access("isAuthenticated()").and()
			.anonymous().and()
			.formLogin()
				.loginProcessingUrl("/static/j_spring_security_check")
				.loginPage("/login")
				.failureUrl("/login?login_error=t").and()
			.logout().logoutSuccessUrl("/login").logoutUrl("/static/j_spring_security_logout").permitAll().invalidateHttpSession(true);
	}
	
	//we dont overwrite it, hoping it will get it from RootMethodSecurityConfiguration.class,
	//which has already providers set 
	
	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth)
			throws Exception {
		
		//auth.userDetailsService(rootMethodSecurityConfiguration.userDetailsService());
		auth.authenticationProvider(aclDaoAuthenticationProvider);
		auth.authenticationProvider(aclAnonymousAuthenticationProvider);
	}
	
	
	
	
	
	
	
   

}