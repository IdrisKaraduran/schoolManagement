package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.payload.Response.TeacherResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Teacher findByUsernameEquals(String username);

    boolean existsByEmail(String email);
    //("select t from teacher t where t.name like concat('%',?1,'%')")

    List<Teacher> getTeacherByNameContaining(String teacherName);

    Teacher getTeacherByUsername(String username);
}
