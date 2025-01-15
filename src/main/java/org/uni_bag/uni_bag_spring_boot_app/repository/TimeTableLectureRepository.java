package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTableLecture;

import java.util.List;

public interface TimeTableLectureRepository extends JpaRepository<TimeTableLecture, Long> {
    List<TimeTableLecture> findAllByTimeTable(TimeTable timeTable);
}
