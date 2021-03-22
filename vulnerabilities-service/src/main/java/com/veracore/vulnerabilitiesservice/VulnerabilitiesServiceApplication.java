package com.veracore.vulnerabilitiesservice;

import com.veracore.vulnerabilitiesservice.dao.VulnerabilityRepository;
import com.veracore.vulnerabilitiesservice.models.Vulnerability;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class VulnerabilitiesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VulnerabilitiesServiceApplication.class, args);
	}

	@Bean
	ApplicationRunner init(VulnerabilityRepository repository) {

		String[][] data = {
				{"Severe", "mvn", "com.fasterxml.jackson.core:jackson-databind", "2.9.9", "Upgrade com.fasterxml.jackson.core:jackson-databind to version 2.9.10.7 or later"}
		};

		return args -> {
			Stream.of(data).forEach(array -> {
				try {
					Vulnerability user = new Vulnerability(
							array[0],
							array[1],
							array[2],
							array[3],
							array[4]
					);
					repository.save(user);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			repository.findAll().forEach(System.out::println);
		};
	}
}
