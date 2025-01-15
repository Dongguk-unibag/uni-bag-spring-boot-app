package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;

import java.util.ArrayList;
import java.util.List;

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
        List<DgLecture> newLectures = new ArrayList<>();

        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        // 새롭게 추가할 강의와 강의 시간에 대한 엔티티 조회
        for (Long lectureId : requestDto.getLectureIds()) {
            DgLecture foundLecture = dgLectureRepository.findById(lectureId).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));
            newLectures.add(foundLecture);

            if(foundTimeTable.getYear() != foundLecture.getYear() || foundTimeTable.getSemester() != foundLecture.getSemester()){
                throw new HttpErrorException(HttpErrorCode.SemesterMismatchException);
            }

            newLectureTimes.addAll(dgLectureTimeRepository.findAllByDgLecture(foundLecture));
        }

        List<DgLectureTime> existingLectureTimes = new ArrayList<>(timeTableLectureRepository.findAllByTimeTable(foundTimeTable).stream()
                .flatMap(timeTableLecture -> dgLectureTimeRepository.findAllByDgLecture(timeTableLecture.getLecture()).stream())
                .toList());

        while (!newLectureTimes.isEmpty()) {
            boolean isOverLapping = isOverlappingLectures(existingLectureTimes, newLectureTimes.get(0));
            if(isOverLapping){
                throw new HttpErrorException(HttpErrorCode.OverLappingLectureError);
            }
            existingLectureTimes.add(newLectureTimes.get(0));
            newLectureTimes.remove(0);
        }

        newLectures.forEach(dgLecture -> timeTableLectureRepository.save(TimeTableLecture.of(foundTimeTable, dgLecture)));


        return MyTimeTableScheduleCreateResponseDto.fromEntity(foundTimeTable, newLectures);
    }

    private boolean isOverlappingLectures(List<DgLectureTime> existingLectureTimes, DgLectureTime newLectureTime){
        for(DgLectureTime lectureTime : existingLectureTimes){
            if(lectureTime.getDayOfWeek().equals(newLectureTime.getDayOfWeek())
                    && lectureTime.getStartTime().before(newLectureTime.getEndTime())
                            && newLectureTime.getStartTime().before(lectureTime.getEndTime())){
                return true;
            }
        }
        return false;
    }
}
