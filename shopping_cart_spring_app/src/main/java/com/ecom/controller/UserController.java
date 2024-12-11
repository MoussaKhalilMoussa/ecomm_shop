package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserDtlsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserDtlsService userDtlsService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {
		if (principal != null) {
			String email = principal.getName();
			UserDtls userDtls = userDtlsService.getUserByEmail(email);
			model.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			model.addAttribute("countCart", countCart);
		}

		List<Category> categories = categoryService.getAllActiveCategory();
		model.addAttribute("categories", categories);

	}
	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/addToCart")
	public String addToCart(@RequestParam("pid") Integer pid, @RequestParam("uid") Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed.");
		} else {
			session.setAttribute("succMsg", "Product added to cart.");
		}
		return "redirect:/view_product_details/" + pid;
	}

}
