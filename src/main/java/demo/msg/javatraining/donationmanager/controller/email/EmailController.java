package demo.msg.javatraining.donationmanager.controller.email;

import demo.msg.javatraining.donationmanager.persistence.model.emailRequest.EmailRequest;
import demo.msg.javatraining.donationmanager.service.emailService.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public void sendMail(@RequestBody EmailRequest request){
        emailService.sendSimpleMessage(request);
    }
}