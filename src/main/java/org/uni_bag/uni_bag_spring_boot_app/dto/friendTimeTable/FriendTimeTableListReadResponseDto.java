package org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class FriendTimeTableListReadResponseDto {
    private Long friendId;
    private String friendName;
    private List<TimeTableInfo> timeTables;

    public static FriendTimeTableListReadResponseDto fromEntity(User friend, List<TimeTable> timeTables){
        return FriendTimeTableListReadResponseDto.builder()
                .friendId(friend.getId())
                .friendName(friend.getName())
                .timeTables(timeTables.stream().map(TimeTableInfo::of).toList())
                .build();
    }

}

@Getter
@AllArgsConstructor
@Builder
class TimeTableInfo{
    @Schema(example = "1", description = "시간표 아이디")
    private Long timeTableId;

    @Schema(example = "2024", description = "년도")
    private int year;

    @Schema(example = "3", description = "학기(1: 봄 학기, 2: 여름 계절학기, 3: 가을 학기, 4: 겨울 계절학기)")
    private int semester;

    public static TimeTableInfo of(TimeTable timeTable){
        return TimeTableInfo.builder()
                .timeTableId(timeTable.getId())
                .year(timeTable.getYear())
                .semester(timeTable.getSemester())
                .build();
    }
}
