package com.example.springproject3.customer;

import com.example.springproject3.exception.BadRequestException;
import com.example.springproject3.exception.DuplicateResourceException;
import com.example.springproject3.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id).orElseThrow(() ->
            new ResourceNotFoundException("用户id [%s] 不存在".formatted(id))
        );
    }

    public void addCustomer(CustomerRegistrationRquest customerRegistrationRquest){
        String email= customerRegistrationRquest.email();
        if(customerDao.existsPersonWithEmail(email)){
            throw new DuplicateResourceException(
                    "电子邮件 [%s] 已经存在".formatted(email));
        }else{
            Customer  customer=new Customer(customerRegistrationRquest.name(),
                    customerRegistrationRquest.email(),
                    customerRegistrationRquest.age());
            customerDao.insertCustomer(customer);
        }

    }

    public void deleteCustomer(Integer id){
        if(customerDao.existsPersonWithId(id)){
            customerDao.deleteCustomer(id);
        }else {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }
    }

    public void updateCustomer(Integer customerId,CustomerUpdateRequest customerUpdateRequest){

        Customer customer=getCustomerById(customerId);
        boolean change=false;
        if(customerUpdateRequest.name()!=null && !customer.getName().equals(customerUpdateRequest.name())){
            customer.setName(customerUpdateRequest.name());
            change=true;
        }
        if(customerUpdateRequest.email()!=null && !customer.getEmail().equals(customerUpdateRequest.email())){
            if(customerDao.existsPersonWithEmail(customerUpdateRequest.email())){
                throw new DuplicateResourceException("邮箱已存在");
            }
            customer.setEmail(customerUpdateRequest.email());
            change=true;
        }
        if(customerUpdateRequest.age()!=null && customer.getAge()!=customerUpdateRequest.age()){
            customer.setAge(customerUpdateRequest.age());
            change=true;
        }
        if(!change){
            throw new BadRequestException("对于ID [%s]，没有更新的内容".formatted(customerId));
        }
        customerDao.updateCustomer(customer);



    }





}
