package com.cargo.logistic_management.service;

import com.cargo.logistic_management.entity.Customer;
import com.cargo.logistic_management.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> tumMusterileriGetir() {
        return customerRepository.findAll();
    }

    public Customer musterikaydet(Customer customer) {
        return customerRepository.save(customer);
    }

    public void musteriSil(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Müşteri bulunamadı!");
        }
        customerRepository.deleteById(id);
    }
}