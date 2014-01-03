package com.in1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cltr")
public class MockController {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void doSomething(){
		
		
	}
	
}
