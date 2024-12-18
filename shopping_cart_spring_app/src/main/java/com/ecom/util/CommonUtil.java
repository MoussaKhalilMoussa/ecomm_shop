package com.ecom.util;

import java.io.UnsupportedEncodingException;

import com.ecom.model.ProductOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public Boolean sendMail(String url, String recipientEmail) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("moussa.khalil.92@gmail.com","Shoping Cart");
		helper.setTo(recipientEmail);

		String content = "<p>Hello,</p>" + "<p>You have a request to reset to your password.</p>"
				+ "<p> Click the link below to change your password:</p>" + "<p> <a href=\"" + url
				+ "\"> Change my password </a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {
		// http://localhost:8080/forgot_password
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}

	String msg = null;

	public Boolean sendMailForProductOrder(ProductOrder order , String status) throws Exception {

		msg = "<p>Hello [[name]],</p>"+
				"<br><p> Thank you <b>[[orderStatus]]</b>.</p>"
				+"<p><b> Products Details: </b></p>"
				+"<p>Name:[[productName]] </p>"
				+"<p>Category:[[category]]</p>"
				+"<p>Quantity: [[quantity]]</p>"
				+"<p>Price: [[price]]</p>"
				+"<p>Payment Type: [[paymentType]]</p>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("moussa.khalil.92@gmail.com","Shopping Cart");
		helper.setTo(order.getOrderAddress().getEmail());

		msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName());
		msg = msg.replace("[[orderStatus]]", status);
		msg = msg.replace("[[productName]]", order.getProduct().getTitle());
		msg = msg.replace("[[category]]", order.getProduct().getCategory());
		msg = msg.replace("[[quantity]]",order.getQuantity().toString());
		msg = msg.replace("[[price]]",order.getPrice().toString());
		msg = msg.replace("[[paymentType]]",order.getPaymentType());

		helper.setSubject("Product Order Status");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}
}
