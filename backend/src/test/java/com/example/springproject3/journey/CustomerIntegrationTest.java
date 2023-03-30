package com.example.springproject3.journey;

import com.example.springproject3.customer.Customer;
import com.example.springproject3.customer.CustomerRegistrationRquest;
import com.example.springproject3.customer.CustomerUpdateRequest;
import com.example.springproject3.customer.Gender;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
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
        CustomerRegistrationRquest request = new CustomerRegistrationRquest(name, email, age,gender,password);

        //发送post
        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRquest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //返回所有用户
        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult().getResponseBody();

        Customer customer = new Customer(name, email, age, Gender.MALE, "password");
        assertThat(customers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(customer);

        //从customers list中filter出对应email的ID
        int id = customers.stream().filter(customer1 -> customer1.getEmail().equals(email)).map(customer1 -> customer1.getId()).findFirst().orElseThrow();
        customer.setId(id);
        //根据ID返回用户
        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(customer);

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
        CustomerRegistrationRquest request = new CustomerRegistrationRquest(name, email, age,gender,password);

        //发送post创建用户
        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRquest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //返回所有用户
        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult().getResponseBody();


        //从customers list中filter出对应email的ID
        int id = customers.stream().filter(customer1 -> customer1.getEmail().equals(email)).map(customer1 -> customer1.getId()).findFirst().orElseThrow();

        //删除 ID
        webTestClient.delete()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //再次查询retrun 404
        webTestClient.get()
                .uri(URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
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
        CustomerRegistrationRquest request = new CustomerRegistrationRquest(name, email, age,gender,password);

        //发送post创建用户
        webTestClient.post()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRquest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //返回所有用户
        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult().getResponseBody();

        int id = customers.stream().filter(customer1 -> customer1.getEmail().equals(email)).map(customer1 -> customer1.getId()).findFirst().orElseThrow();
        System.out.println("test id: "+ id);
        String email1=faker.name().firstName() + UUID.randomUUID() + "@lianwei.com.cn";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, email1, null,null,null);
        webTestClient.put()
                .uri(URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        Customer customer = webTestClient.get()
                .uri(URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer expected=new Customer(id,name,email1,age, gender, "password");
        assertThat(customer).isEqualTo(expected);
    }
}
