package com.summat.summat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SummatApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummatApplication.class, args);
	}

}
