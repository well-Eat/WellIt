package com.study.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "현재 페이지에 문구 출력";
	}
}
