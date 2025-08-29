package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.TimetableNotificationService;
import org.uni_bag.uni_bag_spring_boot_app.service.timetable.TimetableService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyTimeTableService {
    private final TimetableService timetableService;

    private final TimetableNotificationService timetableNotificationService;

    private final TimeTableRepository timeTableRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;

    public MyTimeTableListReadResponseDto getMyTimeTableList(User user) {
        List<TimeTable> foundTimeTables = timeTableRepository.findAllByUser(user);
        return MyTimeTableListReadResponseDto.fromEntity(foundTimeTables);
    }

    public MyEnrolledLectureReadResponseDto getMyEnrolledLecture(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable);

        return MyEnrolledLectureReadResponseDto.of(lectures);
    }

    public MyTimeTableReadResponseDto getMyTimeTableById(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = timetableService.getTimetableWithLectures(foundTimeTable);

        return MyTimeTableReadResponseDto.of(foundTimeTable, lecturesTimeMap);
    }

    public MyTimetableGetResponseDto getMyPrimaryTimeTable(User user) {
        TimeTable foundTimeTable = timeTableRepository.findByUserAndIsPrimary(user, true).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoPrimaryTimeTableError));

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = timetableService.getTimetableWithLectures(foundTimeTable);

        return MyTimetableGetResponseDto.of(foundTimeTable, lecturesTimeMap);
    }

    public MyTimeTableCreateResponseDto createMyTimeTable(User user, MyTimeTableCreateRequestDto requestDto) {
        Optional<TimeTable> timeTableOptional = timeTableRepository.findByUserAndAcademicYearAndSemester(user, requestDto.getYear(), requestDto.getSemester());
        if (timeTableOptional.isPresent()) {
            throw new HttpErrorException(HttpErrorCode.AlreadyExistSeasonTable);
        }

        TimeTable newTimeTable = timeTableRepository.save(TimeTable.of(requestDto.getYear(), requestDto.getSemester(), user, 0));
        return MyTimeTableCreateResponseDto.fromEntity(newTimeTable);
    }

    public MyTimeTableDeleteResponseDto deleteTimeTable(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));
        timeTableRepository.delete(foundTimeTable);

        return MyTimeTableDeleteResponseDto.of(foundTimeTable.getId());
    }

    public MyPrimaryTimeTableUpdateResponseDto updateMyPrimaryTimeTable(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        if (foundTimeTable.isPrimary()) {
            throw new HttpErrorException(HttpErrorCode.AlreadyPrimaryTimeTableError);
        }

        Optional<TimeTable> originalPrimaryTimeTableOptional = timeTableRepository.findByUserAndIsPrimary(user, true);
        originalPrimaryTimeTableOptional.ifPresent((timeTable) -> {
            timeTable.updatePrimary(false);
            timetableNotificationService.cancelNotification(timeTable);
        });

        foundTimeTable.updatePrimary(true);
        timetableNotificationService.scheduleNotification(foundTimeTable);

        return MyPrimaryTimeTableUpdateResponseDto.fromEntity(foundTimeTable);
    }

    public MyPrimaryTimeTableUpdateResponseDto deleteMyPrimaryTimeTable(User user) {
        TimeTable foundTimeTable = timeTableRepository.findByUserAndIsPrimary(user, true).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoPrimaryTimeTableError));
        foundTimeTable.updatePrimary(false);
        timetableNotificationService.cancelNotification(foundTimeTable);

        return MyPrimaryTimeTableUpdateResponseDto.fromEntity(foundTimeTable);
    }
}
