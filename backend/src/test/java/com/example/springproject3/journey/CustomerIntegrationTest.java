package com.example.springproject3.journey;

import com.example.springproject3.customer.*;
import com.github.javafaker.Faker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.com.google.common.io.Files;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout="10000")
public class CustomerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    private static Random random = new Random();

    private final CustomerDTOMapper customerDTOMapper=new CustomerDTOMapper();

    private static String URI = "/api/v1/customers";
    
    @Test
    void canRegisterCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + UUID.randomUUID() + "@lianwei.com.cn";
        Integer age = random.nextInt(1, 100);
        Gender gender=Gender.MALE;
        String password="password";
        //用户注册
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age,gender,password);

        //发送post
        String jwttoken = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk().returnResult(Void.class).getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        //返回所有用户
        List<CustomerDTO> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult().getResponseBody();



        //从customers list中filter出对应email的ID
        int id = customers.stream()
                .filter(customer1 -> customer1.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();
//        customer.setId(id);

        CustomerDTO customerDTO=new CustomerDTO(id,name, email, age, Gender.MALE,List.of("ROLE_USER"),email,null );

        assertThat(customers)
                .contains(customerDTO);
        //根据ID返回用户
        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus().
                isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(customerDTO);

    }

    @Test
    void canDeleteCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + UUID.randomUUID() + "@lianwei.com.cn";
        Integer age = random.nextInt(1, 100);
        Gender gender=Gender.MALE;
        String password="password";
        //用户注册
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age,gender,password);
        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(name, email+".e", age,gender,password);
        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        //发送post创建用户
        String jwttoken = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk().returnResult(Void.class).getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        //返回所有用户
        List<CustomerDTO> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult().getResponseBody();


        //从customers list中filter出对应email的ID
        int id = customers.stream().filter(customer1 -> customer1.email().equals(email)).map(customer1 -> customer1.id()).findFirst().orElseThrow();

        //删除 ID
        webTestClient.delete()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus()
                .isOk();

        //再次查询retrun 404
        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + UUID.randomUUID() + "@lianwei.com.cn";
        Integer age = random.nextInt(1, 100);
        Gender gender=Gender.MALE;
        String password="password";
        //用户注册
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age,gender,password);

        //发送post创建用户
        String jwttoken = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk().returnResult(Void.class).getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        //返回所有用户
        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult().getResponseBody();

        int id = customers.stream().filter(customer1 -> customer1.getEmail().equals(email)).map(customer1 -> customer1.getId()).findFirst().orElseThrow();
        System.out.println("test id: "+ id);


        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("kevinyang", null, null,null,null);
        webTestClient.put()
                .uri(URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();



        CustomerDTO customer = webTestClient.get()
                .uri(URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();
        CustomerDTO customerDTO=new CustomerDTO(
          id,
          "kevinyang",
          email,
          age,
          gender,
          List.of("ROLE_USER"),
          email,null
        );

        assertThat(customer).isEqualTo(customerDTO);

    }
    @Test
    void canUploadAndDownloadProfileImage() throws IOException {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + UUID.randomUUID() + "@lianwei.com.cn";
        Integer age = random.nextInt(1, 100);
        Gender gender=Gender.MALE;
        String password="password";
        //用户注册
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age,gender,password);

        //发送post
        String jwttoken = webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk().returnResult(Void.class).getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        //返回所有用户
        List<CustomerDTO> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult().getResponseBody();

        //从customers list中filter出对应email的ID
        CustomerDTO customerDTO = customers.stream()
                .filter(customer1 -> customer1.email().equals(email))
                .findFirst()
                .orElseThrow();

        assertThat(customerDTO.profileImageId()).isNullOrEmpty();

        Resource resource = new ClassPathResource("%s.jpg".formatted(gender.name()));
        MultipartBodyBuilder bodyBuilder=new MultipartBodyBuilder();
        bodyBuilder.part("file",resource);

//        /{customerId}/profile-image
        webTestClient.post().uri(URI+"/{customerId}/profile-image",customerDTO.id())
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))

                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus()
                .isOk();

        CustomerDTO customer = webTestClient.get()
                .uri(URI+"/{id}", customerDTO.id())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();
        assertThat(customer.profileImageId()).isNotBlank();


        //S3图片下载

        byte[] downloadImage = webTestClient.get().uri(URI + "/{customerId}/profile-image", customerDTO.id())

//                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwttoken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();
        byte[] actual = Files.toByteArray(resource.getFile());
        assertThat(actual).isEqualTo(downloadImage);
    }
}
