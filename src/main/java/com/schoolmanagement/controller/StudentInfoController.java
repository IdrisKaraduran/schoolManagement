package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.StudentInfoResponse;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //save
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> save(HttpServletRequest httpServletRequest,
                                                     @RequestBody @Valid StudentInfoRequestWithoutTeacherId studentInfoRequestWithoutTeacherId){
        String username = (String) httpServletRequest.getAttribute("username");
        return studentInfoService.save(username,studentInfoRequestWithoutTeacherId);
    }

    //Delete Methodu
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @DeleteMapping("/delete/{studentInfoId}")
    public ResponseMessage<?> delete(@PathVariable Long studentInfoId){

        return studentInfoService.deleteStudentInfo(studentInfoId);
    }


    //update islemi
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PutMapping("/update/{studentInfoId}")
    public ResponseMessage<StudentInfoResponse> update(
            @RequestBody @Valid UpdateStudentInfoRequest studentInfoRequest,
             @PathVariable Long studentInfoId){
        return studentInfoService.update(studentInfoRequest,studentInfoId);

    }
//GETaLLForAdmin
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAllForAdmin")
    public ResponseEntity<Page<StudentInfoResponse>> getAll(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){

        //Pageable olusturma islemini Service katinda yazilmasi best practice

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending()) ;
        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllForAdmin(pageable);

        return new ResponseEntity<>(studentInfoResponse, HttpStatus.OK);
    }

    //getAllForTeacher()
    //Bir ogretmen kendi ogrencilerinin bilgilerini almak istedigi zaman bu method calisacak
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllForTeacher")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
            HttpServletRequest httpServletRequest,//Burda hangi ogretmen ise onu buluyoruz
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        Pageable pageable = PageRequest.
                of(page,size, Sort.by("id").descending()) ;
        String username = (String) httpServletRequest.
                getAttribute("username");

       Page<StudentInfoResponse> studentInfoResponse = studentInfoService
               .getAllTeacher(pageable,username);

      // return ResponseEntity.ok(studentInfoResponse); Buda boyle yazilabilir.
        return new ResponseEntity<>(studentInfoResponse,HttpStatus.OK);
    }

    //getAllForStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllByStudent")
    public ResponseEntity<Page<StudentInfoResponse>> getAllByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending()) ;
        String username = (String) httpServletRequest.getAttribute("username");
       Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllStudentInfoByStudent(username,pageable);
       return ResponseEntity.ok(studentInfoResponse);

    }

    //getStudentInfoByStudentId()

    @PreAuthorize("hasAnyAuthority('TEACHER','MANAGER','ASSISTANTMANAGER','ADMIN')")
    @GetMapping("/getByStudentId/{studentId}")
    public ResponseEntity<List<StudentInfoResponse>> getStudentId(@PathVariable Long studentId){
        List<StudentInfoResponse> studentInfoResponse = studentInfoService.
                getStudentInfoByStudentId(studentId);

        return ResponseEntity.ok(studentInfoResponse);
    }



    //getStudentInfoById
    @PreAuthorize("hasAnyAuthority('TEACHER','MANAGER','ASSISTANTMANAGER','ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<StudentInfoResponse> get(@PathVariable Long id){

    StudentInfoResponse studentInfoResponse = studentInfoService
            .findStudentInfoById(id);

    return ResponseEntity.ok(studentInfoResponse);
    }

    //getAllWithPage
    @PreAuthorize("hasAnyAuthority('MANAGER','ASSISTANTMANAGER','ADMIN')")
    @GetMapping("/search")
    public Page<StudentInfoResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type

    ){
        return studentInfoService.search(page,size,sort,type);
    }















}
