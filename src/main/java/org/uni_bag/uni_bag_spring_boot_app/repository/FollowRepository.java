package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    List<Follow> findAllByFollower(User follower);

    Optional<Follow> findByFollowerAndIsSecondaryFriend(User follower, boolean isSecondaryFriend);
}
