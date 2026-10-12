package com.batch.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Turns on @CreatedDate/@CreatedBy/@LastModifiedDate/@LastModifiedBy
 * processing for every entity extending BaseEntity. auditorAwareRef
 * points at the DefaultAuditorAware bean, which supplies the "who"
 * for createdBy/updatedBy.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "defaultAuditorAware")
public class JpaAuditingConfig {
}
