package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableCreateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableReadResponseDto;
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

        TimeTable newTimeTable = timeTableRepository.save(TimeTable.of(requestDto.getYear(), requestDto.getSemester(), user));
        return MyTimeTableCreateResponseDto.fromEntity(newTimeTable);
    }
}
