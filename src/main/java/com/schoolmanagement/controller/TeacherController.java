package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.TeacherResponse;
import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    //getAllTeacher
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAll")
    public List<TeacherResponse> getAllTeacher(){

        return teacherService.getAllTeacher();
    }


    //Update Teacher
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<TeacherResponse> updateTeacher(@RequestBody @Valid TeacherRequest request ,@PathVariable Long userId){
        return teacherService.updateTeacher(request,userId)
;    }


    //getTeacherByName
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getTeacherByName")
    public List<TeacherResponse> getTeacherByName(@RequestParam(name = "name") String teacherName){

        return teacherService.getTeacherByName(teacherName);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteTeacher(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }

    //getTeaacherById
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getSavedTeacherById/{id}")
    public ResponseMessage<TeacherResponse> getSavedTeacherById(@PathVariable Long id){
        return teacherService.getSavedTeacherById(id);
    }

    //getAllWithPage
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/seaarch")
    public Page<TeacherResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return teacherService.search(page,size,sort,type);
    }

    //addLessonProgramToTeacherLessonsProgram
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PostMapping("/chooseLesson")
    public ResponseMessage<TeacherResponse> chooseLesson(@RequestBody @Valid ChooseLessonTeacherRequest chooseLessonRequest){

        return teacherService.chooseLesson(chooseLessonRequest);
    }















}
