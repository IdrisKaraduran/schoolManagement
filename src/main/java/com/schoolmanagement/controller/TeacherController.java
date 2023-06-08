package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.TeacherResponse;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController//Ozellestirilmis controller
@RequestMapping("teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;


    //Not save methodu
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PostMapping("/save")
    public ResponseMessage<TeacherResponse> save(@Valid @RequestBody TeacherRequest teacher){
        return teacherService.save(teacher);
    }


}
