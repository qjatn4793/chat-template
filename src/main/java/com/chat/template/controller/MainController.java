package com.chat.template.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.template.service.MainService;
import com.chat.template.vo.MainVo;

@RestController
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	MainService mainService;
	
	@PostMapping("/chat")
	public List<MainVo> main() {
		
		logger.info("호출 : {}", "test");
		
		return mainService.getUser();
	}
}