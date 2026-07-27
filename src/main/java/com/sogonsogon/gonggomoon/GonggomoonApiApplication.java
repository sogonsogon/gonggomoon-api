package com.sogonsogon.gonggomoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GonggomoonApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GonggomoonApiApplication.class, args);
    }

}
