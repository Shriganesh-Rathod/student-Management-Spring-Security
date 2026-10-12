package com.batch.sms.config;

import com.batch.sms.entity.User;
import com.batch.sms.repository.UserRepository;
import com.batch.sms.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    /*
     * Repository used to save users into the database.
     */
    private final UserRepository userRepository;

    /*
     * PasswordEncoder is used to encode the password
     * before storing it in the database.
     */
    private final PasswordEncoder passwordEncoder;

    /*
     * This method executes automatically when
     * the Spring Boot application starts.
     */
    @Override
    public void run(String... args) {

        /*
         * Check whether the admin user already exists.
         * This prevents creating duplicate admin users
         * every time the application starts.
         */
        if (userRepository.findByUsername("admin").isEmpty()) {

            /*
             * Create the ADMIN user.
             */

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin@123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            System.out.println("======================================");
            System.out.println("Admin user created successfully");
            System.out.println("Username : admin");
            System.out.println("Role     : ADMIN");
            System.out.println("======================================");

        } else
        {
            System.out.println("Admin user already exists.");
        }
    }



}
