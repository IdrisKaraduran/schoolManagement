package com.schoolmanagement.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateMeetRequest {
    @NotNull(message = "Please enter description")
    @Size(min = 6,max = 250,message = "Your description should be at least 6 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Description must consist of the characters .")
    private String description;

    @NotNull(message = "Please enter date")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Please enter startTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "HH:mm",timezone = "US")
    private LocalTime startTime;

    @NotNull(message = "Please enter stopTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "HH:mm",timezone = "US")
    private LocalTime stopTime;

    @NotNull(message = "Please select students")
    private Long[] studentIds;


}
