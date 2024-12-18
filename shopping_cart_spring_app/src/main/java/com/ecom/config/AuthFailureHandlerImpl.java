package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserDtlsRepository;
import com.ecom.service.UserDtlsService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserDtlsRepository userDtlsRepository;

	@Autowired
	private UserDtlsService userDtlsService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String email = request.getParameter("username");
		UserDtls userDtls = userDtlsRepository.findByEmail(email);

		if(userDtls != null) {
			if (userDtls.getIsEnabled()) {

				if (userDtls.getAccountNonLocked()) {

					if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {

						userDtlsService.increaseFailedAttempt(userDtls);
					} else {
						userDtlsService.userAccountLock(userDtls);
						exception = new LockedException("Your account is locked !! Failed attempt 3");
					}
				} else {

					if (userDtlsService.unlockAccountTimeExpired(userDtls)) {
						exception = new LockedException("Your account is unlocked ! Please try to login ");
					} else {
						exception = new LockedException("Your account is locked ! Please try later ");
					}
				}
			} else {
				exception = new LockedException("Your account is inactive");
			}
		} else {
			exception = new LockedException("Email & Password Invalid");
		}
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
