package com.example.springproject3;

import com.example.springproject3.customer.Customer;
import com.example.springproject3.customer.CustomerRepository;
import com.example.springproject3.customer.Gender;
import com.example.springproject3.s3.S3Buckets;
import com.example.springproject3.s3.S3Service;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;


import java.util.Random;


@SpringBootApplication
public class Springproject3Application {

    public static void main(String[] args) {
        SpringApplication.run(Springproject3Application.class, args);
    }
//    @Bean
//    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, S3Service s3Service,S3Buckets s3Buckets){
//        return args -> {
//            createRandomCustomer(customerRepository, passwordEncoder);
////            test_awsS3(s3Service, s3Buckets);
//        };
//    }

//    private static void test_awsS3(S3Service s3Service, S3Buckets s3Buckets) {
//        s3Service.putObject(s3Buckets.getCustomer(),
//                "test",
//                "Hello World".getBytes());
//
//        byte[] object = s3Service.getObject("springboot3-test", "test");
//        System.out.println(new String(object));
//    }

//    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
//        Faker faker = new Faker();
//        Random random=new Random();
//        Integer age=random.nextInt(20,99);
//        Gender gender=age%2==0?Gender.MALE:Gender.FEMALE;
//        Customer customer=new Customer(
//            faker.name().fullName(),
//            faker.internet().emailAddress(),
//                age,
//                gender, passwordEncoder.encode("password"));
//
//        customerRepository.save(customer);
//    }


}
