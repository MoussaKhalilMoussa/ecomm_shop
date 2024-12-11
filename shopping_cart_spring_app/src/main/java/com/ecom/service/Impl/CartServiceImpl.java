package com.ecom.service.Impl;

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
		Cart savedCart = cartRepository.save(cart);
		return savedCart;
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		return null;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId;
	}

}
