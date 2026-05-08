package com.cargo.logistic_management.datatransferobject;

import lombok.Data;

@Data
public class AddressRequestDto {
    private String title;
    private String city;
    private String addressText;
    private Long customerId;
}