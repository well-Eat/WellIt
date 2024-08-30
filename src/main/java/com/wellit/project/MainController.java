package com.wellit.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	

  
	@GetMapping("member/login")
	public String login() {
		return "member/login";
	}
	
	@GetMapping("member/register")
	public String register() {
		return "member/register";
	}
	
	@GetMapping("member/mypage")
	public String mypage() {
		return "member/mypage";
	}
	
	@GetMapping("/enter_password")
	public String enterpassword() {
		return "member/enter_password";
	}
	
	@GetMapping("/update_profile")
	public String updateprofile() {
		return "member/update_profile";
	}
	
	@GetMapping("/order_info")
	public String orderinfo() {
		return "member/order_info";
	}
	
	@GetMapping("member/order_form")
	public String orderform() {
		return "member/order_form";
	}
	
	@GetMapping("/question_form")
	public String questionform() {
		return "member/question_form";
	}
	
	@GetMapping("member//board")
	@ResponseBody
	public String board() {
		return "member/board";
	}
	
	
}
