package com.schoolmanagement.payload.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//jSON ICINDE null olanlarin gozukmemesini sagliyoruz.
public class ResponseMessage<E>{
    private E object;
    private String message;
    private HttpStatus httpStatus;


}
