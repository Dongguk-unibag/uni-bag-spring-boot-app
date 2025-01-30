package org.uni_bag.uni_bag_spring_boot_app.service.timetable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTableLecture;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureTimeRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.TimeTableLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.LectureTimeColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {
    private final TimeTableLectureRepository timeTableLectureRepository;
    private final DgLectureTimeRepository dgLectureTimeRepository;

    public Map<DgLecture, LectureTimeColor> getTimetableWithLectures(TimeTable timeTable){
        List<TimeTableLecture> lectures = timeTableLectureRepository.findAllByTimeTable(timeTable);

        Map<DgLecture, LectureTimeColor> lecturesTimeMap = new HashMap<>();

        for (TimeTableLecture timeTableLecture : lectures) {
            lecturesTimeMap.put(
                    timeTableLecture.getLecture(),
                    new LectureTimeColor(
                            dgLectureTimeRepository.findAllByDgLecture(timeTableLecture.getLecture()),
                            timeTableLecture.getLectureColor()
                    )
            );
        }

        return lecturesTimeMap;
    }
}
