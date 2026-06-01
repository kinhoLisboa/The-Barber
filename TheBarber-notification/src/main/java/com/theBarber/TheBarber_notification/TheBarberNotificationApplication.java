package com.theBarber.TheBarber_notification;

import com.theBarber.TheBarber_notification.config.ZapiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.theBarber.TheBarber_notification.client")
@EnableConfigurationProperties(ZapiProperties.class)
@EnableScheduling
public class TheBarberNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheBarberNotificationApplication.class, args);
	}

}
