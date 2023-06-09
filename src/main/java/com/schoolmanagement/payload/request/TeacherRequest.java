package com.schoolmanagement.payload.request;

import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {
    @NotNull(message = "Please Select Lesson")
    private Set<Long> lessonsIdList;

    @NotNull(message = "Please Select IsAdvisor Teacher")
    private boolean isAdvisorTeacher;//Bilerek kucuk yazdik buyuk yazinca ve is ile basladigi icin getter methodu iyi calismiyor.
    @NotNull(message = "Please enter your email")
    @Email(message = "Please enter valid email")
    @Size(min = 5,max = 50,message = "Your email should be between 5 and 50 chars")
    private String email;

}
