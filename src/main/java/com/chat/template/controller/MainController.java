package com.chat.template.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.chat.template.service.MainService;

@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	MainService mainService;
	
	@GetMapping("/")
	public String main(Model model) {
		
		logger.info("호출 : {}", "test");
		
		model.addAttribute(mainService.getUser());
		
		return "/index.html";
	}
}