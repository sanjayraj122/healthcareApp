package com.login_logout.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "diagnosis")
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long diagnosisId;

    private String conditionCodes;
    private String status;
    private String category;
    private String severity;
    private String bodySite;
    private Date onset;
    private Date abatement;
    private String stage;
    private String evidence;
    private String note;
    private long did;
    private long pid;

}
