package com.rookies3.genaiquestionapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
class GenAiQuestionAppApplicationTests {

	@Container
	static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.6");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mariaDBContainer::getUsername);
		registry.add("spring.datasource.password", mariaDBContainer::getPassword);
	}

	@Test
	void contextLoads() {
	}

}
