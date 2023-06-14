package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.TeacherResponse;
import com.schoolmanagement.payload.dto.TeacherRequestDto;
import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.CheckSameLessonProgram;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private LessonProgramService lessonProgramService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRequestDto teacherRequestDto;
    private final UserRoleService userRoleService;
    private final AdvisorTeacherService advisorTeacherService;

    public ResponseMessage<TeacherResponse> save(TeacherRequest teacherRequest) {

        //Su id li lesson program lari getir
        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

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
           //Degisiklik Advisor Teacher Tablosunda yapilacagi icin teacher kaydinin yapilmasi sorun degil
            if (teacherRequest.isAdvisorTeacher()){
                advisorTeacherService.saveAdvisorTeacher(savedTeacher);
            }


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


    //GetAll
    public List<TeacherResponse> getAllTeacher() {

        return teacherRepository.findAll().stream()
                .map(this::createTeacherResponse)
                .collect(Collectors.toList());
    }

    //Update

    public ResponseMessage<TeacherResponse> updateTeacher(TeacherRequest request, Long userId) {

        //id uzerinden teacher nesnesi getiriliyor
       Optional<Teacher> teacher = teacherRepository.findById(userId);
       //Dto uzerinden eklenecek lessonlar getriliyor

       Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(request.getLessonsIdList());

       if(teacher.isPresent()){
           throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
       }
       else if(lessons.size() == 0){
           throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
       } else if(!checkParameterForUpdateMethod(teacher.get(),request)){
           fieldControl.checkDuplicate(request.getUsername(),
                   request.getSsn(),
                   request.getPhoneNumber(),
                   request.getEmail()
                 );

       }

       //Dto Pojo za cevrilizor
      Teacher updatedTeacher= createUpdateTeacher(request,userId);

       //password encode ediliyor
        updatedTeacher.setPassword(passwordEncoder.encode(request.getPassword()));
        //lessonProgram setleniyor
        updatedTeacher.setLessonsProgramList(lessons);//TODO BURAYA BAKILACAK

       Teacher savedTeacher = teacherRepository.save(updatedTeacher);

       advisorTeacherService.updateAdvisorTeacher(request.isAdvisorTeacher(),savedTeacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message("Teacher updated")
                .httpStatus(HttpStatus.CREATED)
                .object(createTeacherResponse(savedTeacher))//updated teacher da yazilabilir.
                .build();



    }
    private Teacher createUpdateTeacher(TeacherRequest teacher,Long id){
        return Teacher.builder()
                .id(id)
                .username(teacher.getUsername())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .ssn(teacher.getSsn())
                .birthPlace(teacher.getBirthPlace())
                .birthDay(teacher.getBirthDay())
                .phoneNumber(teacher.getPhoneNumber())
                .isAdvisor(teacher.isAdvisorTeacher())
                .gender(teacher.getGender())
                .userRole(userRoleService.getUserRole(RoleType.TEACHER))
                .email(teacher.getEmail())
                .build();


    }
    private boolean checkParameterForUpdateMethod(Teacher teacher, TeacherRequest newTeacherRequest) {
        return teacher.getSsn().equalsIgnoreCase(newTeacherRequest.getSsn())
                || teacher.getUsername().equalsIgnoreCase(newTeacherRequest.getUsername())
                || teacher.getPhoneNumber().equalsIgnoreCase(newTeacherRequest.getPhoneNumber())
                || teacher.getEmail().equalsIgnoreCase(newTeacherRequest.getEmail());
    }


    //GetTeacherByName
    public List<TeacherResponse> getTeacherByName(String teacherName) {

       return teacherRepository.getTeacherByNameContaining(teacherName)
                .stream()
                .map(this::createTeacherResponse)
                .collect(Collectors.toList());
    }

    //delete

    public ResponseMessage<?> deleteTeacher(Long id) {
        teacherRepository.findById(id).orElseThrow(()->{
           throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        });

        teacherRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Teacher is Deleted")
                .httpStatus(HttpStatus.OK)
                .build();

    }


    //GetTeacherById
    public ResponseMessage<TeacherResponse> getSavedTeacherById(Long id) {

        Teacher teacher =  teacherRepository.findById(id)
                  .orElseThrow(()->
                      new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE)
                  );
          return ResponseMessage.<TeacherResponse>builder()
                  .object(createTeacherResponse(teacher))
                  .message("Teacher Succesfully found")
                  .httpStatus(HttpStatus.OK)
                  .build();
    }


    //getAllTeacherWithPage
    public Page<TeacherResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return teacherRepository.findAll(pageable).map(this::createTeacherResponse);
    }


    //addLessonProgramToTeacherLessonsProgram
    public ResponseMessage<TeacherResponse> chooseLesson(ChooseLessonTeacherRequest chooseLessonRequest) {
        //Ekleyecegim teacher yoksa
      Teacher teacher = teacherRepository.findById(chooseLessonRequest.getTeacherId())
                .orElseThrow(()->new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));


      //LessonProgram getiriliyor
      Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(chooseLessonRequest.getLessonProgramId());

              //GelenLessonProgram Ici bos mu kontrolu
        if(lessonPrograms.size() == 0){
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }
        //Teacher in mevcut ders programina
       Set<LessonProgram> existLessonProgram = teacher.getLessonsProgramList();
        CheckSameLessonProgram.checkLessonPrograms(existLessonProgram,lessonPrograms);

        // TODO eklenecek olan LessonProgram mevcuttaki LessonProgram var mi kntrolu

        existLessonProgram.addAll(lessonPrograms);
        teacher.setLessonsProgramList(existLessonProgram);
        Teacher savedTeacher= teacherRepository.save(teacher);

       return ResponseMessage.<TeacherResponse>builder()
               .message("LessonProgram added to Teacher")
               .httpStatus(HttpStatus.CREATED)
               .object(createTeacherResponse(savedTeacher))
               .build();
    }


    public Teacher getTeacherByUsername(String username) {
        if(!teacherRepository.existsByUsername(username)){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        }
        return teacherRepository.getTeacherByUsername(username);
    }




























}
