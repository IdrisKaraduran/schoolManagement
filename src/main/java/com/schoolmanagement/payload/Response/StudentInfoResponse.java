package com.schoolmanagement.payload.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public class StudentInfoResponse {

    private Long id;
    private Double mittermExam;
    private Double finalExam;
    private Integer absentee;
    private String infoNote;
    private String lessonName;
    private int creditScore;
    private boolean isCompulsory;
    private Term educationTerm;
    private Double average;
    private Note Note;
    private StudentResponse studentResponse;



}
