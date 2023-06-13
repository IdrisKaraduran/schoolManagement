package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.StudentResponse;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseMessage<StudentResponse> save(@RequestBody @Valid
                        StudentRequest studentRequest){

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

    //deleteStudent ById
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{studentId}")
    public ResponseMessage<?> deleteStudent(@PathVariable Long studentId){

        return studentService.deleteStudent(studentId);
    }

    //getStudentByName
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getStudentByName")
    public List<StudentResponse> getStudentByName(@RequestParam(name = "name") String studentName){

        return studentService.getStudentByName(studentName);
    }

    //GetStudentById
    //TODO donen deger Pojo olmali
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getStudentByName")
    public Student getStudentByName(@RequestParam(name = "id") Long id){//donen deger pojo olmamali ancak gorevde boyle istedkleri icin bu sekilde yaptik.

        return studentService.getStudentByIdForResponse(id);
    }

    //getAllWithPage
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<StudentResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return studentService.search(page,size,sort,type);
    }

    //chooseLessonProgramById
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @PostMapping("/chooseLesson")
    public ResponseMessage<StudentResponse> chooseLesson(
            HttpServletRequest request, @RequestBody @Valid ChooseLessonProgramWithId chooseLessonProgramRequest){
       //Bu kisim service de yazilirsa daha iyi olur.
        String username = (String) request.getAttribute("username");
        return studentService.chooseLesson(username,chooseLessonProgramRequest);
    }

    //getAllStudentByAdvisorId()
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllByAdvisorId")
    public List<StudentResponse> getAllByAdvisorId(HttpServletRequest request){
        String username = (String) request.getAttribute("username");

        return studentService.getAllStudentByTeacher_Username(username);
    }



















}
