package org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyTimeTableScheduleCreateResponseDto {
    private Long timeTableId;

    private List<NewLecture> lectures;

    public static MyTimeTableScheduleCreateResponseDto fromEntity(TimeTable timeTable, List<DgLecture> lectures){
        return MyTimeTableScheduleCreateResponseDto.builder()
                .timeTableId(timeTable.getId())
                .lectures(lectures.stream().map(dgLecture -> NewLecture.of(dgLecture.getId(), dgLecture.getCourseName())).toList())
                .build();
    }
}

@Getter
@AllArgsConstructor
@Builder
class NewLecture {
    private Long lectureId;

    private String lectureName;

    public static NewLecture of(Long lectureId, String lectureName){
        return NewLecture.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .build();
    }
}
