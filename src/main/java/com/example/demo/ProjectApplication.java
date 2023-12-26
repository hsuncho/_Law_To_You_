package com.example.demo;

import com.example.demo.faq.entity.FAQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
public class ProjectApplication {



	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
