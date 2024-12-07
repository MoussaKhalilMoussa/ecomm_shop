package com.ecom.service;

import com.ecom.model.UserDtls;

public interface userDtlsService {

	public UserDtls saveUser(UserDtls user);

	public UserDtls getUserByEmail(String email);

}
