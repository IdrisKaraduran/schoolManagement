package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.ViceDeanResponse;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.service.ViceDeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("vicedean")
@RequiredArgsConstructor
public class ViceDeanController {

    private final ViceDeanService viceDeanService;

    //Not :Save methodu *************************
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> save(@RequestBody @Valid ViceDeanRequest viceDeanRequest){
        return viceDeanService.save(viceDeanRequest);
    }

    //Not UpdateById
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<ViceDeanResponse> update(@RequestBody @Valid  ViceDeanRequest viceDeanRequest ,@PathVariable Long userId ){
          return viceDeanService.update(viceDeanRequest,userId);
    }

    //Delete
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long userId){

       return viceDeanService.deleteViceDean(userId);

    }

     //GetById ile
    @GetMapping("/getViceDeanById/{userId}")
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> getViceDeanById(@PathVariable Long userId){
        return  viceDeanService.getViceDeanById(userId);
    }



    //GetAlll
    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    public List<ViceDeanResponse> getAll(){
        return viceDeanService.getAllViceDean();
    }

    //getAllWithPage
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN','MANAGER')")
    public Page<ViceDeanResponse> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return viceDeanService.getAllWithPage(page,size,sort,type);
    }


}
