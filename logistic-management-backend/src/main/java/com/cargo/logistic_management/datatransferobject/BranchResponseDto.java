package com.cargo.logistic_management.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BranchResponseDto {
    private Long id;
    private String name;
    private String cityName;
    private Boolean isTransferCenter;
}