package com.example.springproject3.customer;

import com.github.javafaker.Faker;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.*;
import java.util.List;
import java.util.UUID;

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
        Page<Customer> page=Mockito.mock(Page.class);
        List<Customer> customers=List.of(new Customer());
        Mockito.when(page.getContent()).thenReturn(customers);
        Mockito.when(customerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);
        List<Customer> expected=underTest.selectAllCustomers();
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable > pageableArgumentCaptor=ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(customerRepository).findAll(pageableArgumentCaptor.capture());
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(1000));


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
                20,
                Gender.MALE, "password");

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
                20,
                Gender.MALE, "password");

        underTest.updateCustomer(customer);
        Mockito.verify(customerRepository).save(customer);
    }
}