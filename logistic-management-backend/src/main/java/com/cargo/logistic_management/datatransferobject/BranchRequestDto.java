package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranchRequestDto {

    @NotBlank(message = "Şube adı boş bırakılamaz!")
    private String name;

    @NotNull(message = "Adres ID boş bırakılamaz!")
    private Long addressId;

    @NotNull(message = "Aktarma merkezi durumu belirtilmelidir!")
    private Boolean isTransferCenter;
}