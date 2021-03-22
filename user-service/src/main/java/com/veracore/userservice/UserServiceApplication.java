package com.veracore.userservice;

import com.veracore.userservice.dao.UserRepository;
import com.veracore.userservice.models.PaidUser;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	ApplicationRunner init(UserRepository userRepository) {

		String[][] data = {
				{"ahmed", "ahmed@email.com"},
				{"sam", "sam@email.com"},
				{"doe", "doe@email.com"}
		};

		return args -> {
			Stream.of(data).forEach(array -> {
				try {
					PaidUser user = new PaidUser(
							array[0],
							array[1]
					);
					userRepository.save(user);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			userRepository.findAll().forEach(System.out::println);
		};
	}

}
