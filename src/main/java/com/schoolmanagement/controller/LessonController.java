package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.payload.Response.LessonResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.LessonRequest;
import com.schoolmanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    //Save () methodu

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @PostMapping("/save")
    public ResponseMessage<LessonResponse> save(
            @Valid @RequestBody LessonRequest lesson
            ){
        return lessonService.save(lesson);

    }

    //delete
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteLesson(@PathVariable Long id){
        return lessonService.deleteLesson(id);
    }

    //getLessonByLessonName()//http://localhost:8080/lessons/getLessonByName
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getLessonByName")
    public ResponseMessage<LessonResponse> getLessonByLessonName(@RequestParam String lessonName){

        return lessonService.getLessonByLessonName(lessonName);

    }
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAll")
    public List<LessonResponse> getAllLesson(){
        return lessonService.getAllLesson();
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<LessonResponse> search(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size,
        @RequestParam(value = "sort") String sort,
        @RequestParam(value = "type") String type
    ){
        return lessonService.search(page,size,sort,type);
    }

    //getAllLessonByLessonIds()
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAllLessonByLessonId")
    public Set<Lesson> getAllLessonByLessonId(@RequestParam(name = "lessonId") Set<Long> idList){
        return lessonService.getLessonByLessonIdList(idList);
    }

    //update methodu yazilacak




}
