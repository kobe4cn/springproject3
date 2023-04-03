package com.example.springproject3.customer;

public record CustomerRegistrationRequest(
        String name,String email,Integer age,Gender gender,String password
) {

}
