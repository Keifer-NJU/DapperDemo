package com.ae86.mypractice01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Keifer
 */
@SpringBootApplication
@ComponentScan(value = {"com.ae86.mypracticeaop", "com.ae86.mypractice01"})
public class MyPractice01Application {

    public static void main(String[] args) {
        SpringApplication.run(MyPractice01Application.class, args);
    }

}
