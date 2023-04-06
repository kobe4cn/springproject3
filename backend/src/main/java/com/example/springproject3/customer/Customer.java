package com.example.springproject3.customer;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.print.DocFlavor;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customer",uniqueConstraints ={
        @UniqueConstraint(name="customer_email_unique",
                columnNames = "email"
        ),
        @UniqueConstraint(name="profile_image_id_unique",
                columnNames = "profileImageId"
        )
})
public class Customer implements UserDetails {
    @Id
    @SequenceGenerator(name = "customer_id_seq",
            sequenceName = "customer_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "customer_id_seq",strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String profileImageId;

    public String getProfileImageId() {
        return profileImageId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", password='" + password + '\'' +
                ", profileImageId='" + profileImageId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age) && gender == customer.gender && Objects.equals(password, customer.password) && Objects.equals(profileImageId, customer.profileImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, gender, password, profileImageId);
    }

    public void setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
    }

    public Customer(String name, String email, Integer age, Gender gender, String password) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender=gender;
        this.password=password;

    }
    public Customer(String name, String email, Integer age, Gender gender, String password,String profileImageId) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender=gender;
        this.password=password;
        this.profileImageId=profileImageId;

    }

    public Customer(Integer id, String name, String email, Integer age, Gender gender, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender=gender;
        this.password=password;
    }
    public Customer(Integer id, String name, String email, Integer age, Gender gender, String password, String profileImageId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender=gender;
        this.password=password;
        this.profileImageId=profileImageId;
    }

    public Customer() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }




    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
