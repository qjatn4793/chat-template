package com.chat.template.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.template.mapper.MainMapper;
import com.chat.template.vo.MainVo;

@Service
public class MainService {

	@Autowired
	MainMapper mainMapper;
	
	public List<MainVo> getUser() {
		
		return mainMapper.getUser();
	}
}
