package com.shariarunix.refind;

import com.shariarunix.refind.config.DotenvLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RefindApplication {

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(RefindApplication.class, args);
	}

}
