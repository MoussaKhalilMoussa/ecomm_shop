package com.ecom.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserDtlsRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserDtlsRepository userDtlsRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		UserDtls userDtls = userDtlsRepository.findById(userId).get();
		Product product = productRepository.findById(productId).get();

		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart = null;

		if(ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}
        return cartRepository.save(cart);
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		List<Cart> carts = cartRepository.findByUserId(userId);
		double totalOrderPrice = 0.0;
		List<Cart> updateCart = new ArrayList<Cart>();
		for (Cart cart : carts) {
			double totalPrice = (cart.getProduct().getDiscountPrice() * cart.getQuantity());
			cart.setTotalPrice(totalPrice);
			totalOrderPrice += totalPrice;
			cart.setTotalOrderPrice(totalOrderPrice);
			updateCart.add(cart);
		}
		return updateCart;
	}

	@Override
	public Integer getCountCart(Integer userId) {
        return cartRepository.countByUserId(userId);
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;
		if(sy.equalsIgnoreCase("de")){
			updateQuantity = cart.getQuantity() - 1;
			if(updateQuantity <= 0){
				cartRepository.deleteById(cid);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}

	}

}
