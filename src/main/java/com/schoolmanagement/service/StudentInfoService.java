package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.Response.ResponseMessage;
import com.schoolmanagement.payload.Response.StudentInfoResponse;
import com.schoolmanagement.payload.Response.StudentResponse;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.repository.StudentInfoRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {
    private final StudentInfoRepository studentInfoRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;


    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;
    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;



    public ResponseMessage<StudentInfoResponse> save(String username, StudentInfoRequestWithoutTeacherId studentInfoRequest) {
    //Dto ve request den gelen Student TEacher Lesson ve EducationTerm getiriliyor

       Student student = studentService.getStudentByIdForResponse(studentInfoRequest.getStudentId());
       Teacher teacher = teacherService.getTeacherByUsername(username);
       Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
       EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());

       //!!!Lesson Cakisma var mi kontrolu
        if(checkSameLesson(studentInfoRequest.getStudentId(),lesson.getLessonName())){
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE));
        }

        //Ders Notu ortalamasi aliniyor
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());
        //Ders Notu ALfabetik olarak hesaplaniyor
        Note note = checkLetterGrade(noteAverage);

        //Dto-Pojo
        StudentInfo studentInfo = createDto(studentInfoRequest,note,noteAverage);

        //DTO da olmayan fieldlar setleniyor
        studentInfo.setStudent(student);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setTeacher(teacher);
        studentInfo.setLesson(lesson);

        //DB ye kayit islemi yapiliyor
       StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);
       //response olusturuluyor
       return ResponseMessage.<StudentInfoResponse>builder()
               .message("StudentInfo saved successfully")
               .httpStatus(HttpStatus.CREATED)
               .object(createResponse(savedStudentInfo))
               .build();



    }

    private  boolean checkSameLesson(Long studentId,String lessonName){

        return studentInfoRepository.getAllByStudentId_Id(studentId)
                .stream()
                .anyMatch((e)->e.getLesson().getLessonName().equalsIgnoreCase(lessonName));

    }

    private Double calculateExamAverage(Double midtermExam, Double finalExam){

        return ((midtermExam*midtermExamPercentage) +(finalExam*finalExamPercentage));
    }
    private Note checkLetterGrade(Double average){

        if(average<50.0){
            return Note.FF;
        } else if (average>=50.0 && average<55) {
            return Note.DD;
        } else if (average>=55.0 && average<60) {
            return Note.DC;
        } else if (average>=60 && average<65) {
            return Note.CC;
        } else if (average>=65 && average<70) {
            return Note.CB;
        } else if (average>=70 && average<75) {
            return Note.BB;
        } else if (average>=75 && average<80) {
            return Note.BA;
        }else {
            return Note.AA;
        }
    }
    private StudentInfo createDto(StudentInfoRequestWithoutTeacherId studentInfoRequest,
                                  Note note,Double average){
        return StudentInfo.builder()
                .infoNote(studentInfoRequest.getInfoNote())
                .absentee(studentInfoRequest.getAbsentee())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .examAverage(studentInfoRequest.getFinalExam())
                .letterGrade(note)
                .build();

    }
    private StudentInfoResponse createResponse(StudentInfo studentInfo){
        return StudentInfoResponse.builder()
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .id(studentInfo.getId())
                .absentee(studentInfo.getAbsentee())
                .mittermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .infoNote(studentInfo.getInfoNote())
                .Note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(createStudentResponse(studentInfo.getStudent()))
                .build();


    }
    public StudentResponse createStudentResponse(Student student){
        return StudentResponse.builder()
                .userId(student.getId())
                .username(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDay(student.getBirthDay())
                .birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }

    //Delete Mapping
    public ResponseMessage<?> deleteStudentInfo(Long studentInfoId) {

       if(!studentInfoRepository.existsByIdEquals(studentInfoId)) {
           throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND,studentInfoId));
       }

       studentInfoRepository.deleteById(studentInfoId);
       return ResponseMessage.builder()
               .message("Student Info deleted succesfully")
               .httpStatus(HttpStatus.OK)
               .build();
    }


    //Update
    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest studentInfoRequest, Long studentInfoId) {
        //gelmezse exception gelmez cunku diger method larda handle ettik.
        //Parametreden gelen  datalar ile nesneler elde ediliyor
       Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
       StudentInfo getStudentInfo= getStudentInfoById(studentInfoId);
       EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());

       //DersNot ortalamasi hesaplaniyor.
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());
       //Alfabetik Not belirlenecek
        Note note = checkLetterGrade(noteAverage);
        //DTO -->pojo
        StudentInfo studentInfo = createUpdateStudent(studentInfoRequest,studentInfoId,educationTerm,lesson,note,noteAverage);

        //Student ve Teacher nesneleri ekleniyor.
        studentInfo.setStudent(getStudentInfo.getStudent());
        studentInfo.setTeacher(getStudentInfo.getTeacher());

       //DB ye kayit islemi
      StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfo);


      //Response nesnesi ekleniyor

        return ResponseMessage.<StudentInfoResponse>builder()
                .message("Student Info Updated Succesfully")
                .httpStatus(HttpStatus.OK)
                .object(createResponse(updatedStudentInfo))
                .build();



    }
    public StudentInfo getStudentInfoById(Long studentInfoId){

        if(studentInfoRepository.existsByIdEquals(studentInfoId)){
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND,studentInfoId));

        }
        return studentInfoRepository.findByIdEquals(studentInfoId);

    }

    private StudentInfo createUpdateStudent(UpdateStudentInfoRequest studentInfoRequest,
                                            Long studentInfoRequestId,
                                            EducationTerm educationTerm,
                                            Lesson lesson,
                                            Note note,
                                            Double average){
        return StudentInfo.builder()
                .id(studentInfoRequestId)
                .infoNote(studentInfoRequest.getInfoNote())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .absentee(studentInfoRequest.getAbsentee())
                .lesson(lesson)
                .examAverage(average)
                .letterGrade(note)
                .build();
    }

    public Page<StudentInfoResponse> getAllForAdmin(Pageable pageable) {

        return studentInfoRepository.findAll(pageable).map(this::createResponse);
    }

    //getAllForTeacher()
    //allttaki metodu(student)  ya yoksa kontrolunu buraya ekleyiniz.Ancak ana kodu ekle
    public Page<StudentInfoResponse> getAllTeacher(Pageable pageable, String username) {
        return studentInfoRepository.findByTeacherId_UsernameEquals(username,pageable).map(this::createResponse);

    }

    public Page<StudentInfoResponse> getAllStudentInfoByStudent(String username, Pageable pageable) {

        boolean student = studentService.existByUsername(username);
        if(!student){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        }

        return studentInfoRepository.findByStudentId_UsernameEquals(username,pageable).map(this::createResponse);
    }


    //getStudentInfoByStudentId
    public List<StudentInfoResponse> getStudentInfoByStudentId(Long studentId) {

        if(!studentService.existById(studentId)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,studentId));
        }
        if(!studentInfoRepository.existsByStudent_IdEquals(studentId)){
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID,studentId));
        }

        return studentInfoRepository.findByStudent_IdEquals(studentId)
                .stream().map(this::createResponse)
                .collect(Collectors.toList());
    }

    //getStudentInfoById
    public StudentInfoResponse findStudentInfoById(Long id) {

        if(!studentInfoRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND,id));
        }
        return createResponse(studentInfoRepository.findByIdEquals(id));

    }


   //getAllWithPage odev




}
