package com.example.springproject3;

import com.example.springproject3.customer.Customer;
import com.example.springproject3.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Random;

@SpringBootApplication
@RestController
public class Springproject3Application {

    public static void main(String[] args) {
        SpringApplication.run(Springproject3Application.class, args);
    }
    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            Faker faker = new Faker();
            Random random=new Random();
            Customer customer=new Customer(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                    random.nextInt(20,99)
            );

            customerRepository.save(customer);
        };
    }



}
