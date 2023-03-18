package com.example.springproject3.customer;

public record CustomerUpdateRequest(String name,
                                    String email,Integer age) {
}
