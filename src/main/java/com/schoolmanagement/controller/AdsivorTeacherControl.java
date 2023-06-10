package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.payload.Response.AdvisorTeacherResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.service.AdvisorTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advisorTeacher")
@RequiredArgsConstructor
public class AdsivorTeacherControl {
    private final AdvisorTeacherService advisorTeacherService;


    //DeleteAdvisorTeacher(Weil Wir  savedMethod inTeacherController gemacht habe)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteAdvisorTeacher(@PathVariable Long id){
        return advisorTeacherService.deleteAdvisorTeacher(id);
    }



    //GetAllAdvisorTeacher(Normally)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAll")
    public List<AdvisorTeacherResponse> getAllAdvisorTeacher(){
        return advisorTeacherService.getAllAdvisorTeacher();
    }




    //GetAllAdvisorTeacherWithPage (search)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<AdvisorTeacherResponse> search(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type

    ){
        return advisorTeacherService.search(page,size,sort,type);
    }







}
