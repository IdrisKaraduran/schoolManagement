package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.LessonProgramResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.TeacherResponse;
import com.schoolmanagement.payload.dto.LessonProgramDto;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.repository.LessonProgramRepository;
import com.schoolmanagement.utils.CreateResponseObjectForService;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {


    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final LessonProgramDto lessonProgramDto;
    private final LessonProgramRepository lessonProgramRepository;
    private final StudentService studentService;
    private final CreateResponseObjectForService createResponseObjectForService;


    public ResponseMessage<LessonProgramResponse> save(
            LessonProgramRequest request) {


      Set<Lesson> lessons =
              lessonService.getLessonByLessonIdList(request.getLessonIdList());
      EducationTerm educationTerm =
              educationTermService.getById(request.getEducationTermId());

      //!!!Yukarda geleenn lessons ici bos degilse zaman kontrolu
        if(lessons.size() == 0 ){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        }else if(TimeControl.check(request.getStartTime(),request.getStopTime())){
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }

        //!!DTO -POJO
        LessonProgram lessonProgram = lessonProgramRequestToDto(request,lessons);
        //!!lessonProgram da education Term bilgisi setleniyor
        lessonProgram.setEducationTerm(educationTerm);
        //lessonProgram DB ye kaydediliyor.
       LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
        //ResponMessage objesi olusturuluyor
        return ResponseMessage.<LessonProgramResponse>builder()
                .message("LessonProgram ist Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createLessonProgramResponseForSaveMethod(savedLessonProgram))
                .build();

    }
    private LessonProgram lessonProgramRequestToDto(LessonProgramRequest lessonProgramRequest,Set<Lesson> lessons){
        return lessonProgramDto.dtoLessonProgram(lessonProgramRequest,lessons);
    }

    private LessonProgramResponse createLessonProgramResponseForSaveMethod(LessonProgram lessonProgram){
        return  LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .build();
    }


    //GetAlll
    // Not :  getAll() *************************************************************************
    public List<LessonProgramResponse> getAllLessonProgram() {

        return lessonProgramRepository.findAll()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());

    }
    public LessonProgramResponse createLessonProgramResponse(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
               // .lessonName(lessonProgram.getLesson())
                .teachers(lessonProgram.getTeachers()
                        .stream()
                        .map(this::createTeacher)
                        .collect(Collectors.toSet()))
                .students(lessonProgram.getStudents()
                        .stream()
                        .map(createResponseObjectForService::createStudentResponse)
                        .collect(Collectors.toSet()))
                .build();
    }
    public TeacherResponse createTeacher(Teacher teacher){

        return TeacherResponse.builder()
                .userId(teacher.getId())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .birthDay(teacher.getBirthDay())
                .birthPlace(teacher.getBirthPlace())
                .ssn(teacher.getSsn())
                .phoneNumber(teacher.getPhoneNumber())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .username(teacher.getUsername())
                .build();
    }


    public LessonProgramResponse getByLessonProgramId(Long id) {

     LessonProgram lessonProgram =
             lessonProgramRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });
           return lessonProgramRepository.findById(id)
                   .map(this::createLessonProgramResponse).get();
    }


    public List<LessonProgramResponse> getAllLessonProgramUnassigned() {


        return lessonProgramRepository.findByTeaachers_IdNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }


    public List<LessonProgramResponse> getAllLessonProgramAssigned() {

        return lessonProgramRepository.findByTeachers_IdNotNull()
                .stream()
                .map(this::createLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage deleteLessonProgram(Long id) {
        //!!! id kontrolu
        lessonProgramRepository.findById(id).orElseThrow(()->{
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });
        lessonProgramRepository.deleteById(id);
        //Bu lessonPrograma dahil olan teacer ve student larda degisiklik yapilmasi gerekiyor
        //biz bunu lessonProgram entity sinifi icinde @PreRemove ile yaptik.

        return ResponseMessage.builder()
                .message("Lesson Program is deleted succesfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public Set<LessonProgramResponse> getLessonProgramByTeacher(String username) {
        return lessonProgramRepository.getLessonProgramByTeacherUsername(username)
                .stream()
                .map(this::createLessonProgramResponseForTeacher)
                .collect(Collectors.toSet());
    }
    public LessonProgramResponse createLessonProgramResponseForTeacher(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .students(lessonProgram.getStudents().stream()
                        .map(createResponseObjectForService::createStudentResponse)
                        .collect(Collectors.toSet()))
                .build();
    }


    public Set<LessonProgramResponse> getLessonProgramByStudent(String username) {

        return lessonProgramRepository.getLessonProgramByStudentUsername(username)
                .stream()
                .map(this::createLessonProramResponseForStudent)
                .collect(Collectors.toSet());



    }
    public LessonProgramResponse createLessonProramResponseForStudent(LessonProgram lessonProgram){

        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .teachers(lessonProgram.getTeachers()
                        .stream()
                        .map(this::createTeacher)
                        .collect(Collectors.toSet()))
                .build();
    }


    public Page<LessonProgramResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if (Objects.equals(type,"desc")){
           pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        }
        return lessonProgramRepository.findAll(pageable)
                .map(this::createLessonProgramResponse);

    }


    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdList) {

        return lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdList);
    }
}
