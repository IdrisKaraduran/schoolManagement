package com.schoolmanagement.entity.concretes;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvisorTeacher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    //Teacher iliskisi
    @OneToOne
    private Teacher teacher;

    //Student iliskisi
    @OneToMany(mappedBy = "advisorTeacher",orphanRemoval = true,cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
    private List<Student> students;


    //Meet iliskisi
    @OneToMany(mappedBy = "advisorTeacher",cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Meet> meet;







}
