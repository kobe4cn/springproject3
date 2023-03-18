package com.example.springproject3.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPADataAccessServiceTest {
    private CustomerJPADataAccessService  underTest;
    private AutoCloseable autoCloseable;
    private static Faker FAKER=new Faker();
    @Mock
    private CustomerRepository customerRepository;
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest=new CustomerJPADataAccessService(customerRepository);

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //given

        //when
        underTest.selectAllCustomers();
        //then
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        int id=1;
        underTest.selectCustomerById(id);
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);
        Mockito.verify(customerRepository).save(customer);

    }

    @Test
    void existsPersonWithEmail() {
        var email="kevin.yang@lianwei.com.cn";
        underTest.existsPersonWithEmail(email);
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomer() {
        int id=1;
        underTest.deleteCustomer(id);
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void existsPersonWithId() {
        int id=1;
        underTest.existsPersonWithId(id);
        Mockito.verify(customerRepository).existsById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" + UUID.randomUUID(),
                20
        );

        underTest.updateCustomer(customer);
        Mockito.verify(customerRepository).save(customer);
    }
}