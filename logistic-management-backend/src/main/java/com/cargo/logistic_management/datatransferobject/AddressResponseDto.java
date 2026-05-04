package com.cargo.logistic_management.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressResponseDto {
    private Long id;
    private String title;
    private String city;
    private String district;
    private String fullAddress;
    private String customerName;
}