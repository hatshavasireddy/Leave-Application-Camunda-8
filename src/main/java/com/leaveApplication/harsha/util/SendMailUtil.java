package com.leaveApplication.harsha.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailUtil {

	@Autowired
	private JavaMailSender mailSend;

	public boolean mailConnect(String to, String cc, String body, String subject) {
		boolean isSent = false;
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setSubject(subject);
			msg.setText(
					"Hi "
			+ body);
			msg.setTo(to);
			msg.setCc(cc);
			mailSend.send(msg);
			isSent = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSent;
	}
}
