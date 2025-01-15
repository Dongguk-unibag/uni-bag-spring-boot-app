package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.PrimaryTimeTable;

public interface PrimaryTimeTableRepository extends JpaRepository<PrimaryTimeTable, Long> {
}
