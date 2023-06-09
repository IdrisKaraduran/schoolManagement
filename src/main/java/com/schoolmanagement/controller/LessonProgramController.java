package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.LessonProgramResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.service.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonProgram")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    //Not
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public ResponseMessage<LessonProgramResponse> save(@RequestBody @Valid LessonProgramRequest lessonProgramRequest){

        return lessonProgramService.save(lessonProgramRequest);
    }


    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAll(){

        return lessonProgramService.getAllLessonProgram();
    }


    //getById
    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public LessonProgramResponse getById(@PathVariable Long id){
        return lessonProgramService.getByLessonProgramId(id);
    }

    @GetMapping("/getAllUnassigned")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllLessonProgramUnassigned();
    }

    // Not :  getAllLessonProgramAssigned() **************************************************
    @GetMapping("/getAllAssigned") //http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllLessonProgramAssigned();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public ResponseMessage delete(@PathVariable Long id){

        return lessonProgramService.deleteLessonProgram(id);
    }

    //getLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/getAllLessonProgramByTeacher")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacherId(HttpServletRequest httpServletRequest){

        String username = (String) httpServletRequest.getAttribute("username");

        return lessonProgramService.getLessonProgramByTeacher(username);

    }

    ///getAllLessonProgramByStudent
    @GetMapping("/getAllLessonProgramByStudent")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest httpServletRequest){
        //istek icindeki username e ulasmak icin HttpRequest parametre aliyoruz

        String username = (String) httpServletRequest.getAttribute("username");

        return lessonProgramService.getLessonProgramByStudent(username);
    }

    //getAllWithPage
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> search(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size,
        @RequestParam(value = "sort") String sort,
        @RequestParam(value = "type") String type
    ){
        return lessonProgramService.search(page,size,sort,type);
    }



















}
