package com.example.springproject3.customer;

import com.example.springproject3.AbstractTestcontainers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {
    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper=new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        underTest=new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        //when
        List<Customer> customers = underTest.selectAllCustomers();

        //then

        assertThat(customers).isNotEmpty();
        assertThat(customers.stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail())).count()).isGreaterThanOrEqualTo(1);

    }

    @Test
    void selectCustomerById() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(
                        customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> optional = underTest.selectCustomerById(id);

        assertThat(optional).isPresent().hasValueSatisfying(customer1 -> {
            assertThat(customer1.getId()).isEqualTo(id);
            assertThat(customer1.getName()).isEqualTo(customer.getName());
            assertThat(customer1.getAge()).isEqualTo(customer.getAge());
            assertThat(customer1.getEmail()).isEqualTo(customer.getEmail());
        });

    }

    @Test
    void insertCustomer() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        boolean b = underTest.existsPersonWithEmail(customer.getEmail());
        assertThat(b).isFalse();
        underTest.insertCustomer(customer);
        boolean b1 = underTest.existsPersonWithEmail(customer.getEmail());
        assertThat(b1).isTrue();
    }

    @Test
    void existsPersonWithEmail() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);

        boolean b = underTest.existsPersonWithEmail(customer.getEmail());
        assertThat(b).isTrue();
    }

    @Test
    void deleteCustomer() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId).findFirst().orElseThrow();

        underTest.deleteCustomer(id);
        Optional<Customer> optional = underTest.selectCustomerById(id);
        assertThat(optional).isNotPresent();
    }

    @Test
    void existsPersonWithId() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId).findFirst().orElseThrow();

        boolean b = underTest.existsPersonWithId(id);

        assertThat(b).isTrue();
    }

    @Test
    void updateCustomerwithName() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId).findFirst().orElseThrow();

        Customer update=new Customer();
        update.setId(id);
        update.setName("kevin");
        underTest.updateCustomer(update);
        Optional<Customer> customer1 = underTest.selectCustomerById(id);
        assertThat(customer1).isPresent().hasValueSatisfying(customer2 -> {
            assertThat(customer2.getId()).isEqualTo(id);
            assertThat(customer2.getName()).isEqualTo(update.getName());
            assertThat(customer2.getEmail()).isEqualTo(customer.getEmail());
            assertThat(customer2.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerwithAge() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId).findFirst().orElseThrow();

        Customer update=new Customer();
        update.setId(id);
        update.setAge(30);
        underTest.updateCustomer(update);
        Optional<Customer> customer1 = underTest.selectCustomerById(id);
        assertThat(customer1).isPresent().hasValueSatisfying(customer2 -> {
            assertThat(customer2.getId()).isEqualTo(id);
            assertThat(customer2.getName()).isEqualTo(customer.getName());
            assertThat(customer2.getEmail()).isEqualTo(customer.getEmail());
            assertThat(customer2.getAge()).isEqualTo(update.getAge());
        });

    }

    @Test
    void updateCustomerwithEmail() {
        Customer customer=new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress()+ "-" +UUID.randomUUID(),
                20,
                Gender.MALE, "password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream()
                .filter(customer1 -> customer1.getEmail().equals(customer.getEmail()))
                .map(Customer::getId).findFirst().orElseThrow();

        Customer update=new Customer();
        update.setId(id);
        update.setEmail(FAKER.internet().emailAddress()+"-"+UUID.randomUUID());
        underTest.updateCustomer(update);
        Optional<Customer> customer1 = underTest.selectCustomerById(id);
        assertThat(customer1).isPresent().hasValueSatisfying(customer2 -> {
            assertThat(customer2.getId()).isEqualTo(id);
            assertThat(customer2.getName()).isEqualTo(customer.getName());
            assertThat(customer2.getEmail()).isEqualTo(update.getEmail());
            assertThat(customer2.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void canUpdateProfileImageId(){
        String email= FAKER.internet().safeEmailAddress();
        Customer customer=new Customer(FAKER.name().fullName(),
                email,
                31,Gender.MALE,"password");
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().filter(customer1 -> customer1.getEmail().equals(email)).map(customer1 -> customer1.getId()).findFirst().orElseThrow();
        underTest.updateCustomerProfileImageId("dddd",id);
        Optional<Customer> customerOptional = underTest.selectCustomerById(id);
        assertThat(customerOptional).isPresent().hasValueSatisfying(customer1 -> {
           assertThat( customer1.getProfileImageId()).isEqualTo("dddd");
        });

    }
}