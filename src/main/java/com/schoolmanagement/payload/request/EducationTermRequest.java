package com.schoolmanagement.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagement.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EducationTermRequest implements Serializable {
    @NotNull(message = "Education muss not be empty")
    private Term term;
    @NotNull(message = "Start Date musst not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull(message = "End Date musst not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @NotNull(message = "Last Registration Date musst not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate lastRegistrationDate;



}