package com.example.springproject3.customer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;

class CustomerRowMapperTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper=new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getInt("age")).thenReturn(40);
        Mockito.when(resultSet.getString("name")).thenReturn("kevin");
        Mockito.when(resultSet.getString("email")).thenReturn("kevin@lianwei.com.cn");

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        Customer expected=new Customer(1,"kevin","kevin@lianwei.com.cn",40);
        assertThat(actual).isEqualTo(expected);
    }


}