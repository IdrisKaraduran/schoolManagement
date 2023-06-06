package com.schoolmanagement.entity.concretes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data//Getter ve setter ve equals gibi metodlari getiriyor
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)//Var olan nesnin kopyasini alarak degisiklik yapmasini sagliyor.
//Yeni bir nesne olusturmak yerine var olan nesnenin kopyasini alarak degisiklik yapmamizi saglar.
public class ContactMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String subject;
    @NotNull
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")//Gelen bilgiden gereksizleri gormemek icin yazdik
    private LocalDate date;



}
