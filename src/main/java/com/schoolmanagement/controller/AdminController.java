package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Admin;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    /*
    http://localhost:8080/admin/save
    {
  "username": "john_doe",
  "name": "John",
  "surname": "Doe",
  "birthDay": "1990-01-01",
  "ssn": "123-45-6789",
  "birthPlace": "New York",
  "password": "password123",
  "phoneNumber": "555-123-4567",
  "gender": "MALE",
  "built_in" : false
}








     */

    //1)save Methodu
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")//Sadece bu hasRole sahip olanlar bu methodu kullanabilsin diye bu annotaion i yazdik.

    public ResponseEntity<?> save(@Valid @RequestBody AdminRequest adminRequest){

        return ResponseEntity.ok(adminService.save(adminRequest));
    }

    //2)GetAll Methodu olacak
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public  ResponseEntity<Page<Admin>> getAll(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        //Pageable obje olustrulmasi servis katinda da yapilabilir. Contack message de service de yaptik.
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }
       Page<Admin> author = adminService.getAllAdmin(pageable);
        return new ResponseEntity<>(author,HttpStatus.OK);

    }


    //Dellete olacak
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id){

        return ResponseEntity.ok(adminService.deleteAdmin(id));
    }


}
