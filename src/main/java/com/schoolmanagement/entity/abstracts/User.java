package com.schoolmanagement.entity.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass//Db de User tablosu olusmadan bu sinifin anac sinif olarak kullanilmasini sagliyor.
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder//Bir admin ayni zamanda bir userdir. Alt siniflarin user sinifinin ozelliklerini kullanabilmesine izin verir.
public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn;

    private  String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    private String birthPlace;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//Hassas veri odugu icin okuma islemlerine kullanilmasin
    private String password;
    @Column(unique = true)
    private String phoneNumber;
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//Hassas veri odugu icin okuma islemlerine kullanilmasin
    private UserRole userRole;

    private Gender gender;




}
