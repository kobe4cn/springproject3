package com.example.springproject3.customer;

import com.example.springproject3.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController

@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping
    public List<CustomerDTO> getCustomer(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustmerById(@PathVariable("customerId") Integer id){
        return customerService.getCustomerById(id);
    }

    @GetMapping("/email/{email}")
    public CustomerDTO getCustmerById(@PathVariable("email") String email){
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRquest){
        customerService.addCustomer(customerRegistrationRquest);
        String token = jwtUtil.issueToken(customerRegistrationRquest.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,token)
                .build();
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id){

        customerService.deleteCustomer(id);
    }
    @PutMapping("/{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id,
                               @RequestBody CustomerUpdateRequest customerUpdateRequest){
        customerService.updateCustomer(id,customerUpdateRequest);
    }

    @PostMapping(value = "/{customerId}/profile-image",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCustomerProfileImage(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file")MultipartFile file){
        customerService.uploadCustomerImage(customerId,file);
    }
    @GetMapping(value = "/{customerId}/profile-image")
    public byte[] getCustomerProfileImage(
            @PathVariable("customerId") Integer customerId
            ){
        return customerService.getCustomerImage(customerId);
    }

}
