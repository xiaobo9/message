package com.thunisoft.dzjz.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author renxb
 * @version 4.0
 */
@SpringBootApplication
@ComponentScan(value = {"com.thunisoft.dzjz"})
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}
