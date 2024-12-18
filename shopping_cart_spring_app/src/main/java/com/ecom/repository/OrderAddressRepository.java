package com.ecom.repository;

import com.ecom.model.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {

}
