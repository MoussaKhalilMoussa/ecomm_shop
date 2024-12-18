package com.ecom.controller;

import com.ecom.model.*;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserDtlsService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDtlsService userDtlsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/cart")
    public String loadCardPage(Principal principal, Model model) {
        UserDtls user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        } else {
            double totalOrderPrice = 0.0;
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        return userDtlsService.getUserByEmail(email);
    }

    @GetMapping("/cartQuantityUpdate")
    public String cartUpdateQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String orderPage(Principal principal, Model model) {
        UserDtls user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (!carts.isEmpty()) {
            double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 259 + 100;
            model.addAttribute("totalOrderPrice", totalOrderPrice);
            model.addAttribute("orderPrice", orderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/save_order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal principal) throws Exception {
        UserDtls user = getLoggedInUserDetails(principal);
        orderService.saveOrder(user.getId(), request);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccessPage() {
        return "/user/success";
    }

    @GetMapping("/user_orders")
    public String myOrder(Principal principal, Model model) {
        UserDtls loggedInUser = getLoggedInUserDetails(principal);
        List<ProductOrder> orders = orderService.getOrderByUser(loggedInUser.getId());
        model.addAttribute("orders", orders);
        return "/user/my_orders";
    }

    @GetMapping("/update_status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        statusValues(id, st, session, orderService);
        return "redirect:/user/user_orders";
    }

    public void statusValues(@RequestParam Integer id, @RequestParam Integer st, HttpSession session, OrderService orderService) {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }
        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
        try {
            commonUtil.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Order status updated.");
        } else {
            session.setAttribute("errorMsg", "Order status not updated.");
        }
    }

    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }

    @PostMapping("/update_profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile image, HttpSession session) {
        UserDtls updatedUserProfile = userDtlsService.updateUserProfile(user, image);

        if (ObjectUtils.isEmpty(updatedUserProfile)) {
            session.setAttribute("errorMsg", "Profile update failed.");
        } else {
            session.setAttribute("succMsg", "Profile updated.");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword, Principal principal, HttpSession session) {
        UserDtls loggedInUser = getLoggedInUserDetails(principal);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUser.getPassword());
        if (matches) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            loggedInUser.setPassword(encodedPassword);
            UserDtls updatedUser = userDtlsService.updateUser(loggedInUser);
            if (ObjectUtils.isEmpty(updatedUser)) {
                session.setAttribute("errorMsg", "Password update failed !! Error in server");
            } else {
                session.setAttribute("succMsg", "Password updated successfully.");
            }
        } else {
            session.setAttribute("errorMsg", "Current Password Incorrect.");
        }
        return "redirect:/user/profile";
    }
}

