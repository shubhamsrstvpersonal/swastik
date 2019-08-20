package com.swastik.sales.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableAutoConfiguration
@CrossOrigin
public class MainClass {

    public static void main(String[] args){
        SpringApplication.run(MainClass.class, args);
    }
}
