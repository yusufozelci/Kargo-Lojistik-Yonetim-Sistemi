package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.AddressRequestDto;
import com.cargo.logistic_management.datatransferobject.AddressResponseDto;
import com.cargo.logistic_management.entity.Address;
import com.cargo.logistic_management.entity.Customer;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.AddressRepository;
import com.cargo.logistic_management.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream().map(address -> new AddressResponseDto(
                address.getId(),
                address.getTitle(),
                address.getCity(),
                address.getDistrict(),
                address.getFullAddress(),
                address.getCustomer() != null ? address.getCustomer().getFullName() : "Bilinmiyor"
        )).collect(Collectors.toList());
    }

    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        Address address = new Address();
        address.setTitle(addressRequestDto.getTitle());
        address.setCity(addressRequestDto.getCity());
        address.setFullAddress(addressRequestDto.getAddressText());
        address.setDistrict("-");

        if (addressRequestDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(addressRequestDto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı"));
            address.setCustomer(customer);
        }

        Address savedAddress = addressRepository.save(address);

        return new AddressResponseDto(
                savedAddress.getId(),
                savedAddress.getTitle(),
                savedAddress.getCity(),
                savedAddress.getDistrict(),
                savedAddress.getFullAddress(),
                savedAddress.getCustomer() != null ? savedAddress.getCustomer().getFullName() : null
        );
    }
}