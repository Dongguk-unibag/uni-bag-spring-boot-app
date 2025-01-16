package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

import java.util.List;

public interface DgLectureTimeRepository extends JpaRepository<DgLectureTime, Long> {
    List<DgLectureTime> findAllByDgLecture(DgLecture dgLecture);
}
