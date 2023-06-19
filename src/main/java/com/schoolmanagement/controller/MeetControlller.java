package com.schoolmanagement.controller;

import com.schoolmanagement.payload.Response.MeetResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.MeetRequestWithoutId;
import com.schoolmanagement.payload.request.UpdateMeetRequest;
import com.schoolmanagement.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("meet")
@RequiredArgsConstructor
public class MeetControlller {

    private final MeetService meetService;

    //save Methodu bir meet i teacher yapar
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<MeetResponse> save(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid MeetRequestWithoutId meetRequest
    ){
       String username = (String) httpServletRequest.getAttribute("username");

       return meetService.save(username,meetRequest);
    }

    //getAll
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAll")
    public List<MeetResponse> getAll(){
        return meetService.getAll();

    }


    //getMeetById
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getMeetById/{meetId}")
    private ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId){
        return meetService.getMeetById(meetId);
    }

    //getAllMeetByAdvisorAsPage
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetByAdvisorAsPage")
    public ResponseEntity<Page<MeetResponse>> getAllMeetAdvisorAsPage(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
       String username = (String) httpServletRequest.getAttribute("username");
       Pageable pageable = PageRequest.of(page,size, Sort.by("date").descending());
       Page<MeetResponse> meet  = meetService.getAllMeetByAdvisorTeacherAsPage(username,pageable);

      return ResponseEntity.ok(meet);
    }

    //getAllMeetByAdvisorAsList
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetByAdvisorTeacherAsList")
    public ResponseEntity<List<MeetResponse>> getAllMeetByAdvisorAsList(HttpServletRequest httpServletRequest){
       String username = (String) httpServletRequest.getAttribute("username");
       List<MeetResponse> meet = meetService.getAllMeetByAdvisorAsList(username);
       return ResponseEntity.ok(meet);
    }
    
    //delete
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @DeleteMapping("/delete/{meetId}")
    public ResponseMessage<?> delete(@PathVariable Long meetId){
        return meetService.delete(meetId);
    }
    //Her yere ASSISTANT_MANAGER

    //Update
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PutMapping("/update/{meetId}")
    public ResponseMessage<MeetResponse> update(
            @PathVariable Long meetId,
            @RequestBody @Valid UpdateMeetRequest meetRequest

     ){
        return meetService.update(meetRequest,meetId);

    }

    //getAllMeetByStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllMeetByStudent")
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest){
       String username = (String) httpServletRequest.getAttribute("username");
       return meetService.getAllMeetByStudentByUsername(username);
    }

    //getAllMeetWithPage
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/search")
    public Page<MeetResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){
        return meetService.search(page,size);
    }

















}
