package org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.*;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;
import org.uni_bag.uni_bag_spring_boot_app.service.timetable.TimetableService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendTimeTableService {
    private final TimetableService timetableService;

    private final UserRepository userRepository;
    private final TimeTableRepository timeTableRepository;
    private final FollowRepository followRepository;

    public FriendTimeTableListReadResponseDto getFriendTimeTableList(User follower, Long followeeId) {
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        List<TimeTable> foundTimeTables = timeTableRepository.findAllByUser(followee);
        return FriendTimeTableListReadResponseDto.fromEntity(followee, foundTimeTables);
    }

    public FriendTimeTableReadResponseDto getFriendTimeTableById(User follower, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findById(timeTableId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        if(!followRepository.existsByFollowerAndFollowee(follower, foundTimeTable.getUser())) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = timetableService.getTimetableWithLectures(foundTimeTable);

        return FriendTimeTableReadResponseDto.of(foundTimeTable.getUser(), foundTimeTable, lecturesTimeMap);
    }

    public FriendTimeTableReadResponseDto getFriendPrimaryTimeTable(User follower, Long followeeId) {
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        TimeTable foundTimeTable = timeTableRepository.findByUserAndIsPrimary(followee, true).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoPrimaryTimeTableError));

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = timetableService.getTimetableWithLectures(foundTimeTable);

        return FriendTimeTableReadResponseDto.of(foundTimeTable.getUser(), foundTimeTable, lecturesTimeMap);
    }

    public FriendTimeTableReadResponseDto getSecondaryFriendTimeTable(User user) {
        Follow secondaryFriend = followRepository.findByFollowerAndIsSecondaryFriend(user, true)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSecondaryFriendError));

        TimeTable foundTimeTable = timeTableRepository.findByUserAndIsPrimary(secondaryFriend.getFollowee(), true).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoPrimaryTimeTableError));

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = timetableService.getTimetableWithLectures(foundTimeTable);

        return FriendTimeTableReadResponseDto.of(foundTimeTable.getUser(), foundTimeTable, lecturesTimeMap);
    }
}
