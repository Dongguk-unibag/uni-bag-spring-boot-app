package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MyTimeTableScheduleService {
    private final TimeTableRepository timeTableRepository;
    private final DgLectureRepository dgLectureRepository;
    private final DgLectureTimeRepository dgLectureTimeRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;

    public MyTimeTableScheduleCreateResponseDto createMyTimeTableSchedule(User user, @Valid MyTimeTableScheduleCreateRequestDto requestDto) {
        List<DgLectureTime> candidateLectureTimes = new ArrayList<>();   // 새로 추가하려는 강의 시간 후보들
        List<LectureColor> candidateLectures = new ArrayList<>();        // 새로 추가하려는 강의들

        TimeTable timeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        // 요청에서 넘어온 강의들 조회 및 검증
        for (NewLectureScheduleDto scheduleDto : requestDto.getNewLectureSchedules()) {
            DgLecture lecture = dgLectureRepository.findById(scheduleDto.getLectureId())
                    .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));

            candidateLectures.add(new LectureColor(lecture, scheduleDto.getLectureColor()));

            if (timeTable.getAcademicYear() != lecture.getAcademicYear()
                    || timeTable.getSemester() != lecture.getSemester()) {
                throw new HttpErrorException(HttpErrorCode.SemesterMismatchError);
            }

            candidateLectureTimes.addAll(dgLectureTimeRepository.findAllByDgLecture(lecture));
        }

        List<DgLecture> existingLectures = timeTableLectureRepository.findAllByTimeTable(timeTable)
                .stream().map(TimeTableLecture::getLecture).toList();

        List<DgLectureTime> existingLectureTimes = new ArrayList<>(
                existingLectures.stream()
                        .flatMap(lec -> dgLectureTimeRepository.findAllByDgLecture(lec).stream())
                        .toList()
        );

        checkLectureDuplicated(existingLectures, candidateLectures);

        while (!candidateLectureTimes.isEmpty()) {
            checkLecturesOverlapped(existingLectureTimes, candidateLectureTimes.get(0));
            existingLectureTimes.add(candidateLectureTimes.get(0));
            candidateLectureTimes.remove(0);
        }

        candidateLectures.forEach(lectureColor ->
                timeTableLectureRepository.save(TimeTableLecture.of(timeTable, lectureColor))
        );

        return MyTimeTableScheduleCreateResponseDto.fromEntity(timeTable, candidateLectures);
    }


    public MyTimeTableScheduleCreateResponseDto createNdrimsTimeTableSchedule(User user, NdrimsTimeTableScheduleCreateRequestDto requestDto) {
        List<LectureColor> newLectures = new ArrayList<>();

        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        int year = foundTimeTable.getAcademicYear();
        int semester = foundTimeTable.getSemester();

        // 기존 강의 일정 모두 삭제
        timeTableLectureRepository.deleteAllByTimeTable(foundTimeTable);

        // 새롭게 추가할 강의와 강의 시간에 대한 엔티티 조회
        for (NewNdrimsLectureSchedule newLectureScheduleDto : requestDto.getNewLectureSchedules()) {
            DgLecture foundLecture = dgLectureRepository.findByCourseCodeAndAcademicYearAndSemester(newLectureScheduleDto.getSbjNo(), year, semester).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));
            newLectures.add(new LectureColor(foundLecture, newLectureScheduleDto.getLectureColor()));
        }

        newLectures.forEach(lectureColor -> timeTableLectureRepository.save(TimeTableLecture.of(foundTimeTable, lectureColor)));

        return MyTimeTableScheduleCreateResponseDto.fromEntity(foundTimeTable, newLectures);
    }

    public MyTimeTableScheduleDeleteResponseDto deleteMyTimeTableSchedule(User user, MyTimeTableScheduleDeleteRequestDto requestDto) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        DgLecture deleteTargetLecture = dgLectureRepository.findById(requestDto.getLectureId()).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));

        TimeTableLecture foundTimeTableLecture = timeTableLectureRepository.findByTimeTableAndLecture(foundTimeTable, deleteTargetLecture)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableScheduleError));

        timeTableLectureRepository.delete(foundTimeTableLecture);

        return MyTimeTableScheduleDeleteResponseDto.of(foundTimeTable, deleteTargetLecture);
    }

    private void checkLecturesOverlapped(List<DgLectureTime> existingLectureTimes, DgLectureTime newLectureTime) {
        for (DgLectureTime lectureTime : existingLectureTimes) {
            if (lectureTime.getDayOfWeek().equals(newLectureTime.getDayOfWeek())
                    && lectureTime.getStartTime().before(newLectureTime.getEndTime())
                    && newLectureTime.getStartTime().before(lectureTime.getEndTime())) {
                throw new HttpErrorException(HttpErrorCode.OverLappingLectureError);
            }
        }
    }

    private void checkLectureDuplicated(List<DgLecture> existingLectures, List<LectureColor> newLectures) {
        for (LectureColor newLecture : newLectures) {
            for (DgLecture existingLecture : existingLectures) {
                if (newLecture.getLecture().equals(existingLecture)) {
                    throw new HttpErrorException(HttpErrorCode.AlreadyExistLectureScheduleError);
                }
            }
        }
    }
}
