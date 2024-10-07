package com.RentEase.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
        public void sendEmailWithAttachment(String to, String subject , String text,  String link , String pdfPath) throws MessagingException {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper message = new MimeMessageHelper(mimeMessage , true);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text + "\n\nLink:" + link, true);

            if(pdfPath != null && !pdfPath.isEmpty()){
                FileSystemResource file = new FileSystemResource(new File(pdfPath));
                message.addAttachment("Booking.pdf", file);
            }

            javaMailSender.send(mimeMessage);
        }


}
