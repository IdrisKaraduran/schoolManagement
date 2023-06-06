package com.schoolmanagement.payload.request.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@SuperBuilder//Db de tablo olustururken child class larin bu class daki field lari kullanabilmesini sagliyor.
@MappedSuperclass//Db de tablo olusturmadan anac bir sinif olusturmayi saglar.Bu class db ye kaydedilmeyecek ama kaydedilecek olan siniflarin ana class i
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseUserRequest implements Serializable {

    //Validation lari yazmazsak bir problem yasar miyim
    //Cleint tarafindan gelecek ve db ye gidip kaydolacak bu yuzden
    //kod patlamasin diye validation yapmak zorundayiz.
    @NotNull(message = "Please enter your username")
    @Size(min=4, max=16, message = "Your username should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your username must consist of the characters .")
    private String username;


    @NotNull(message = "Please enter your name")
    @Size(min=2, max=16, message = "Your name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your name must consist of the characters .")
    private String name;

    @NotNull(message = "Please enter your surname")
    @Size(min=4, max=16, message = "Your surname should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your surname must consist of the characters .")
    private String surname;


    @NotNull(message = "Please enter your birtday")
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")//Formatli hali
    @Past//Bu tarihin gecmis bir tarihe ait olup olmadigini kontrol etmek icin yazdik.
    private LocalDate birthDay;

    @NotNull//SSn pattern yaz
    @Pattern(regexp = "^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$",
            message = "Please enter valid SSN number")
    private String ssn;//Tc kimlik numarasi


    @NotNull(message = "Please Enter Your BirthPlace")
    @Size(min=2, max=16, message = "Your birthplace should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your surname must consist of the characters .")
    private String birthPlace;

    @NotNull(message = "Please Enter Your Password")
    @Size(min=8, max=60, message = "Your password should be at least 8 chars")
   // @Column(nullable = false,length = 60)
    private  String password;

    @NotNull(message = "Please Enter Your BirthPlace")
    @Size(min=12, max=12, message = "Your Phone number should be 12 chars")
    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    private String phoneNumber;

    @NotNull(message = "Please enter your gender")
    private Gender gender;



}