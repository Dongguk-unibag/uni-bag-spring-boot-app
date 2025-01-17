package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    Optional<Assignment> findByIdAndUser(Long id, User user);
    List<Assignment> findAllByUser(User user);
}
