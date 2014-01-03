package com.in1.test;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.in1.boot.IntegrationsImcApplication;
import com.in1.controller.MockController;
import com.in1.model.TimeZone;
import com.in1.repository.TimeZoneRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = IntegrationsImcApplication.class)
@ActiveProfiles({"test"})
public class AnnotationTest {

	@Autowired
	private TimeZoneRepository timeZoneRepository;
	
	@Autowired MockController mockController;
	
	@Before
	public void setAuthentication(){	
		GrantedAuthority mockRole = new SimpleGrantedAuthority("NONE");
		Sid mockSid = new PrincipalSid("somebody");
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockSid,"",Arrays.asList(mockRole)));	
	}
	
	@Test(expected=AccessDeniedException.class)
	public void testRepositoryPrePost(){
		timeZoneRepository.findOne(1);		
	}
	
	@Test(expected=AccessDeniedException.class)
	public void testControllerPrePost(){
		mockController.doSomething();	
	}
	
	@Test
	public void testRepositoryTransaction(){
		
		try {
			createAndRollbackRepository();
		} catch (Exception e) {}
		
		List<TimeZone> tz = timeZoneRepository.findById(1);	
		
		Assert.isTrue(tz==null || tz.size()==0, "List should be null or empty");
		
		
	}	
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void testActiveRecordTransaction(){
		
		try {
			createAndRollbackActiveRecord();
		} catch (Exception e) {}
		
		TimeZone.findTimeZoneById(1);
	}
	
	
	@Transactional(rollbackFor=Exception.class)
	private void createAndRollbackActiveRecord() throws Exception{
		
		TimeZone tz = new TimeZone();
		tz.persist();
		
		throw new Exception();
		
	}
	
	
	@Transactional(rollbackFor=Exception.class)
	private void createAndRollbackRepository() throws Exception{
		
		TimeZone tz = new TimeZone();
		timeZoneRepository.save(tz);
		
		throw new Exception();
		
	}
	
	

	@After
	public void clearAuthenticatio(){
		SecurityContextHolder.clearContext();
	}
	
	
}
