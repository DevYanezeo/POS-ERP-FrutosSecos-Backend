package com.erp.p03;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class P03Application {

	public static void main(String[] args) {
		SpringApplication.run(P03Application.class, args);
	}

	@PostConstruct
	public void init() {
		// Set JVM timezone to Chile
		TimeZone.setDefault(TimeZone.getTimeZone("America/Santiago"));
		System.out.println("Timezone set to America/Santiago");
	}

}
