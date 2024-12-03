package com.sharuk.gava_connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = "com.sharuk.gava_connect")
public class GavaConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GavaConnectApplication.class, args);
	}

}
