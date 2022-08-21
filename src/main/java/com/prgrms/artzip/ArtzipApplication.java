package com.prgrms.artzip;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ArtzipApplication {
	public static void main(String[] args) {
		SpringApplication.run(ArtzipApplication.class, args);
	}
}
