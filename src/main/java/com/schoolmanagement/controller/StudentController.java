package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.StudentResponse;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    //save methodu
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PostMapping("/save")
    public ResponseMessage<StudentResponse> save(@RequestBody @Valid StudentRequest studentRequest){

        return studentService.save(studentRequest);
    }

    @GetMapping("/changeStatus")//mevcut data ustunde degisiklik icin getMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public ResponseMessage<?> changeStatus(@RequestParam Long id, @RequestParam boolean status){
        return studentService.changeStatus(id,status);
    }

    //getAllStudent()
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAll")
    public List<StudentResponse> getAllStudent(){
        return studentService.getAllStudent();
    }

    //update
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<StudentResponse> updateStudent(@PathVariable Long userId,
                                                          @RequestBody @Valid StudentRequest studentRequest){
        return studentService.updateStudent(userId,studentRequest);
    }










}
