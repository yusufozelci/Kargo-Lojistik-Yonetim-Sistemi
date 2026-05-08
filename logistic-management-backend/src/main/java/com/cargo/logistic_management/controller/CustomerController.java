package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.entity.Customer;
import com.cargo.logistic_management.service.CustomerService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> musterileriListele() {
        return customerService.tumMusterileriGetir();
    }

    @PostMapping
    public Customer yeniMusteriEkle(@RequestBody Customer customer) {
        return customerService.musterikaydet(customer);
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> musteriSil(@PathVariable Long id) {
        customerService.musteriSil(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }
}