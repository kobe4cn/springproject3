package com.example.springproject3.customer;

import com.example.springproject3.exception.BadRequestException;
import com.example.springproject3.exception.DuplicateResourceException;
import com.example.springproject3.exception.ResourceNotFoundException;

import com.example.springproject3.s3.S3Buckets;
import com.example.springproject3.s3.S3Service;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder encoder;

    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;


    private CustomerDTOMapper customerDTOMapper=new CustomerDTOMapper();
    @BeforeEach
    void setUp() {
        underTest=new CustomerService(customerDao, encoder, customerDTOMapper, s3Service, s3Buckets);

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
        int id=1;
        Customer customer=new Customer(
                id,"kevin","kevin.yang@lianwei.com.cn",40,Gender.MALE,
                "password");
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDTO expected=customerDTOMapper.apply(customer);
        assertThat(underTest.getCustomerById(id)).isEqualTo(expected);
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
        CustomerRegistrationRequest request=new CustomerRegistrationRequest("kevin",email,40,Gender.MALE,"password");

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor=ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer value = customerArgumentCaptor.getValue();

        assertThat(value.getId()).isNull();
        assertThat(value.getAge()).isEqualTo(request.age());
        assertThat(value.getEmail()).isEqualTo(request.email());
        assertThat(value.getName()).isEqualTo(request.name());
        assertThat(value.getPassword()).isEqualTo(encoder.encode("password"));
    }

    @Test
    void willThrowWhenEmailexist() {
        String email="kevin.yang@lianwei.com.cn";
        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request=new CustomerRegistrationRequest("kevin",email,40,Gender.MALE,"password");
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
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40, Gender.MALE, "password");
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("kevin1", null, null,null,null);
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
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40, Gender.MALE, "password");
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, "kevin@lianwei.com.cn", null,null,null);
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
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40, Gender.MALE, "password");

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 5,null,null);
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

        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40,Gender.MALE, "password");
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge(),customer.getGender(),customer.getPassword());
        assertThatThrownBy(() -> underTest.updateCustomer(id,updateRequest)).isInstanceOf(BadRequestException.class).hasMessageContaining("对于ID [%s]，没有更新的内容".formatted(id));
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void updateCustomerwithEmailExist() {
        int id=1;
        Customer customer=new Customer(id,"kevin","kevin.yang@lianwei.com.cn",40, Gender.MALE, "password");
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, "kkk@suba.cincb", null,null,null);
        Mockito.when(customerDao.existsPersonWithEmail(updateRequest.email())).thenReturn(true);


        assertThatThrownBy(() ->underTest.updateCustomer(id,updateRequest) ).isInstanceOf(DuplicateResourceException.class).hasMessageContaining("邮箱已存在");
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void canUploadProfileImage(){
        int id=10;
       when(customerDao.existsPersonWithId(id)).thenReturn(true);
        MultipartFile multipartFile=new MockMultipartFile("file","Hello World".getBytes());
        String bucket="springboot3-test";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        underTest.uploadCustomerImage(id,multipartFile);

        ArgumentCaptor<String> profileImageId=ArgumentCaptor.forClass(String.class);
        verify(customerDao).updateCustomerProfileImageId(profileImageId.capture(),eq(id));
        verify(s3Service).putObject(bucket,"/images/%s/%s".formatted(id, profileImageId.getValue()),"Hello World".getBytes());
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.existsPersonWithId(customerId)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.uploadCustomerImage(
                customerId, mock(MultipartFile.class))
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [" + customerId + "] not found");

        // Then
        verify(customerDao).existsPersonWithId(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        int customerId = 10;

        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() -> {
            underTest.uploadCustomerImage(customerId, multipartFile);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("failed to upload profile image")
                .hasRootCauseInstanceOf(IOException.class);

        // Then
        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());
    }

    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "2222";
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                19,
                Gender.MALE,
                "password",

                profileImageId
        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "springboot3-test";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(
                bucket,
                "/images/%s/%s".formatted(customerId, profileImageId))
        ).thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        // Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                19,Gender.MALE,
                "password",
                ""
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage( "用户 %s 图片 不存在".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }

    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("用户 %s 不存在".formatted(customerId));

        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }






}