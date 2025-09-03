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
    @Schema(example = "26", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "전공", description = "교과과정")
    private String curriculum;

    @Schema(example = "기초", description = "영역")
    private String area;

    @Schema(example = "1학년", description = "대상학년")
    private String targetGrade;

    @Schema(example = "BUC10222-01", description = "학수번호")
    private String courseCode;

    @Schema(example = "불교신행과문화", description = "교과목명")
    private String courseName;

    @Schema(example = "홍길동", description = "담당교원")
    private String instructor;

    @Schema(example = "D403", description = "강의실")
    private String classroom;

    @Schema(example = "", description = "학점")
    private Float credits;

    @Schema(example = "3.0", description = "이론")
    private Float theory;

    @Schema(example = "0.0", description = "실습")
    private Float practical;

    @Schema(example = "???", description = "공학인증")
    private String engineeringAccreditation;

    @Schema(example = "이론", description = "강의유형")
    private String courseType;

    @Schema(example = "일반강의", description = "강의형태")
    private String courseFormat;

    @Schema(example = "상대평가", description = "성적평가방법")
    private String evaluationMethod;

    @Schema(example = "GRADE", description = "성적등급유형")
    private String gradeType;

    @Schema(example = "전공", description = "이수구분")
    private String completionType;

    @Schema(example = "불교문화대학-불교학부-불교문화콘텐츠전공", description = "개설대학-학과-전공")
    private String offeringMajor;

    @Schema(example = "팀티칭", description = "팀티칭")
    private String teamTeaching;

    @Schema(example = "타전공생 수강제한", description = "비고")
    private String remarks;

    @Schema(example = "2024", description = "년도")
    private int year;

    @Schema(example = "3", description = "학기")
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
                .offeringMajor(lecture.getOfferingCollege() + "-" + lecture.getOfferingDepartment() + "-" +  lecture.getOfferingMajor())
                .teamTeaching(lecture.getTeamTeaching())
                .remarks(lecture.getRemarks())
                .year(lecture.getAcademicYear())
                .semester(lecture.getSemester())
                .theory(lecture.getTheory())
                .practical(lecture.getPractical())
                .classroom(lecture.getClassroom())
                .credits(lecture.getCredits())
                .engineeringAccreditation(lecture.getEngineeringAccreditation())
                .teamTeaching(lecture.getTeamTeaching())
                .remarks(lecture.getRemarks())
                .year(lecture.getAcademicYear())
                .semester(lecture.getSemester())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class FoundLectureTimeInfo {
    @Schema(example = "수", description = "강의 요일")
    private String dayOfWeek;

    @Schema(example = "09:00:00", description = "강의 시작시간")
    private Time startTime;

    @Schema(example = "12:00:00", description = "강의 종료시간")
    private Time endTime;

    public static FoundLectureTimeInfo from(DgLectureTime lectureTime) {
        return FoundLectureTimeInfo.builder()
                .dayOfWeek(lectureTime.getDayOfWeek())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}