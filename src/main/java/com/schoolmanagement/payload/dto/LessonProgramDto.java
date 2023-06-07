package com.schoolmanagement.payload.dto;

import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
public class LessonProgramDto {

    //Dto -pojo donusumu
    public LessonProgram dtoLessonProgram(LessonProgramRequest lessonProgramRequest, Set<Lesson> lessons){
        return LessonProgram.builder()
                .startTime(lessonProgramRequest.getStartTime())
                .stopTime(lessonProgramRequest.getStopTime())
                .day(lessonProgramRequest.getDay())
                .lesson(lessons)
                .build();
    }



}
