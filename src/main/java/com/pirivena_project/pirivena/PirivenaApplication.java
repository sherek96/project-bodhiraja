package com.pirivena_project.pirivena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
public class PirivenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PirivenaApplication.class, args);
	}

}
