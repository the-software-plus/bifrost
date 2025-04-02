package com.bifrost.Bifrost.config;

import com.bifrost.Bifrost.entities.Role;
import com.bifrost.Bifrost.entities.User;
import com.bifrost.Bifrost.repository.RoleRepository;
import com.bifrost.Bifrost.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role role = roleRepository.findByName(Role.Values.ADMIN.name());
        var userAdmin = userRepository.findByEmail("admin@icev.com");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin já existe");
                },
                () -> {
                    User admin = new User();

                    admin.setEmail("admin@icev.com");
                    admin.setPassword(passwordEncoder.encode("admin@123"));
                    admin.setUsername("admin");
                    admin.setRoles(Set.of(role));

                    userRepository.save(admin);
                }
        );
    }
}
