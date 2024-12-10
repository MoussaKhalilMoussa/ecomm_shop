package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserDtlsService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserDtlsService userDtlsService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

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
	public String index() {
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(@RequestParam(value = "category", defaultValue = "") String category, Model model) {
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProducts(category);

		model.addAttribute("categories", categories);
		model.addAttribute("products", products);
		model.addAttribute("paramValue", category);
		return "product";
	}

	@GetMapping("/view_product_details/{id}")
	public String product(@PathVariable("id") int id, Model model) {

		Product productById = productService.getProductById(id);
		model.addAttribute("product", productById);

		return "view_product_details";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {
		String fileImage = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(fileImage);
		UserDtls saveUser = userDtlsService.saveUser(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Register Successfully");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}

		return "redirect:/register";
	}

	@GetMapping("/forgot_password")
	public String forgotPassword() {
		return "forgot_password";
	}

	@PostMapping("/forgot_password")
	public String processForgotPassword(@RequestParam("email") String email, HttpSession session,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		UserDtls userByEmail = userDtlsService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {

			session.setAttribute("errorMsg", "Invalid email !");
		} else {
			String resetToken = UUID.randomUUID().toString();
			userDtlsService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset_password?token=jdklhvljvjlvkvkjkjd
			String url = CommonUtil.generateUrl(request) + "/reset_password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email...Password reset link sent");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server! Email not sent");
			}
		}
		return "redirect:/forgot_password";
	}

	@GetMapping("/reset_password")
	public String showResetPassword(@RequestParam("token") String token, HttpSession session, Model model) {
		UserDtls userByToken = userDtlsService.getUserByToken(token);

		if (userByToken == null) {
			// session.setAttribute("errorMsg", "Your link is invalid or expired");
			model.addAttribute("msg", "Your link is invalid or expired !!");
			return "message";
		}
		model.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset_password")
	public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String password,
			HttpSession session, Model model) {
		UserDtls userByToken = userDtlsService.getUserByToken(token);

		if (userByToken == null) {
			model.addAttribute("msg", "Your link is invalid or expired !!");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userDtlsService.updateUser(userByToken);
			// session.setAttribute("succMsg", "Password changed successfully.");
			model.addAttribute("msg", "Password changed successfully!!");
			return "message";
		}
	}
}
