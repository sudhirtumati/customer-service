package com.sudhirt.practice.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sudhirt.practice.customer.entity.Customer;
import com.sudhirt.practice.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void get_should_throw_404_when_customer_not_found() throws Exception {
        when(customerService.get(anyString())).thenThrow(new RuntimeException());
        mockMvc.perform(get("/customers/v1/invalid_id")).andExpect(status().isNotFound());
    }

    @Test
    void get_should_return_customer() throws Exception {
        var customer = createCustomer("First Name");
        when(customerService.get(anyString())).thenReturn(customer);
        mockMvc.perform(get("/customers/v1/some_id")).andExpect(status().isOk());
    }

    @Test
    void delete_should_throw_404_when_customer_not_found() throws Exception {
        doThrow(new RuntimeException()).when(customerService).delete(anyString());
        mockMvc.perform(delete("/customers/v1/invalid_id")).andExpect(status().isNotFound());
    }

    @Test
    void delete_should_delete_customer() throws Exception {
        doNothing().when(customerService).delete("some_customer_id");
        mockMvc.perform(delete("/customers/v1/some_customer_id")).andExpect(status().isOk());
    }

    @Test
    void update_should_throw_404_when_customer_not_found() throws Exception {
        when(customerService.update(any(Customer.class))).thenThrow(new RuntimeException());
        var customer = createCustomer("First Name");
        var objectMapper = getObjectMapper();
        var payload = objectMapper.writeValueAsString(customer);
        mockMvc.perform(put("/customers/v1/some_customer_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_should_modify_customer() throws Exception {
        var customer = createCustomer("First Name Updated");
        when(customerService.update(any(Customer.class))).thenReturn(customer);
        var objectMapper = getObjectMapper();
        var payload = objectMapper.writeValueAsString(customer);
        mockMvc.perform(put("/customers/v1/some_customer_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void add_should_throw_400_when_customer_id_is_part_of_payload() throws Exception {
        when(customerService.add(any(Customer.class))).thenThrow(new IllegalArgumentException());
        var customer = createCustomer("First Name");
        var objectMapper = getObjectMapper();
        var payload = objectMapper.writeValueAsString(customer);
        mockMvc.perform(post("/customers/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_should_create_new_customer_successfully() throws Exception {
        var customer = createCustomer("First Name");
        customer.setId(null);
        var objectMapper = getObjectMapper();
        var payload = objectMapper.writeValueAsString(customer);
        customer.setId("customer_id");
        when(customerService.add(any(Customer.class))).thenReturn(customer);
        mockMvc.perform(post("/customers/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/customers/v1/customer_id"));
    }

    private Customer createCustomer(String s) {
        return Customer.builder()
                .firstName(s)
                .lastName("Last Name")
                .dateOfBirth(LocalDate.now().minusYears(20))
                .active(true)
                .build();
    }

    private ObjectMapper getObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}
