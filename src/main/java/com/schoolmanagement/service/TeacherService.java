package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.TeacherResponse;
import com.schoolmanagement.payload.dto.TeacherRequestDto;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private LessonProgramService lessonProgramService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRequestDto teacherRequestDto;
    private final UserRoleService userRoleService;

    public ResponseMessage<TeacherResponse> save(TeacherRequest teacherRequest) {

        //Su id li lesson program lari getir
        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(teacherRequest.getLessonIdList());

        //lesson programin ici bossa excep firlat
        if(lessons.size()==0){
            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }else {
            //DUPLICATE KONTROLU
           fieldControl.checkDuplicate(teacherRequest.getUsername(),
                        teacherRequest.getSsn(),
                        teacherRequest.getPhoneNumber(),
                        teacherRequest.getEmail());

            //POJO OLUYOR BURDA
            Teacher teacher= teacherRequestToDto(teacherRequest);
            //Role setleniyor.
            teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
            //Ders programi ekleniyor
            teacher.setLessonsProgramList(lessons);
            //Password encode ediliyor.
            teacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
            //DB ye kayit islemi yapiliyor.
            Teacher savedTeacher = teacherRepository.save(teacher);
            //TODO AdvisorTeacher yazilinca ekleme yapilacak

            return ResponseMessage.<TeacherResponse>builder()
                    .message("Teacher saved successfully")
                    .httpStatus(HttpStatus.CREATED)
                    .object(createTeacherResponse(savedTeacher))
                    .build();
        }


    }


    private Teacher teacherRequestToDto(TeacherRequest teacherRequest){
        //Pojo ya cevirdik
        return teacherRequestDto.dtoTeacher(teacherRequest);
    }

    private TeacherResponse createTeacherResponse(Teacher teacher){
        return TeacherResponse.builder()
                .userId(teacher.getId())
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDay(teacher.getBirthDay())
                .birthPlace(teacher.getBirthPlace())
                .ssn(teacher.getSsn())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .build();
    }



}
