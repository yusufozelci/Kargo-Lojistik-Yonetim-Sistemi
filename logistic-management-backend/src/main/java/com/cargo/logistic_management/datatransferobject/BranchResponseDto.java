package com.cargo.logistic_management.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BranchResponseDto {
    private Long id;
    private String name;
    private String city;
    private Boolean isTransferCenter;
}