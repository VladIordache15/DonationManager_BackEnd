package de.msg.javatraining.donationmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling

public class DonationmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonationmanagerApplication.class, args);
	}

}
