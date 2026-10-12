package com.batch.sms.entity;

import com.batch.sms.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /**
     * Bidirectional 1:N, owned by Course (mappedBy = "department").
     * cascade = ALL: saving/deleting a Department cascades to its Courses.
     * orphanRemoval = true: if a Course is removed from THIS list (not just
     * its department field nulled), JPA deletes that Course row. This is
     * the key difference from cascade alone — orphanRemoval reacts to
     * collection membership, cascade reacts to the parent's own lifecycle.
     */
    @Builder.Default
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();
}
