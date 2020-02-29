package com.sudhirt.practice.customer.service;

import com.sudhirt.practice.customer.entity.Customer;
import com.sudhirt.practice.customer.repository.CustomerRepository;
import net.bytebuddy.pool.TypePool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer add(Customer customer) {
        if(customer.getId() != null) {
            throw new IllegalArgumentException("ID should not be provided while creating a customer");
        }
        return customerRepository.save(customer);
    }

    public void delete(String id) {
        Customer customer = customerRepository.findById(id).orElseThrow(RuntimeException::new);
        customerRepository.delete(customer);
    }

    public Customer update(Customer customer) {
        if(customer == null) {
            return customer;
        }
        Customer dbCustomer = customerRepository.findById(customer.getId()).orElseThrow(RuntimeException::new);
        return customerRepository.save(customer);
    }

    public Customer get(String id) {
        return customerRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
