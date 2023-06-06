package com.schoolmanagement.payload.Response.abstracts;

import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@Data
@NoArgsConstructor
//@MappedSuperclass Bu annotation gereksiz oldugu icin yoruma aldik.
@SuperBuilder
@AllArgsConstructor
public abstract class BaseUserResponse {
    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String ssn;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;



}
