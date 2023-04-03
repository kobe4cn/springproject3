package com.example.springproject3.auth;

import com.example.springproject3.customer.Customer;
import com.example.springproject3.customer.CustomerDTO;
import com.example.springproject3.customer.CustomerDTOMapper;
import com.example.springproject3.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;

    private final JWTUtil jwtUtil;


    public AuthenticationService(AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login (AuthenticationRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.name(),
                        request.password()
                )
        );
        Customer principal = (Customer)authentication.getPrincipal();
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);
        String token = jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());
        return new AuthenticationResponse(token,customerDTO);

    }
}
