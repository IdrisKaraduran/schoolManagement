package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.AdvisorTeacherResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.repository.AdvisorTeaacherRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AdvisorTeacherService {

    private final AdvisorTeaacherRepository advisorTeaacherRepository;
    private final UserRoleService userRoleService;
    public ResponseMessage<?> deleteAdvisorTeacher(Long id) {
      AdvisorTeacher advisorTeacher= advisorTeaacherRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        advisorTeaacherRepository.deleteById(advisorTeacher.getId());

        return ResponseMessage.<AdvisorTeacher>builder()
                .message("Advisor Teacher Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public List<AdvisorTeacherResponse> getAllAdvisorTeacher() {
       return  advisorTeaacherRepository.findAll().stream()
                .map(this::createResponseObject)
                 .collect(Collectors.toList());

    }
    private AdvisorTeacherResponse createResponseObject(AdvisorTeacher advisorTeacher){
        return AdvisorTeacherResponse.builder()
                .advisorTeacherId(advisorTeacher.getId())
                .teacherName(advisorTeacher.getTeacher().getName())
                .teacherSurname(advisorTeacher.getTeacher().getSurname())
                .teacherSSN(advisorTeacher.getTeacher().getSsn())
                .build();

    }

    public Page<AdvisorTeacherResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return advisorTeaacherRepository.findAll(pageable).map(this::createResponseObject);
    }

    //TeacherService icin ekleme yapilacak
    //save AdvisorTeacher

    public void saveAdvisorTeacher(Teacher teacher) {
       AdvisorTeacher advisorTeacherBuilder =  AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER))
                .build();

        advisorTeaacherRepository.save(advisorTeacherBuilder);
    }

    //Update icin UpdateAdvisorTeacher for Teacher
    public void updateAdvisorTeacher(boolean status, Teacher teacher) {
   //status dedigimiz yer isAdvisor mi diye soruyoruz.
        //Teacher Id ile iliskilendirilmis dvisor Teacher nesnesini DB den bulup gwtiriyorum.
       Optional<AdvisorTeacher> advisorTeacher = advisorTeaacherRepository.getAdvisorTeacherByTeacher_Id(teacher.getId());

      AdvisorTeacher.AdvisorTeacherBuilder advisorTeacherBuilder = AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER));

       if(advisorTeacher.isPresent()){//cagrilan data varsa status a gore sildik yada kaydettik.
           if(status){
               advisorTeacherBuilder.id(advisorTeacher.get().getId());
               advisorTeaacherRepository.save(advisorTeacherBuilder.build());
           }else {
               advisorTeaacherRepository.deleteById(advisorTeacher.get().getId());
           }
       }else {
           advisorTeaacherRepository.save(advisorTeacherBuilder.build());

       }


    }























}
