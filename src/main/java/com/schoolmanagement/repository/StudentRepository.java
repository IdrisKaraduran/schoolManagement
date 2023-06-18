package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.Response.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Student findByUsernameEquals(String username);


    boolean existsByEmail(String email);

    @Query(value = "SELECT (count(s)>0) from Student s")//Student dan studentlari say sifirdan buyukse true kucukse false
    boolean findStudent();

    @Query(value = "Select max(s.studentNumber) from Student s")
    int getMaxStudentNumber();

    List<Student> getStudentByNameContaining(String studentName);

    Optional<Student> findByUsername(String username);


    @Query(value = "select s from Student s where s.advisorTeacher.teacher.username =:username")
    //@Query(value ="select s from Student s Join s.advisorTeacher at Join at.teacher t Where t.username = username")
    List<Student> getStudentByAdvisorTeacher_Username(String username);

    @Query(value = "SELECT s FROM Student s where s.id IN :id")
    List<Student> findByIdsEquals(Long[] id);
}
