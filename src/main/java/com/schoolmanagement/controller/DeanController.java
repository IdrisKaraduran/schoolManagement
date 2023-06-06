package com.schoolmanagement.controller;


import com.schoolmanagement.payload.Response.DeanResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.service.DeanService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("dean")
public class DeanController {

    private final DeanService deanService;

    //save methodu (Neyi kaydedecegiz. Dean rolune sahip user i kaydetme islemidir.)
    @PostMapping("/save")//http://localhost:8080/dean/save
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> save(@RequestBody
                                @Valid DeanRequest deanRequest){

        return deanService.save(deanRequest);
    }

    //Not :UpdateById() ***********************
    @PutMapping("/update/{userId}")//http://localhost:8080/dean/update/1
    @PreAuthorize("hasAuthority('ADMIN')")//
    public ResponseMessage<DeanResponse> update(@RequestBody @Valid
                 DeanRequest deanRequest,@PathVariable Long userId){

        return deanService.update(deanRequest,userId);//methodda kactane parametre varsa o kadar gonderiyoruz
        //Odev :Bir dean sadece kendi dean ini update etmeli
        //isPrincible kullan
    }

    //Not Delete methodu
    @DeleteMapping("/delete/{userId}")//http://localhost:8080/dean/delete/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long userId){

        return deanService.deleteDean(userId);
    }

    //Not Id ye gore Dean i getir
    @GetMapping("/getManagerById/{userId}")//http://localhost:8080/dean/getMnagerById/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> getDeanById(@PathVariable Long userId){

        return deanService.getDeanById(userId);
    }


    //GetAll Methodu
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ADMIN')")//http://localhost:8080/dean/getAll
    public List<DeanResponse> getAll(){
        return deanService.getAllDean();
    }

//search
    @GetMapping("/search")//Burda search yok sadece page olarak getAll yapildi
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<DeanResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return deanService.search(page,size,sort,type);
    }









}
