package com.example.springproject3.customer;

import com.example.springproject3.exception.BadRequestException;
import com.example.springproject3.exception.DuplicateResourceException;
import com.example.springproject3.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;
    @BeforeEach
    void setUp() {
        underTest=new CustomerService(customerDao);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void cangetCustomerById() {
        int id=10;
        Customer customer=new Customer(
                id,"kevin","kevin.yang@lianwei.com.cn",40
        );
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        assertThat(underTest.getCustomerById(id)).isEqualTo(customer);
    }

    @Test
    void willThrowgetCustomerreturnEmpty() {
        int id=10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户id [%s] 不存在".formatted(id));
    }



    @Test
    void addCustomer() {
        String email="kevin.yang@lianwei.com.cn";
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRquest request=new CustomerRegistrationRquest("kevin",email,40);

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getId()).isNull();
        assertThat(value.getAge()).isEqualTo(request.age());
        assertThat(value.getEmail()).isEqualTo(request.email());
        assertThat(value.getName()).isEqualTo(request.name());
    }

    @Test
    void willThrowWhenEmailexist() {
        String email="kevin.yang@lianwei.com.cn";
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRquest request=new CustomerRegistrationRquest("kevin",email,40);
        assertThatThrownBy(() -> underTest.addCustomer(request)).isInstanceOf(DuplicateResourceException.class)
                        .hasMessageContaining("电子邮件 [%s] 已经存在".formatted(email));

        verify(customerDao,never()).insertCustomer(any());
//        underTest.addCustomer(request);
//
//        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
//        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
//        Customer value = customerArgumentCaptor.getValue();
//
//        assertThat(value.getId()).isNull();
//        assertThat(value.getAge()).isEqualTo(request.age());
//        assertThat(value.getEmail()).isEqualTo(request.email());
//        assertThat(value.getName()).isEqualTo(request.name());
    }

    @Test
    void deleteCustomer() {
        int id=1;
        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(true);

        underTest.deleteCustomer(id);
        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void withThrowndeleteCustomer() {
        int id=1;
        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("customer with id [%s] not found".formatted(id));
        verify(customerDao,never()).deleteCustomer(id);

    }



    @Test
    void updateCustomerwithName() {
        int id=1;
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("kevin1", null, null);
        underTest.updateCustomer(id,updateRequest);
        ArgumentCaptor<Customer> argumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());

        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getName()).isEqualTo(updateRequest.name());
        assertThat(captorValue.getAge()).isEqualTo(customer.getAge());
        assertThat(captorValue.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void updateCustomerwithEmail() {
        int id=1;
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, "kevin@lianwei.com.cn", null);
        underTest.updateCustomer(id,updateRequest);
        ArgumentCaptor<Customer> argumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());

        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getName()).isEqualTo(customer.getName());
        assertThat(captorValue.getAge()).isEqualTo(customer.getAge());
        assertThat(captorValue.getEmail()).isEqualTo(updateRequest.email());
    }

    @Test
    void updateCustomerwithAge() {
        int id=1;
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 5);
        underTest.updateCustomer(id,updateRequest);
        ArgumentCaptor<Customer> argumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());

        Customer captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getName()).isEqualTo(customer.getName());
        assertThat(captorValue.getAge()).isEqualTo(updateRequest.age());
        assertThat(captorValue.getEmail()).isEqualTo(customer.getEmail());
    }
    @Test
    void updateCustomerwithNochange() {
        int id=1;

        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());
        assertThatThrownBy(() -> underTest.updateCustomer(id,updateRequest)).isInstanceOf(BadRequestException.class).hasMessageContaining("对于ID [%s]，没有更新的内容".formatted(id));
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void updateCustomerwithEmailExist() {
        int id=1;
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, "kkk@suba.cincb", null);
        Mockito.when(customerDao.existsPersonWithEmail(updateRequest.email())).thenReturn(true);


        assertThatThrownBy(() ->underTest.updateCustomer(id,updateRequest) ).isInstanceOf(DuplicateResourceException.class).hasMessageContaining("邮箱已存在");
        verify(customerDao,never()).updateCustomer(any());
    }
}