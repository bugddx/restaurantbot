package com.restaurantbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class RestaurantbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantbotApplication.class, args);
	}

}
