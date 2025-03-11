package org.uni_bag.uni_bag_spring_boot_app.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    Optional<Assignment> findByIdAndUser(Long id, User user);
    List<Assignment> findAllByUser(User user);

    @Query("SELECT a FROM Assignment a WHERE a.isCompleted = false AND (a.endDateTime >= :oneHourLater OR a.endDateTime >= :tomorrow9AM)")
    List<Assignment> findAssignmentsAfterOneHour(
            @Param("oneHourLater") LocalDateTime oneHourLater,
            @Param("tomorrow9AM") LocalDateTime tomorrow9AM
    );
}
