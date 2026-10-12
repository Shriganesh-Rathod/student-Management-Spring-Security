package com.batch.sms.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Supplies the value for @CreatedBy / @LastModifiedBy on every entity
 * extending BaseEntity. There's no logged-in user yet (Security comes
 * later in the course), so this is a fixed placeholder for now.
 *
 * Once Spring Security is added, replace getCurrentAuditor() with:
 *   SecurityContextHolder.getContext().getAuthentication().getName()
 * and this class stops being a stand-in.
 */
@Component
public class DefaultAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("SYSTEM");
    }
}
