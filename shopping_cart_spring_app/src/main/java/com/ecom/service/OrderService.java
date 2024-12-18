package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;
    public List<ProductOrder> getOrderByUser(Integer userId);
    public ProductOrder updateOrderStatus(Integer orderId, String status);
    public List<ProductOrder> getAllOrders();
}
