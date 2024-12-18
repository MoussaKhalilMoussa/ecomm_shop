package com.ecom.service.Impl;

import com.ecom.model.*;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.UserDtlsRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);
        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = getOrderAddress(orderRequest);
            order.setOrderAddress(address);
            ProductOrder savedOrder = orderRepository.save(order);

            commonUtil.sendMailForProductOrder(savedOrder,"Success");
        }
    }

    private static OrderAddress getOrderAddress(OrderRequest orderRequest) {
        OrderAddress address = new OrderAddress();
        address.setFirstName(orderRequest.getFirstName());
        address.setLastName(orderRequest.getLastName());
        address.setEmail(orderRequest.getEmail());
        address.setMobileNumber(orderRequest.getMobileNumber());
        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());
        address.setState(orderRequest.getState());
        address.setPinCode(orderRequest.getPinCode());
        return address;
    }

    @Override
    public List<ProductOrder> getOrderByUser(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer orderId, String status) {
        Optional<ProductOrder> orderById = orderRepository.findById(orderId);
        if (orderById.isPresent()) {
            ProductOrder productOrder = orderById.get();
            productOrder.setStatus(status);
            return orderRepository.save(productOrder);
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

}
