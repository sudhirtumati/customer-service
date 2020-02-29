package com.sudhirt.practice.customer.service;

import com.sudhirt.practice.customer.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
public class CustomerServiceTests {

    @Autowired
    private CustomerService customerService;

    @Test
    void add_should_create_customer_successfully() {
        var customer = Customer.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(LocalDate.now().minusYears(20))
                .active(true).build();
        var savedCustomer = customerService.add(customer);
        assertThat(savedCustomer.getId()).isNotNull();
    }

    @Test
    void delete_should_throw_exception_when_customer_is_not_found() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
            customerService.delete("invalid_id");
        });
    }

    @Test
    void delete_should_remove_customer_successfully() {
        var customer = Customer.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(LocalDate.now().minusYears(20))
                .active(true).build();
        var savedCustomer = customerService.add(customer);
        customerService.delete(savedCustomer.getId());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
            customerService.get(savedCustomer.getId());
        });
    }

    @Test
    void update_should_update_customer_successfully() {
        var customer = Customer.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(LocalDate.now().minusYears(20))
                .active(true).build();
        var savedCustomer = customerService.add(customer);
        savedCustomer.setFirstName("First name updated");
        customerService.update(savedCustomer);
        savedCustomer = customerService.get(savedCustomer.getId());
        assertThat("First name updated").isEqualTo(savedCustomer.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(savedCustomer.getLastName());
        assertThat(customer.getDateOfBirth()).isEqualTo(savedCustomer.getDateOfBirth());
        assertThat(customer.isActive()).isEqualTo(savedCustomer.isActive());
    }
}
