package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;

import java.util.Optional;

public interface DgLectureRepository extends JpaRepository<DgLecture, Long>, JpaSpecificationExecutor<DgLecture> {
    Optional<DgLecture> findByCourseCodeAndYearAndSemester(String courseCode, int year, int semester);
}
