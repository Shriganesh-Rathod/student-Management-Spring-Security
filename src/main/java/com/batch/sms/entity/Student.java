package com.batch.sms.entity;

import com.batch.sms.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Student extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    // Relative path (e.g. "uploads/student-images/3_photo.jpg"), not the
    // image bytes themselves — the file lives on disk, the DB only
    // stores where to find it. Nullable: a student can exist without a photo.
    @Column(length = 255)
    private String imagePath;

    // @Embedded pulls Address's columns straight into the students table
    // (address_line/street, city, state, pincode, country). We override
    // "street" here to show how a clashing/generic embeddable column name
    // can be renamed per usage site without touching the Address class.
    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "address_line", length = 100))
    private Address address;

    // Owning side of 1:N with Department — nullable because a student can
    // be created before being assigned to a department.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Owning side of the M:N with Course: @JoinTable here means the join
    // table (student_course) and its two FK columns are defined by THIS
    // entity. Course only has mappedBy = "courses" and does no DDL for it.
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}
