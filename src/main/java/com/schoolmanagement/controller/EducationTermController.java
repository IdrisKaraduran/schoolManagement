package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.EducationTermResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.service.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController//Bu ayni zamanda componente ve restfull api dir
@RequestMapping("educationTerms")
@RequiredArgsConstructor
public class EducationTermController {

    private final EducationTermService educationTermService;

    //Save Methodu
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> save(@Valid @RequestBody EducationTermRequest educationTermRequest){
        return educationTermService.save(educationTermRequest);

    }

    //getById
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/{id}")//8080/educationterm/1
    public EducationTermResponse get(@PathVariable Long id){
        return educationTermService.get(id);
    }

    //Not getAll()
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/getAll")
    public List<EducationTermResponse> getAll(){
        return educationTermService.getAll();
    }

    //getAllWithPage()
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/search")
    public Page<EducationTermResponse> getAllWithPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "startDate") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
            return educationTermService.getAllWithPage(page,size,sort,type);

    }

    //delete
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return educationTermService.delete(id);
    }


    //updateById()
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/update/{id}")
    public ResponseMessage<EducationTermResponse> update(@PathVariable Long id,
              @RequestBody @Valid EducationTermRequest educationTermRequest){

        return educationTermService.update(id,educationTermRequest);
    }


















}
