package com.example.springproject3.customer;

import com.example.springproject3.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {
    @Autowired
    private CustomerRepository underTest;
    @BeforeEach
    void setUp() {

    }

    @Test
    void existsCustomerByEmail() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" + UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.save(customer);


        boolean b = underTest.existsCustomerByEmail(customer.getEmail());
        assertThat(b).isTrue();
    }
}