package com.phegondev.usersmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.phegondev.usersmanagementsystem")
public class LyceeSaintAlexandre {

	public static void main(String[] args) {
		SpringApplication.run(LyceeSaintAlexandre.class, args);
	}

}
