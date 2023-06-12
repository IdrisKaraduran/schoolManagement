package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
