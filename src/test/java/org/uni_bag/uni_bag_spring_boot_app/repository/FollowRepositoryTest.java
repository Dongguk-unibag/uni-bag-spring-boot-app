package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FollowRepositoryTest {
    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("두 유저가 팔로우 관계인지 확인")
    void testExistsByFollowerAndFollowee() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        User user3 = createUser("희진", "345");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        Follow follow = createFollow(user1, user2, true);
        entityManager.persist(follow);

        // when
        boolean result1 = followRepository.existsByFollowerAndFollowee(user1, user2);
        boolean result2 = followRepository.existsByFollowerAndFollowee(user2, user3);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("두 유저 간의 Follow 객체 조회")
    void testFindByFollowerAndFollowee() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        User user3 = createUser("광래", "345");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        Follow follow = createFollow(user1, user2, true);
        entityManager.persist(follow);

        // when
        Optional<Follow> result1 = followRepository.findByFollowerAndFollowee(user1, user2);
        Optional<Follow> result2 = followRepository.findByFollowerAndFollowee(user2, user3);

        // then
        assertThat(result1).isPresent().get().isEqualTo(follow);
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("모든 Followee 조회")
    void testFindAllByFollower() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        User user3 = createUser("광래", "345");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        Follow follow1 = createFollow(user1, user2, true);
        Follow follow2 = createFollow(user1, user3, false);
        entityManager.persist(follow1);
        entityManager.persist(follow2);

        // when
        List<Follow> result = followRepository.findAllByFollower(user1);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(follow1, follow2);

    }

    @Test
    @DisplayName("Secondary 친구 조회")
    void testFindByFollowerAndIsSecondaryFriend() {
        // given
        User user1 = createUser("민수", "123");
        User user2 = createUser("희진", "234");
        entityManager.persist(user1);
        entityManager.persist(user2);

        Follow follow = createFollow(user1, user2, true);
        entityManager.persist(follow);

        // when
        Optional<Follow> result = followRepository.findByFollowerAndIsSecondaryFriend(user1, true);

        // then
        assertThat(result).isPresent().get().isEqualTo(follow);
    }

    private Follow createFollow(User follower, User followee, boolean isSecondaryFriend) {
        return Follow.builder()
                .follower(follower)
                .followee(followee)
                .isSecondaryFriend(isSecondaryFriend)
                .build();
    }

    private User createUser(String name, String snsId) {
        return User.builder()
                .name(name)
                .snsId(snsId)
                .snsType(SnsType.Kakao)
                .studentId(null)
                .build();
    }
}