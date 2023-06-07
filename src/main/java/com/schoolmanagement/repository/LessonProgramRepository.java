package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.payload.Response.LessonProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LessonProgramRepository extends JpaRepository<LessonProgram,Long> {

    List<LessonProgram> findByTeaachers_IdNull();

    List<LessonProgram> findByTeachers_IdNotNull();

    @Query("Select l from LessonProgram l inner join l.teachers teachers where teachers.username =?1")
    Set<LessonProgram> getLessonProgramByTeacherUsername(String username);
}
