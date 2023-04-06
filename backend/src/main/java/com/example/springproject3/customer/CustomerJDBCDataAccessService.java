package com.example.springproject3.customer;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql= """
                select id,name,email,age,gender,password,profile_image_id
                from customer
                LIMIT 1000
                """;

        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql= """
                 select id,name,email,age,gender,password,profile_image_id from customer where id=?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,id).stream().findFirst();

    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql= """
                insert into customer(name,email,age,gender,password)
                values(?,?,?,?,?)
                 """;
        int update=jdbcTemplate.update(sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().name(),
                customer.getPassword()
        );
        System.out.println("jdbctemplate "+ update);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql= """
                select count(id)
                from customer 
                where email=?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count!=null && count>0;
    }

    @Override
    public void deleteCustomer(Integer id) {
        var sql= """
                delete from customer where id=?
                """;
        int update = jdbcTemplate.update(sql, id);
        System.out.println("delete customer by id "+id);
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql= """
                select count(id)
                from customer 
                where id=?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count!=null && count>0;
    }

    @Override
    public void updateCustomer(Customer customer) {
        if(customer.getName()!=null){
            var sql= """
                update customer set name=?
                where id=?
                """;
            jdbcTemplate.update(sql,customer.getName(),customer.getId());
        }
        if(customer.getEmail()!=null){
            var sql= """
                update customer set email=?
                where id=?
                """;
            jdbcTemplate.update(sql,customer.getEmail(),customer.getId());
        }
        if(customer.getAge()!=null){
            var sql= """
                update customer set age=?
                where id=?
                """;
            jdbcTemplate.update(sql,customer.getAge(),customer.getId());
        }
        if(customer.getGender()!=null){
            var sql= """
                update customer set gender=?
                where id=?
                """;
            jdbcTemplate.update(sql,customer.getGender().toString(),customer.getId());
        }
        if(customer.getPassword()!=null){
            var sql= """
                update customer set password=?
                where id=?
                """;
            jdbcTemplate.update(sql,customer.getPassword(),customer.getId());
        }

    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {

        var sql= """
                 select id,name,email,age,gender,password,profile_image_id from customer where email=?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,email).stream().findFirst();
    }

    @Override
    public void updateCustomerProfileImageId(String profileImageID, Integer CustomerId) {
        var sql= """
                update customer
                set profile_image_id=?
                where id= ?
                """;
        jdbcTemplate.update(sql,profileImageID,CustomerId);
    }
}
