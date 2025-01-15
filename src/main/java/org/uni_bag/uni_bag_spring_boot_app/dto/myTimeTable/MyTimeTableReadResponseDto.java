package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableReadResponseDto {
    private Long tableId;
    List<MyTimeTableLecture> lectures;

    public static MyTimeTableReadResponseDto of(TimeTable timeTable, Map<DgLecture, List<DgLectureTime>> lectureTimeMap) {
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
    private Long lectureId;
    private String lectureName;
    private String instructorName;
    private String classRoom;
    private String lectureFormat;
    List<LectureTime> lectureTimes;

    public static MyTimeTableLecture of(DgLecture lecture, List<DgLectureTime> lectureTimes) {
        return MyTimeTableLecture.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getCourseName())
                .instructorName(lecture.getInstructor())
                .classRoom(lecture.getClassroom())
                .lectureFormat(lecture.getCourseFormat())
                .lectureTimes(lectureTimes.stream().map(LectureTime::of).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class LectureTime{
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;

    public static LectureTime of(DgLectureTime lectureTime) {
        return LectureTime.builder()
                .dayOfWeek(lectureTime.getDayOfWeek())
                .startTime(lectureTime.getStartTime())
                .endTime(lectureTime.getEndTime())
                .build();
    }
}
