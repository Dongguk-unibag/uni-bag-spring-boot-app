package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyTimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;
    private final DgLectureTimeRepository dgLectureTimeRepository;

    public MyTimeTableListReadResponseDto getMyTimeTableList(User user) {
        List<TimeTable> foundTimeTables = timeTableRepository.findAllByUser(user);
        return MyTimeTableListReadResponseDto.fromEntity(foundTimeTables);
    }

    public MyTimeTableReadResponseDto getMyTimeTable(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user).orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));
        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable);

        Map<DgLecture, List<DgLectureTime>> lecturesTimeMap = new HashMap<>();

        for (TimeTableLecture timeTableLecture : lectures) {
            lecturesTimeMap.put(
                    timeTableLecture.getLecture(),
                    dgLectureTimeRepository.findAllByDgLecture(timeTableLecture.getLecture())
            );
        }

        return MyTimeTableReadResponseDto.of(foundTimeTable, lecturesTimeMap);
    }

    public MyTimeTableCreateResponseDto createMyTimeTable(User user, MyTimeTableCreateRequestDto requestDto){
        Optional<TimeTable> timeTableOptional = timeTableRepository.findByUserAndYearAndSemester(user, requestDto.getYear(), requestDto.getSemester());
        if(timeTableOptional.isPresent()){
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

    public MyTimeTableOrderUpdateResponseDto updateMyTimeTableOrder(User user, MyTimeTableOrderUpdateRequestDto requestDto) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(requestDto.getTimeTableId(), user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        if(foundTimeTable.getTableOrder() == requestDto.getOrder()){
            throw new HttpErrorException(HttpErrorCode.SameTableOrderError);
        }

        Optional<TimeTable> originalOrderTimeTableOptional = timeTableRepository.findByUserAndTableOrder(user, requestDto.getOrder());
        originalOrderTimeTableOptional.ifPresent(timeTable -> timeTable.updateOrder(0));
        foundTimeTable.updateOrder(requestDto.getOrder());

        return MyTimeTableOrderUpdateResponseDto.fromEntity(foundTimeTable);
    }

    public MyEnrolledLectureReadResponseDto getMyEnrolledLecture(User user, Long timeTableId) {
        TimeTable foundTimeTable = timeTableRepository.findByIdAndUser(timeTableId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchTimeTableError));

        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(foundTimeTable);

        return MyEnrolledLectureReadResponseDto.of(lectures);
    }
}
