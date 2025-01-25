package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableReadResponseDto {
    @Schema(example = "1", description = "시간표 아이디")
    private Long tableId;

    List<MyTimeTableLecture> lectures;

    public static MyTimeTableReadResponseDto of(TimeTable timeTable, Map<DgLecture, LectureTimeColor> lectureTimeMap) {
        return MyTimeTableReadResponseDto.builder()
                .tableId(timeTable.getId())
                .lectures(lectureTimeMap.entrySet().stream()
                        .map(dgLectureListEntry -> MyTimeTableLecture.of(dgLectureListEntry.getKey(), dgLectureListEntry.getValue()))
                        .toList()
                )
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class MyTimeTableLecture {
    @Schema(example = "7", description = "강의 아이디")
    private Long lectureId;

    @Schema(example = "알고리즘", description = "강의 이름")
    private String lectureName;

    @Schema(example = "홍길동", description = "교수 이름")
    private String instructorName;

    @Schema(example = "S02", description = "강의실 이름")
    private String classRoom;

    @Schema(example = "일반 강의", description = "강의 형태")
    private String lectureFormat;

    @Schema(example = "#ffffff", description = "강의 색상코드")
    private String lectureColor;

    private List<LectureTime> lectureTimes;

    public static MyTimeTableLecture of(DgLecture lecture, LectureTimeColor lectureTimes) {
        return MyTimeTableLecture.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getCourseName())
                .instructorName(lecture.getInstructor())
                .classRoom(lecture.getClassroom())
                .lectureFormat(lecture.getCourseFormat())
                .lectureColor(lectureTimes.getLectureColor())
                .lectureTimes(lectureTimes.getDgLectureTime().stream().map(LectureTime::of).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class LectureTime{
    @Schema(example = "목", description = "강의 요일")
    private String dayOfWeek;

    @Schema(example = "13:00:00", description = "강의 시작시간")
    private Time startTime;

    @Schema(example = "17:00:00", description = "강의 종료시간")
    private Time endTime;

    public static LectureTime of(DgLectureTime lectureTime) {
        return LectureTime.builder()
                .dayOfWeek(lectureTime.getDayOfWeek())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}
