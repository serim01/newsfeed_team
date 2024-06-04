package com.sparta.newspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NewspeedApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewspeedApplication.class, args);
	}

}
