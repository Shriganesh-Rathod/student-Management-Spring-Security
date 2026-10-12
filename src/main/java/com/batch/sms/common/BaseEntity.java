package com.batch.sms.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Shared base for every entity: the primary key and four auditing columns.
 * Subclasses get these fields automatically through inheritance
 * (@MappedSuperclass does NOT create its own table — its fields are added
 * as columns to each subclass's table).
 *
 * createdBy / updatedBy are populated via the AuditorAware<String> bean
 * (see config.DefaultAuditorAware). Until Spring Security is added, that
 * bean just returns a fixed "SYSTEM" value — once Security is in place,
 * it will be swapped to return the actual logged-in username instead.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false, length = 50)
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(length = 50)
    private String updatedBy;
}
