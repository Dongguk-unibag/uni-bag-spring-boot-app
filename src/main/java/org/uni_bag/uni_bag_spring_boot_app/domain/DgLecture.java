package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class DgLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // 아이디를 기준으로 두개의 엔티티가 서로 같은지 비교
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

    private int year;

    private int semester;
}