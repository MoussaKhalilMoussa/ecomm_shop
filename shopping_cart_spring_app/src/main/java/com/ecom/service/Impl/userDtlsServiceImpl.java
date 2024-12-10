package com.ecom.service.Impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserDtlsRepository;
import com.ecom.service.UserDtlsService;
import com.ecom.util.AppConstant;

@Service
public class userDtlsServiceImpl implements UserDtlsService {

	@Autowired
	UserDtlsRepository userDtlsRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnabled(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		UserDtls savedUser = userDtlsRepository.save(user);
		return savedUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		return userDtlsRepository.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userDtlsRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDtls> foundUserByid = userDtlsRepository.findById(id);

		if (foundUserByid.isPresent()) {
			UserDtls user = foundUserByid.get();
			user.setIsEnabled(status);
			userDtlsRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attemp = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attemp);
		userDtlsRepository.save(user);

	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userDtlsRepository.save(user);

	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();

		if (unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userDtlsRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void resetAttempt(int userId) {

	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls userByEmail = userDtlsRepository.findByEmail(email);
		userByEmail.setResetToken(resetToken);
		userDtlsRepository.save(userByEmail);
	}

	@Override
	public UserDtls getUserByToken(String token) {
		return userDtlsRepository.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userDtlsRepository.save(user);
	}

}
