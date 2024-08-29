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
	public String login() {
		return "/load/map";
	}
	
  @GetMapping("load/place")
	public String login() {
		return "/load/place";
	}
  
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/mypage")
	public String mypage() {
		return "mypage";
	}
	
	@GetMapping("/enter_password")
	public String enterpassword() {
		return "enter_password";
	}
	
	@GetMapping("/update_profile")
	public String updateprofile() {
		return "update_profile";
	}
	
	@GetMapping("/order_info")
	public String orderinfo() {
		return "order_info";
	}
	
	@GetMapping("/order_form")
	public String orderform() {
		return "order_form";
	}
	
	@GetMapping("/question_form")
	public String questionform() {
		return "question_form";
	}
	
	@GetMapping("/board")
	@ResponseBody
	public String board() {
		return "board";
	}
	
	
}
