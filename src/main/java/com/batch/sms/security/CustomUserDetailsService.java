package com.batch.sms.security;

import com.batch.sms.entity.User;
import com.batch.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

     // Repository used to find the user from the database.
    private final UserRepository userRepository;

    /*
     * Spring Security calls this method during authentication.
     *
     * For example:
     * User enters:
     * username = admin
     * password = admin123
     *
     * Spring Security calls:
     * loadUserByUsername("admin")
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*
         * Find the user from our database.
         * If the username does not exist,
         * throw UsernameNotFoundException.
         */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        /*
         * Convert our application's User entity
         * into Spring Security's UserDetails object.
         *
         * Spring Security uses UserDetails internally
         * during authentication.
         */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())

                /*
                 * Spring Security expects roles in the form:
                 *
                 * ROLE_USER
                 * ROLE_ADMIN
                 * The roles(...) method automatically adds
                 * the "ROLE_" prefix.
                 * Role.USER  -> ROLE_USER
                 * Role.ADMIN  -> ROLE_ADMIN
                 */
                .roles(user.getRole().name())
                .disabled(false)
                .build();
    }
}
