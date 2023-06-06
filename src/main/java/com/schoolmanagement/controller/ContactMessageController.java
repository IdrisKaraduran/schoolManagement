package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.ContactMessageResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("contactMessages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    //Not:Save Metodu olacak
    @PostMapping("/save")
    public ResponseMessage<ContactMessageResponse> save(@Valid @RequestBody ContactMessageRequest contactMessageRequest){

        return contactMessageService.save(contactMessageRequest);

    }


    //Not:GetAll metodu olacak
    @GetMapping("/getAll")//Bu tarz yapilar page yapida cagrilir
    public Page<ContactMessageResponse> getAll(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return contactMessageService.getAll(page,size,sort,type);

    }


    //Not:SearcByEmail
    @GetMapping("/searchByEmail")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")//Sadece bu hasRole sahip olanlar bu methodu kullanabilsin diye bu annotaion i yazdik.
    public Page<ContactMessageResponse> searcByEmail(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type

    ){
        return contactMessageService.searchByEmail(email,page,size,sort,type);
    }

    //Not:searchBySubject
    @GetMapping("/searchBySubject")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")//Sadece bu hasRole sahip olanlar bu methodu kullanabilsin diye bu annotaion i yazdik.

    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "date") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type

    ){
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }


    //Hepsinin postman de testini yap.
    //Annotaion lar icin spring in kaynagina git orda okuyarak bunlari bulabilirsiniz.


}
