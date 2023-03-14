package com.example.springproject3.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<Customer> getCustomer(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public Customer getCustmerById(@PathVariable("customerId") Integer id){
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRquest customerRegistrationRquest){
        customerService.addCustomer(customerRegistrationRquest);
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

}
