package com.ks.iota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ks.controller.IotaController;

@SpringBootApplication
@ComponentScan(basePackageClasses = IotaController.class)
public class IotaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotaApplication.class, args);
	}
}
