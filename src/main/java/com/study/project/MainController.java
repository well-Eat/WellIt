package com.study.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	

	@GetMapping("load/map")
	public String map() {
		return "/load/map";
	}
  
	
	
	
}
