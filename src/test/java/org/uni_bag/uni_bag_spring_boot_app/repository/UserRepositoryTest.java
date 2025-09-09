package org.uni_bag.uni_bag_spring_boot_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserEmsLoginCompleteRequestDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("snsId로 유저 조회")
    void findBySnsId(){
        // given
        User user = createUser();
        entityManager.persist(user);

        // when
        Optional<User> result = userRepository.findBySnsId(user.getSnsId());

        // then
        assertThat(result).isPresent().get().isEqualTo(user);

    }

    @Test
    @DisplayName("이름과 학번으로 유저 조회")
    void findByNameAndStudentId(){
        // given
        User user = createUser("최민수", "2019212962");
        entityManager.persist(user);

        // when
        Optional<User> result = userRepository.findByNameAndStudentId(user.getName(),user.getStudentId());

        // then
        assertThat(result).isPresent().get().isEqualTo(user);
    }

    public User createUser() {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        return User.from(oAuthUserInfoDto);
    }

    public User createUser(String name, String studentId) {
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto("111", SnsType.Kakao, "민수", "unibag@unibag.com");
        User user = User.from(oAuthUserInfoDto);
        UserEmsLoginCompleteRequestDto userEmsLoginCompleteRequestDto = new UserEmsLoginCompleteRequestDto(name, studentId);
        user.completeEmsLogin(userEmsLoginCompleteRequestDto);
        return user;
    }
}