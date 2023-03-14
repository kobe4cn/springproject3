package com.example.springproject3.customer;

public record CustomerUpdateRequest(Integer id,String name,
                                    String email,Integer age) {
}
