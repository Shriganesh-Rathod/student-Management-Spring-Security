package com.batch.sms.repository;

import com.batch.sms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * Finds a user using the username.
     * Spring Security will use this method later
     * when authenticating a user.
     * Database:
     * admin | encoded-password | ADMIN
     */
    Optional<User> findByUsername(String username);
}
