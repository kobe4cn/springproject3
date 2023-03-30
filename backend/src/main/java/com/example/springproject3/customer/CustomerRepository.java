package com.example.springproject3.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByEmail(String email);
    Optional<Customer> findCustomerByEmail(String email);
}
