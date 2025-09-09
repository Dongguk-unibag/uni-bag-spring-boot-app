package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class DgLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String curriculum;

    private String area;

    private String targetGrade;

    private String courseCode;

    private String courseName;

    private String instructor;

    private String classroom;

    private Float credits;

    private Float theory;

    private Float practical;

    private String engineeringAccreditation;

    private String courseType;

    private String courseFormat;

    private String evaluationMethod;

    private String gradeType;

    private String completionType;

    private String offeringCollege;

    private String offeringDepartment;

    private String offeringMajor;

    private String teamTeaching;

    private String remarks;

    private int academicYear;

    private int semester;

    @OneToMany(mappedBy = "dgLecture")
    private List<DgLectureTime> dgLectureTimes;
}