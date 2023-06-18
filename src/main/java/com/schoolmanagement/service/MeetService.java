package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Meet;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.MeetResponse;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.request.MeetRequestWithoutId;
import com.schoolmanagement.repository.MeetRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public ResponseMessage<MeetResponse> save(String username, MeetRequestWithoutId meetRequest) {

   AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(()->
           new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME)));

   //Toplati saatlerinin gecerliligini kontrol ediyorum
        if(TimeControl.check(meetRequest.getStartTime(),meetRequest.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

      //Toplantiya katilacak ogrenciler icin yeni meeting saatlerinde cakisma var mi kontrolu eski toplantilariyla celisiyor mu
      //Toplantiya katilacak ogrencilerin id leri ile calismam lazim her biri icin kontrol edicem

        for (Long studentId: meetRequest.getStudentIds()) {
           boolean check = studentRepository.existsById(studentId);
           if(!check){
               throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE));
           }
        //Burda ogrencinin cakisan toplantisi var mi kontrol etmem gerekiyor.
         checkMeetConflict(studentId, meetRequest.getDate(),meetRequest.getStartTime(),meetRequest.getStopTime());


        }
        //yeni meet e katilacak olan students lar getiriliyor.
       List<Student> students = studentService.getStudentByIds(meetRequest.getStudentIds());
        //Meet nesnesi olusturulup ilgili field lar olusturuluyor
        Meet meet = new Meet();
        meet.setDate(meetRequest.getDate());
        meet.setStartTime(meetRequest.getStartTime());
        meet.setStopTime(meetRequest.getStopTime());
        meet.setStudentList(students);
        meet.setDescription(meetRequest.getDescription());
        meet.setAdvisorTeacher(advisorTeacher);

        //save iskemi
        Meet savedMeet = meetRepository.save(meet);

        //Response nesnesi olusturuluyor.
        return ResponseMessage.<MeetResponse>builder()
                .message("Meet saved succesfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createMeetResponse(savedMeet))
                .build();


    }

    private void checkMeetConflict(Long studentId, LocalDate date, LocalTime startTime, LocalTime stopTime){
     //Ogrencinin meet lerini getirdim.
      List<Meet> meets =  meetRepository.findByStudentList_IdEquals(studentId);

      // TODO meets  size kontrol edilmeli

      for(Meet meet : meets){
        LocalTime existingStartTime =  meet.getStartTime();
        LocalTime existingStopTime =  meet.getStopTime();

          if (meet.getDate().equals(date) && ((startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) || //yenisi eskisinin icinde mi//// yeni gelen meetingin startTime bilgisi mevcut mettinglerden herhangi birinin startTim,e ve stopTime arasinda mi ???
                  (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) || //yenisinin stopu eskisinin (bas-bitis) arasinda mi  ////  yeni gelen meetingin stopTime bilgisi mevcut mettinglerden herhangi birinin startTim,e ve stopTime arasinda mi ???
                  (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) || //yenisi eskisini kapsiyor mu
                  (startTime.equals(existingStartTime) && stopTime.equals(existingStopTime)))) { //eskisi ve yenisi ayni zaman diliminde mi
              throw new ConflictException(Messages.MEET_EXIST_MESSAGE);
          }

      }
    }
    private MeetResponse createMeetResponse(Meet meet){
        return MeetResponse.builder()
                .id(meet.getId())
                .date(meet.getDate())
                .startTime(meet.getStartTime())
                .stopTime(meet.getStopTime())
                .description(meet.getDescription())
                .advisorTeacherId(meet.getAdvisorTeacher().getId())
                .teacherSsn(meet.getAdvisorTeacher().getTeacher().getSsn())
                .teacherName(meet.getAdvisorTeacher().getTeacher().getName())
                .students(meet.getStudentList())
                .build();
    }


    public List<MeetResponse> getAll() {
        return meetRepository.findAll()
                .stream()
                .map(this::createMeetResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<MeetResponse> getMeetById(Long meetId) {
       Meet meet = meetRepository.findById(meetId).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));

       return ResponseMessage.<MeetResponse>builder()
               .message("Meet Successfully found")
               .httpStatus(HttpStatus.OK)
               .object(createMeetResponse(meet))
               .build();


    }


    public Page<MeetResponse> getAllMeetByAdvisorTeacherAsPage(String username, Pageable pageable) {
       AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME,username)));

       return meetRepository.findByAdvisorTeacher_IdEquals(advisorTeacher.getId(),pageable)
               .map(this::createMeetResponse);

    }






}
