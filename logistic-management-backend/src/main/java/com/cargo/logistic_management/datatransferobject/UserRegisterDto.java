package com.cargo.logistic_management.datatransferobject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @NotBlank(message = "Ad Soyad alanı boş bırakılamaz.")
    @Size(min = 3, max = 100, message = "Ad Soyad en az 3, en fazla 100 karakter olmalıdır.")
    private String fullName;

    @NotBlank(message = "E-posta alanı boş bırakılamaz.")
    @Email(message = "Lütfen geçerli bir e-posta adresi giriniz.")
    private String email;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Lütfen geçerli bir telefon numarası giriniz. (Örn: 5551234567)")
    private String phone;

    @NotBlank(message = "Şifre alanı boş bırakılamaz.")
    @Size(min = 8, max = 50, message = "Şifre en az 8, en fazla 50 karakter olmalıdır.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.#_-]).+$",
            message = "Şifreniz en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter (@$!%*?&.#_-) içermelidir."
    )
    private String password;
}