package com.cargo.logistic_management.config;

import com.cargo.logistic_management.entity.Role;
import com.cargo.logistic_management.entity.User;
import com.cargo.logistic_management.repository.RoleRepository;
import com.cargo.logistic_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        createRoleIfNotFound("CUSTOMER");
        createRoleIfNotFound("COURIER");
        createRoleIfNotFound("ADMIN");

        if (userRepository.findByEmail("admin@novakargo.com").isEmpty()) {
            Role adminRole = roleRepository.findByRoleName("ADMIN");

            User admin = new User();
            admin.setFullName("Sistem Yöneticisi");
            admin.setEmail("admin@novakargo.com");
            admin.setPhone("00000000000");
            admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
            admin.setRole(adminRole);
            admin.setStatus(true);

            userRepository.save(admin);
            System.out.println(">>> [BİLGİ] Varsayılan Admin hesabı oluşturuldu! (admin@novakargo.com / admin123)");
        }
    }

    private void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
            System.out.println(">>> [BİLGİ] Rol oluşturuldu: " + roleName);
        }
    }
}