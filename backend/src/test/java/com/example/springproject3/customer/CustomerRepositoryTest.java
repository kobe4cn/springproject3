package com.example.springproject3.customer;

import com.example.springproject3.AbstractTestcontainers;
import com.example.springproject3.Testconfig;
import com.example.springproject3.s3.S3Buckets;
import com.example.springproject3.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({Testconfig.class})
class CustomerRepositoryTest extends AbstractTestcontainers {
    @Autowired
    private CustomerRepository underTest;
    @Autowired
    private ApplicationContext applicationContext;


    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
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

    @Test
    void findCustomerByEmail(){
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" + UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.save(customer);
        Optional<Customer> customerByEmail = underTest.findCustomerByEmail(customer.getEmail());
        assertThat(customerByEmail).isNotNull();
    }

    @Test
    void canUpdateProfileImageId(){
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" + UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.save(customer);
        Optional<Customer> customerByEmail = underTest.findCustomerByEmail(customer.getEmail());
        underTest.updateProfileImageId("dddd",customerByEmail.get().getId());
        Optional<Customer> customerOptional = underTest.findById(customerByEmail.get().getId());

        assertThat(customerOptional).isPresent().hasValueSatisfying(customer1 -> {
            assertThat(customer1.getProfileImageId()).isEqualTo("dddd");
        });

    }
}