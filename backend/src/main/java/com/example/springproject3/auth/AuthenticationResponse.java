package com.example.springproject3.auth;

import com.example.springproject3.customer.CustomerDTO;

public record AuthenticationResponse(String token,CustomerDTO customerDTO) {
}
