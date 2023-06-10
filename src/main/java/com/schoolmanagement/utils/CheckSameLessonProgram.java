package com.schoolmanagement.utils;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.exception.BadRequestException;

import java.util.HashSet;
import java.util.Set;

public class CheckSameLessonProgram {

    public static void checkLessonPrograms(Set<LessonProgram> existLessonProgram, Set<LessonProgram> lessonProgramRequest){

        if(existLessonProgram.isEmpty() && lessonProgramRequest.size()>1){//karsilastirma yapmak icin birden buyuk
            checkDuplicaticateLessonProgram(lessonProgramRequest);
        }else {
            checkDuplicaticateLessonProgram(lessonProgramRequest);
            checkDuplicateLessonsProgram(existLessonProgram, lessonProgramRequest);
        }

    }

    private static void checkDuplicaticateLessonProgram(Set<LessonProgram> lessonPrograms) {
        Set<String> uniqueLessonProgramKeys = new HashSet<>();
        for(LessonProgram lessonProgram : lessonPrograms){
            String lessonProgramKey = lessonProgram.getDay().name() + lessonProgram.getStartTime();
            if(uniqueLessonProgramKeys.contains(lessonProgramKey)){//contain true veya false doner
                throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
            }
            uniqueLessonProgramKeys.add(lessonProgramKey);
        }

    }
    private static void checkDuplicateLessonsProgram(Set<LessonProgram> existLessonProgram, Set<LessonProgram> lessonProgramRequest){
        for(LessonProgram requestLessonProgram : lessonProgramRequest){

            if(existLessonProgram.stream().anyMatch(lessonProgram ->
                    lessonProgram.getStartTime().equals(requestLessonProgram.getStartTime()) &&
                    lessonProgram.getDay().name().equals(requestLessonProgram.getDay().name())))
            {
                throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
            }
        }
    }
    //TODO start Time baska bir Lesson Programin Start Time ve End Time arasinda mi kontrolu eklenecek.




























}
