package com.example.springproject3.customer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerDTOMapper implements Function<Customer,CustomerDTO> {

    @Override
    public CustomerDTO apply(Customer customer) {
       return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
               customer.getAge(),
                customer.getGender(),
               customer.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
               customer.getUsername(),
               customer.getProfileImageId()
        );
    }
}
