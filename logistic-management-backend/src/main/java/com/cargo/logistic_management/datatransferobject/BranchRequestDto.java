package com.cargo.logistic_management.datatransferobject;

import lombok.Data;

@Data
public class BranchRequestDto {
    private String name;
    private String city;
    private Boolean isTransferCenter;
}