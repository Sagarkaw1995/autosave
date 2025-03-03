package com.cctns.autosave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AutosaveApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutosaveApplication.class, args);
	}
}
