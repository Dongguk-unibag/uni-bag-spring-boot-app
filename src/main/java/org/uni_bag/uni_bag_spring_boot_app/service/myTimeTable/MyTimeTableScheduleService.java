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
        List<DgLectureTime> newLectureTimes = new ArrayList<>();
        List<LectureColor> newLectures = new ArrayList<>();

        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        // 새롭게 추가할 강의와 강의 시간에 대한 엔티티 조회
        for (NewLectureScheduleDto newLectureScheduleDto : requestDto.getNewLectureSchedules()) {
            DgLecture foundLecture = dgLectureRepository.findById(newLectureScheduleDto.getLectureId()).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));
            newLectures.add(new LectureColor(foundLecture, newLectureScheduleDto.getLectureColor()));

            if(foundTimeTable.getYear() != foundLecture.getYear() || foundTimeTable.getSemester() != foundLecture.getSemester()){
                throw new HttpErrorException(HttpErrorCode.SemesterMismatchError);
            }

            newLectureTimes.addAll(dgLectureTimeRepository.findAllByDgLecture(foundLecture));
        }

        List<DgLecture> existingLectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable).stream().map(TimeTableLecture::getLecture).toList();
        List<DgLectureTime> existingLectureTimes = new ArrayList<>(existingLectures.stream().flatMap(dgLecture -> dgLectureTimeRepository.findAllByDgLecture(dgLecture).stream()).toList());

        checkLectureDuplicated(existingLectures, newLectures);

        while (!newLectureTimes.isEmpty()) {
            checkLecturesOverlapped(existingLectureTimes, newLectureTimes.get(0));
            existingLectureTimes.add(newLectureTimes.get(0));
            newLectureTimes.remove(0);
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

    private void checkLecturesOverlapped(List<DgLectureTime> existingLectureTimes, DgLectureTime newLectureTime){
        for(DgLectureTime lectureTime : existingLectureTimes){
            if(lectureTime.getDayOfWeek().equals(newLectureTime.getDayOfWeek())
                    && lectureTime.getStartTime().before(newLectureTime.getEndTime())
                            && newLectureTime.getStartTime().before(lectureTime.getEndTime())){
                throw new HttpErrorException(HttpErrorCode.OverLappingLectureError);
            }
        }
    }

    private void checkLectureDuplicated(List<DgLecture> existingLectures, List<LectureColor> newLectures){
        for(LectureColor newLecture : newLectures){
            for(DgLecture existingLecture : existingLectures){
                if(newLecture.getLecture().equals(existingLecture)){
                    throw new HttpErrorException(HttpErrorCode.AlreadyExistLectureScheduleError);
                }
            }
        }
    }
}
