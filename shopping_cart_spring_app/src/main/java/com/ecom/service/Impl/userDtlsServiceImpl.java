package com.ecom.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserDtlsRepository;
import com.ecom.service.userDtlsService;

@Service
public class userDtlsServiceImpl implements userDtlsService {

	@Autowired
	UserDtlsRepository userDtlsRepository;

	@Override
	public UserDtls saveUser(UserDtls user) {
		return userDtlsRepository.save(user);
	}

}
