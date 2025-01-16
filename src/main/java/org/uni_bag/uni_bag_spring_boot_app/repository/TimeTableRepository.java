package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.TimeTable;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;
import java.util.Optional;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    Optional<TimeTable> findByUserAndYearAndSemester(User user, int year, int semester);
    Optional<TimeTable> findByIdAndUser(Long id, User user);
    List<TimeTable> findAllByUser(User user);
}
