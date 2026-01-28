package app.config;

import app.entity.Role;
import app.entity.User;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.email}")
    private String adminEmail;

    public ApplicationInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository,
            RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findRoleByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role().setName("ADMIN")));
        roleRepository.findRoleByName("USER")
                .orElseGet(() -> roleRepository.save(new Role().setName("USER")));

        Optional<User> admin = userRepository.findUserByEmail(adminEmail);
        if (admin.isEmpty()) {
            List<Role> roleList = new ArrayList<>();
            roleList.add(adminRole);
            User initAdmin = new User()
                    .setUsername(adminUsername)
                    .setEmail(adminEmail)
                    .setPassword(passwordEncoder.encode(adminPassword))
                    .setRoles(roleList);
            userRepository.save(initAdmin);
        }

    }
}
