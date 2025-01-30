package org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.LectureDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class FriendTimeTableReadResponseDto {
    @Schema(example = "1", description = "친구 아이디")
    private Long friendId;

    @Schema(example = "홍길동", description = "친구 이름")
    private String friendName;

    @Schema(example = "1", description = "시간표 아이디")
    private Long tableId;

    List<LectureDto> lectures;

    public static FriendTimeTableReadResponseDto of(User friend, TimeTable timeTable, Map<DgLecture, LectureTimeColor> lectureTimeMap) {
        return FriendTimeTableReadResponseDto.builder()
                .friendId(friend.getId())
                .friendName(friend.getName())
                .tableId(timeTable.getId())
                .lectures(lectureTimeMap.entrySet().stream()
                        .map(dgLectureListEntry -> LectureDto.of(dgLectureListEntry.getKey(), dgLectureListEntry.getValue()))
                        .toList()
                )
                .build();
    }
}

