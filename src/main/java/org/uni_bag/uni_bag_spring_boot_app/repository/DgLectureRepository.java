package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;

public interface DgLectureRepository extends JpaRepository<DgLecture, Long> {
}
