package com.theBarber.TheBarber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.theBarber.TheBarber")
public class TheBarberApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheBarberApplication.class, args);
	}

}
