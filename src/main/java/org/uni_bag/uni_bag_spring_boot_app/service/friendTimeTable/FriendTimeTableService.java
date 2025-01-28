package org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendTimeTableService {
    private final UserRepository userRepository;
    private final TimeTableRepository timeTableRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;
    private final DgLectureTimeRepository dgLectureTimeRepository;
    private final FollowRepository followRepository;

    public FriendTimeTableReadResponseDto getFriendTimeTable(User follower, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findById(timeTableId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        if(!followRepository.existsByFollowerAndFollowee(follower, foundTimeTable.getUser())) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable);

        Map<DgLecture, List<DgLectureTime>> lecturesTimeMap = new HashMap<>();

        for (TimeTableLecture timeTableLecture : lectures) {
            lecturesTimeMap.put(
                    timeTableLecture.getLecture(),
                    dgLectureTimeRepository.findAllByDgLecture(timeTableLecture.getLecture())
            );
        }

        return FriendTimeTableReadResponseDto.of(foundTimeTable.getUser(), foundTimeTable, lecturesTimeMap);
    }

    public FriendTimeTableListReadResponseDto getFriendTimeTableList(User follower, Long followeeId) {
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        List<TimeTable> foundTimeTables = timeTableRepository.findAllByUser(followee);
        return FriendTimeTableListReadResponseDto.fromEntity(followee, foundTimeTables);
    }

    public FriendTimeTableReadResponseDto getFriendPrimaryTimeTable(User follower, Long followeeId) {
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new HttpErrorException(HttpErrorCode.AccessDeniedError);
        }

        TimeTable foundTimeTable = timeTableRepository.findByUserAndTableOrder(followee, 1).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoPrimaryTimeTableError));

        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable);

        Map<DgLecture, List<DgLectureTime>> lecturesTimeMap = new HashMap<>();

        for (TimeTableLecture timeTableLecture : lectures) {
            lecturesTimeMap.put(
                    timeTableLecture.getLecture(),
                    dgLectureTimeRepository.findAllByDgLecture(timeTableLecture.getLecture())
            );
        }

        return FriendTimeTableReadResponseDto.of(foundTimeTable.getUser(), foundTimeTable, lecturesTimeMap);
    }
}
