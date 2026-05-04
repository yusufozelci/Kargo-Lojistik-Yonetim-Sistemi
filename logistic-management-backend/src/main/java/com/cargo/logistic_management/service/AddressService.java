package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.AddressResponseDto;
import com.cargo.logistic_management.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

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
}