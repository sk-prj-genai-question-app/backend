package com.rookies3.genaiquestionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

@EnableJpaAuditing
@SpringBootApplication
public class GenAiQuestionAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenAiQuestionAppApplication.class, args);
	}
}
