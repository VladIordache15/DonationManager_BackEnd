package de.msg.javatraining.donationmanager.service.emailService;

import de.msg.javatraining.donationmanager.persistence.model.emailRequest.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(EmailRequest request) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("savu.sergiu.g@gmail.com");
        simpleMailMessage.setTo(request.getDestination());
        simpleMailMessage.setSubject(request.getSubject());
        simpleMailMessage.setText(request.getMessage());
        emailSender.send(simpleMailMessage);
    }
}