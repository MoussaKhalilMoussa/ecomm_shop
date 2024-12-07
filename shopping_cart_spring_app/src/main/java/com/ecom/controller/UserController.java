package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.userDtlsService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	userDtlsService userDtlsService;

	@Autowired
	CategoryService categoryService;

	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {

		if (principal != null) {
			String email = principal.getName();
			UserDtls userDtls = userDtlsService.getUserByEmail(email);
			model.addAttribute("user", userDtls);
		}

		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);

	}
	@GetMapping("/")
	public String home() {
		return "user/home";
	}
}
