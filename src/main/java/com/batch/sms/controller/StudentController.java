package com.batch.sms.controller;

import com.batch.sms.dto.PageResponse;
import com.batch.sms.dto.StudentRequestDto;
import com.batch.sms.dto.StudentResponseDto;
import com.batch.sms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student CRUD, search, and photo upload")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Create a student")
    @ApiResponse(responseCode = "201", description = "Student created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(@Valid @RequestBody StudentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(request));
    }

    @Operation(summary = "List students with pagination, sorting, and optional name search",
            description = "Example: /api/students?page=0&size=10&sort=firstName,asc&search=rah")
    @GetMapping
    public ResponseEntity<PageResponse<StudentResponseDto>> getStudents(
            @Parameter(description = "Matches against first or last name, case-insensitive")
            @RequestParam(required = false) String search,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(studentService.getStudents(search, pageable));
    }

    @Operation(summary = "Get a student by id")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Operation(summary = "Update a student")
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id,
                                                            @Valid @RequestBody StudentRequestDto request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @Operation(summary = "Delete a student")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload or replace a student's photo",
            description = "multipart/form-data with a single field named 'file'. Max 5MB.")
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<StudentResponseDto> uploadImage(@PathVariable Long id,
                                                            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentService.uploadImage(id, file));
    }
}
