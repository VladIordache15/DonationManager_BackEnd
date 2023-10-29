package demo.msg.javatraining.donationmanager.persistence.model.emailRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String destination;
    private String message;
    private String subject;

}