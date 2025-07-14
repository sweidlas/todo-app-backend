package com.example.graphqlserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphqlserverApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlserverApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// ...run on programm start

	}

}
