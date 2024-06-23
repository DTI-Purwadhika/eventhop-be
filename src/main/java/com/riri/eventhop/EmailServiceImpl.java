//package com.riri.eventhop.users.service.impl;
//
//import com.riri.eventhop.users.service.EmailService;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailServiceImpl implements EmailService {
//    private JavaMailSender javaMailSender;
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(to);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(body);
//        javaMailSender.send(mailMessage);
//    }
//}
