package com.boottech.springshirojwt.config;

import com.boottech.springshirojwt.entities.GroupRole;
import com.boottech.springshirojwt.entities.User;
import com.boottech.springshirojwt.repositories.GroupRoleRepository;
import com.boottech.springshirojwt.repositories.UserRepository;
import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final GroupRoleRepository roleRepo;
    private final PasswordService passwordService;

    public DataLoader(UserRepository userRepo, GroupRoleRepository roleRepo, PasswordService passwordService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordService = passwordService;
    }

    @Override
    public void run(String... args) {

        GroupRole adminRole = createRoleIfNotExists(
                "Administrator",
                "ADMIN",
                "user:read,user:create,user:update,user:delete"
        );

        GroupRole userRole = createRoleIfNotExists(
                "User",
                "USER",
                "user:read"
        );

        createUserIfNotExists(
                "admin",
                "admin123",
                "admin@example.com",
                adminRole
        );

        createUserIfNotExists(
                "user",
                "user123",
                "user@example.com",
                userRole
        );
    }


    private GroupRole createRoleIfNotExists(String name, String code, String permissions) {
        return roleRepo.findByCode(code)
                .orElseGet(() -> {
                    GroupRole role = new GroupRole();
                    role.setName(name);
                    role.setCode(code);
                    role.setPermissions(permissions);
                    return roleRepo.save(role);
                });
    }

    private void createUserIfNotExists(
            String username,
            String rawPassword,
            String email,
            GroupRole role
    ) {
        if (userRepo.findByUsername(username).isPresent()) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordService.encryptPassword(rawPassword))
                .email(email)
                .enabled(true)
                .roles(List.of(role))
                .build();

        userRepo.save(user);
    }


}
