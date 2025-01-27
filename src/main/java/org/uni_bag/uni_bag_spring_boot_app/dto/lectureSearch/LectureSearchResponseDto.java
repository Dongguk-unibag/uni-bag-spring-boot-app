package org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

import java.sql.Time;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class LectureSearchResponseDto {
    List<FoundLectureWithTimesDto> lectures;

    public static LectureSearchResponseDto from(List<DgLecture> lectures) {
        return LectureSearchResponseDto.builder()
                .lectures(lectures.stream().map(FoundLectureWithTimesDto::from).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class FoundLectureWithTimesDto {
    private FoundLectureInfo lecture;
    private List<FoundLectureTimeInfo> lectureTimes;

    public static FoundLectureWithTimesDto from(DgLecture lecture) {
        return FoundLectureWithTimesDto.builder()
                .lecture(FoundLectureInfo.from(lecture))
                .lectureTimes(lecture.getDgLectureTimes().stream().map(FoundLectureTimeInfo::from).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class FoundLectureInfo {
    @Schema(description = "강의 아이디")
    private Long lectureId;

    @Schema(description = "교과과정")
    private String curriculum;

    @Schema(description = "영역")
    private String area;

    @Schema(description = "대상학년")
    private String targetGrade;

    @Schema(description = "학수번호")
    private String courseCode;

    @Schema(description = "교과목명")
    private String courseName;

    @Schema(description = "담당교원")
    private String instructor;

    @Schema(description = "요일/교시")
    private String dayPeriod;

    @Schema(description = "강의실")
    private String classroom;

    @Schema(description = "학점")
    private Float credits;

    @Schema(description = "이론")
    private Float theory;

    @Schema(description = "실습")
    private Float practical;

    @Schema(description = "공학인증")
    private String engineeringAccreditation;

    @Schema(description = "강의유형")
    private String courseType;

    @Schema(description = "강의형태")
    private String courseFormat;

    @Schema(description = "성적평가방법")
    private String evaluationMethod;

    @Schema(description = "성적등급유형")
    private String gradeType;

    @Schema(description = "이수구분")
    private String completionType;

    @Schema(description = "개설대학")
    private String offeringCollege;

    @Schema(description = "개설학과")
    private String offeringDepartment;

    @Schema(description = "개설전공")
    private String offeringMajor;

    @Schema(description = "팀티칭")
    private String teamTeaching;

    @Schema(description = "비고")
    private String remarks;

    @Schema(description = "년도")
    private int year;

    @Schema(description = "학기")
    private int semester;

    public static FoundLectureInfo from(DgLecture lecture) {
        return FoundLectureInfo.builder()
                .lectureId(lecture.getId())
                .curriculum(lecture.getCurriculum())
                .area(lecture.getArea())
                .targetGrade(lecture.getTargetGrade())
                .courseCode(lecture.getCourseCode())
                .courseName(lecture.getCourseName())
                .instructor(lecture.getInstructor())
                .courseType(lecture.getCourseType())
                .courseFormat(lecture.getCourseFormat())
                .evaluationMethod(lecture.getEvaluationMethod())
                .gradeType(lecture.getGradeType())
                .completionType(lecture.getCompletionType())
                .offeringCollege(lecture.getOfferingCollege())
                .offeringDepartment(lecture.getOfferingDepartment())
                .offeringMajor(lecture.getOfferingMajor())
                .teamTeaching(lecture.getTeamTeaching())
                .remarks(lecture.getRemarks())
                .year(lecture.getYear())
                .semester(lecture.getSemester())
                .theory(lecture.getTheory())
                .practical(lecture.getPractical())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class FoundLectureTimeInfo {
    private String dayOfWeek;

    private Time startTime;

    private Time endTime;

    public static FoundLectureTimeInfo from(DgLectureTime lectureTime) {
        return FoundLectureTimeInfo.builder()
                .dayOfWeek(lectureTime.getDayOfWeek())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}