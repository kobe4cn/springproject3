package com.example.springproject3.journey;

import com.example.springproject3.customer.*;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
}
