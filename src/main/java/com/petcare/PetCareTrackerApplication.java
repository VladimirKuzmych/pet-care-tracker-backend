package com.petcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetCareTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetCareTrackerApplication.class, args);
    }
}
