package com.vmware.training.courseservice;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//@Builder
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Entity
@ToString
public class Course {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private int duration;

}
