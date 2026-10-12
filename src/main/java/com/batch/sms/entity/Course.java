package com.batch.sms.entity;

import com.batch.sms.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Course extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    private Integer credits;

    // Capacity check is what makes the enrollment-approval workflow
    // realistic: enrolledCount starts at 0 and only ever changes inside
    // EnrollmentRequestServiceImpl.approve(), never set directly by a client.
    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(nullable = false)
    private Integer enrolledCount = 0;

    // Owning side of the 1:N with Department — the FK (department_id)
    // lives in this table's column.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Inverse side of the M:N with Student — Student owns the join table,
    // so this side just reads mappedBy = "courses" from Student.
    @Builder.Default
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
