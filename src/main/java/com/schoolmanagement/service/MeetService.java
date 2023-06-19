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
import com.schoolmanagement.payload.request.UpdateMeetRequest;
import com.schoolmanagement.repository.MeetRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    public List<MeetResponse> getAllMeetByAdvisorAsList(String username) {
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME,username)));

        return meetRepository.getByAdvisorTeacher_IdEquals(advisorTeacher.getId())
                .stream().map(this::createMeetResponse)
                .collect(Collectors.toList());

    }

    public ResponseMessage<?> delete(Long meetId) {

       Meet meet = meetRepository.findById(meetId).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));

        meetRepository.deleteById(meetId);

        return ResponseMessage.builder()
                .message("Delete Meet Successfully")
                .httpStatus(HttpStatus.OK)
                .build();

    }

    //UPDATE
    public ResponseMessage<MeetResponse> update(UpdateMeetRequest meetRequest, Long meetId) {

        //save ve update kontrol kisimlari ortak methodlar uzerinden cagrilacak

        Meet getMeet = meetRepository.findById(meetId).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE,meetId)));

        //!!!Time controlleri yapmam lazim
        if(TimeControl.check(meetRequest.getStartTime(),meetRequest.getStopTime())){
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);
        }
        //HER OGRENCI ICIN MEET CONFLICT KONTROLU
        for (Long studentId : meetRequest.getStudentIds()) {
            checkMeetConflict(studentId,meetRequest.getDate(),meetRequest.getStartTime(),meetRequest.getStopTime());
        }

        List<Student> students = studentService.getStudentByIds(meetRequest.getStudentIds());

        //DTO -POJO
       Meet meet = createUpdatedMeet(meetRequest,meetId);
       meet.setStudentList(students);
       meet.setAdvisorTeacher(getMeet.getAdvisorTeacher());

       //db ye kayit
       Meet updatedMeet = meetRepository.save(meet);

       return ResponseMessage.<MeetResponse>builder()
               .message("Meet Updated Succesully")
               .httpStatus(HttpStatus.OK)
               .object(createMeetResponse(updatedMeet))
               .build();

    }

    private Meet createUpdatedMeet(UpdateMeetRequest updateMeetRequest,Long id){
        return Meet.builder()
                .id(id)
                .startTime(updateMeetRequest.getStartTime())
                .stopTime(updateMeetRequest.getStopTime())
                .date(updateMeetRequest.getDate())
                .description(updateMeetRequest.getDescription())
                .build();

    }


    public List<MeetResponse> getAllMeetByStudentByUsername(String username) {

     Student student = studentService.getStudentByUsernameForOptional(username).orElseThrow(()->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

     return meetRepository.findByStudentList_IdEquals(student.getId())
             .stream()
             .map(this::createMeetResponse)
             .collect(Collectors.toList());

    }


    public Page<MeetResponse> search(int page, int size) {
      Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
      return meetRepository.findAll(pageable).map(this::createMeetResponse);
    }


















}
