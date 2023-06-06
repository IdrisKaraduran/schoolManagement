package com.schoolmanagement.payload.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageResponse implements Serializable {

    //Db ye gidecek seylere validation yapilir .Db den gelene yapilmaz.Yapilsa bir sey olmaz

    private String name;
    private  String email;
    private String subject;
    private String message;
    private LocalDate date;

}
