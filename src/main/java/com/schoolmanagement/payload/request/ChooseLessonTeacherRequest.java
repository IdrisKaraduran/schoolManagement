package com.schoolmanagement.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChooseLessonTeacherRequest {
    @Size(min = 1,message = "Lesson musst not be empty")
    @NotNull
    private Set<Long> lessonProgramId;
    @NotNull(message = "Please select teacher")
    private Long teacherId;


}
