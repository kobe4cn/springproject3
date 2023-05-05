package com.example.springproject3.customer;

import com.example.springproject3.exception.BadRequestException;
import com.example.springproject3.exception.DuplicateResourceException;
import com.example.springproject3.exception.ResourceNotFoundException;
import com.example.springproject3.s3.S3Buckets;
import com.example.springproject3.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper;

    private final S3Service s3Service;

    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, S3Service s3Service, S3Buckets s3Buckets) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerDao.selectAllCustomers().stream().map(customerDTOMapper).collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id){
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() ->
            new ResourceNotFoundException("用户id [%s] 不存在".formatted(id))
        );
    }

    public CustomerDTO getCustomerByEmail(String email){
        return customerDao.selectUserByEmail(email)
                .map(customerDTOMapper)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Email [%s] 不存在".formatted(email))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRquest){
        String email= customerRegistrationRquest.email();
        if(customerDao.existsPersonWithEmail(email)){
            throw new DuplicateResourceException(
                    "电子邮件 [%s] 已经存在".formatted(email));
        }else{
            Customer  customer=new Customer(customerRegistrationRquest.name(),
                    customerRegistrationRquest.email(),
                    customerRegistrationRquest.age(), customerRegistrationRquest.gender(), passwordEncoder.encode(customerRegistrationRquest.password()));
            customerDao.insertCustomer(customer);
        }
    }

    public void deleteCustomer(Integer id){
        checkCustomerExistedOrThrow(id);
        customerDao.deleteCustomer(id);
    }

    private void checkCustomerExistedOrThrow(Integer id) {
        if(!customerDao.existsPersonWithId(id)){


            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }
    }

    public void updateCustomer(Integer customerId,CustomerUpdateRequest customerUpdateRequest){

        Customer customer = customerDao.selectCustomerById(customerId).orElseThrow(() ->
                new ResourceNotFoundException("用户id [%s] 不存在".formatted(customerId))
        );
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
        if(customerUpdateRequest.gender()!=null && customer.getGender()!=customerUpdateRequest.gender()){
            customer.setGender(customerUpdateRequest.gender());
            change=true;
        }
        if(!change){
            throw new BadRequestException("对于ID [%s]，没有更新的内容".formatted(customerId));
        }
        customerDao.updateCustomer(customer);



    }


    public void uploadCustomerImage(Integer customerId, MultipartFile file) {
        checkCustomerExistedOrThrow(customerId);
        String imageID = UUID.randomUUID().toString();
        try {

            s3Service.putObject(s3Buckets.getCustomer(), "/images/%s/%s".formatted(customerId, imageID), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image",e);
        }
        //Store image to db
        customerDao.updateCustomerProfileImageId(imageID,customerId);


    }

    public byte[] getCustomerImage(Integer customerId) {
        var customer=customerDao.selectCustomerById(customerId).map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "用户 %s 不存在".formatted(customerId)
                ));
        if(StringUtils.isBlank(customer.profileImageId())){
            throw new ResourceNotFoundException(
                    "用户 %s 图片 不存在".formatted(customerId));
        }
        var imageID=customer.profileImageId();
        byte[] image = s3Service.getObject(s3Buckets.getCustomer(), "/images/%s/%s".formatted(customerId, imageID));
        return image;
    }
}
