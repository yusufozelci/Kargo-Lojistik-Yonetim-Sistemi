package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.datatransferobject.UserResponseDto;
import com.cargo.logistic_management.entity.Customer;
import com.cargo.logistic_management.repository.CustomerRepository;
import com.cargo.logistic_management.repository.RoleRepository;
import com.cargo.logistic_management.entity.User;
import com.cargo.logistic_management.repository.UserRepository;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CustomerRepository customerRepository;

    private UserResponseDto convertToDto(User user) {
        String roleName = user.getRole() != null
                ? user.getRole().getRoleName()
                : "-";

        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                roleName,
                user.getStatus()
        );
    }

    private UserResponseDto convertCustomerToDto(Customer customer) {
        return new UserResponseDto(
                customer.getId(),
                customer.getFullName(),
                null, 
                customer.getPhone(),
                "CUSTOMER", 
                true
        );
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllCustomers() {
        Stream<UserResponseDto> customersFromUsers = userRepository.findAllByRole_RoleName("CUSTOMER")
                .stream()
                .map(this::convertToDto);

        Stream<UserResponseDto> customersFromCustomers = customerRepository.findAll()
                .stream()
                .map(this::convertCustomerToDto);

        return Stream.concat(customersFromUsers, customersFromCustomers)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " ID'li kullanıcı bulunamadı!"));
        return convertToDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRegisterDto registerDto) {
        User user = new User();
        user.setFullName(registerDto.getFullName());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));

        roleRepository.findById(1L).ifPresent(user::setRole);
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() != null && "CUSTOMER".equals(savedUser.getRole().getRoleName())) {
            Customer newCustomer = new Customer();
            newCustomer.setFullName(savedUser.getFullName());
            newCustomer.setPhone(savedUser.getPhone());
            newCustomer.setCustomerType("KAYITLI_KULLANICI");

            customerRepository.save(newCustomer);
        }

        return convertToDto(savedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " ID'li kullanıcı bulunamadı!"));
        userRepository.delete(user);
    }

    public void hardDeleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " ID'li kullanıcı bulunamadı!"));

        userRepository.hardDeleteById(id);
    }

    public void generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Bu e-posta adresine ait kullanıcı bulunamadı!"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        user.setResetOtp(otp);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);
        emailService.sendOtpEmail(email, otp);
    }

    public void resetPassword(String email, String otp, String newPassword, String confirmPassword) {
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Girdiğiniz şifreler birbiriyle uyuşmuyor!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Yeni şifre, eski şifrenizle aynı olamaz! Lütfen farklı bir şifre belirleyin.");
        }

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new IllegalArgumentException("Hatalı veya geçersiz doğrulama kodu!");
        }

        if (user.getResetOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Doğrulama kodunun süresi dolmuş.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepository.save(user);

        emailService.sendPasswordChangeNotification(email);
    }
}
