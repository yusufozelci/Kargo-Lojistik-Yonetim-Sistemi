package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.datatransferobject.UserResponseDto;
import com.cargo.logistic_management.repository.RoleRepository;
import com.cargo.logistic_management.entity.User;
import com.cargo.logistic_management.repository.UserRepository;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " ID'li kullanıcı bulunamadı!"));
        return convertToDto(user);
    }

    public UserResponseDto createUser(UserRegisterDto registerDto) {
        User user = new User();
        user.setFullName(registerDto.getFullName());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        roleRepository.findById(3L).ifPresent(user::setRole);

        User savedUser = userRepository.save(user);
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

    public void register(UserRegisterDto userRegisterDto) {
    }
}