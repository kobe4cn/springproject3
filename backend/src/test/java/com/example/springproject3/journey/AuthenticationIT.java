package com.example.springproject3.journey;

import com.example.springproject3.auth.AuthenticationRequest;
import com.example.springproject3.auth.AuthenticationResponse;
import com.example.springproject3.customer.CustomerDTO;
import com.example.springproject3.customer.CustomerDTOMapper;
import com.example.springproject3.customer.CustomerRegistrationRequest;
import com.example.springproject3.customer.Gender;
import com.example.springproject3.jwt.JWTAuthenticationFilter;
import com.example.springproject3.jwt.JWTUtil;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private JWTUtil jwtUtil;
    private static String URI = "/api/v1/auth";
    private static String PATH = "/api/v1/customers";
    private static Random random = new Random();

    private final CustomerDTOMapper customerDTOMapper=new CustomerDTOMapper();

    @Test
    void canLogin(){


            Faker faker = new Faker();
            String name = faker.name().fullName();
            String email = faker.name().lastName() + UUID.randomUUID() + "@lianwei.com.cn";
            Integer age = random.nextInt(1, 100);
            Gender gender=Gender.MALE;
            String password="password";
            //用户注册
            CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age,gender,password);

        AuthenticationRequest authenticationRequest=new AuthenticationRequest(
                email,password
        );
            //在用户不存在的情况返回 没有认证通过
            webTestClient.post()
                    .uri(URI+"/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(authenticationRequest),AuthenticationRequest.class)
                    .exchange()
                    .expectStatus()
                    .isUnauthorized();
            //发起用户注册
            String jwttoken = webTestClient.post()
                    .uri(PATH)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(request), CustomerRegistrationRequest.class)
                    .exchange()
                    .expectStatus()
                    .isOk().returnResult(Void.class).getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        //用户注册之后登陆成功
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(URI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        CustomerDTO customerDTO = result.getResponseBody().customerDTO();
//        AuthenticationResponse authenticationResponse = (AuthenticationResponse) customerDTO;

        String token = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        assertThat(jwtUtil.isTokenValid(token,customerDTO.username())).isTrue();
        assertThat(customerDTO.username()).isEqualTo(email);

//            //返回所有用户
//            List<CustomerDTO> customers = webTestClient.get()
//                    .uri(URI)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
//                    .exchange()
//                    .expectStatus().isOk()
//                    .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
//                    })
//                    .returnResult().getResponseBody();
//
//
//
//            //从customers list中filter出对应email的ID
//            int id = customers.stream()
//                    .filter(customer1 -> customer1.email().equals(email))
//                    .map(CustomerDTO::id)
//                    .findFirst()
//                    .orElseThrow();
////        customer.setId(id);
//
//            CustomerDTO customerDTO=new CustomerDTO(id,name, email, age, Gender.MALE,List.of("ROLE_USER"),email);
//
//            assertThat(customers)
//                    .contains(customerDTO);
//            //根据ID返回用户
//            webTestClient.get()
//                    .uri(URI + "/{id}", id)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .header(HttpHeaders.AUTHORIZATION,String.format("Bearer %s",jwttoken))
//                    .exchange()
//                    .expectStatus().
//                    isOk()
//                    .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
//                    })
//                    .isEqualTo(customerDTO);


    }
}
