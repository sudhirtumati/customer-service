package com.sudhirt.practice.customer.controller;

import com.sudhirt.practice.customer.entity.Customer;
import com.sudhirt.practice.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/customers/v1")
@Validated
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    Customer get(@NotNull @PathVariable String id) {
        try {
            return customerService.get(id);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    ResponseEntity add(@RequestBody Customer customer) {
        try {
            var savedCustomer = customerService.add(customer);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedCustomer.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch(IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID should not be part of create request");
        }
    }

    @DeleteMapping("/{id}")
    void delete(@NotNull @PathVariable String id) {
        try {
            customerService.delete(id);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    void update(@NotNull @PathVariable String id, @RequestBody Customer customer) {
        customer.setId(id);
        try {
            customerService.update(customer);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
