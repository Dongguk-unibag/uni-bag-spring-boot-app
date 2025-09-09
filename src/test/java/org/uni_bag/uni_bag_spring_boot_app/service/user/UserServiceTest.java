package org.uni_bag.uni_bag_spring_boot_app.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserEmsLoginCompleteRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("회원 정보 조회")
    class UserInfoGetTest {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() {
            // given
            User user = createUser();

            // when
            UserInfoDto userInfo = userService.getUserInfo(user);

            // then
            assertThat(userInfo).isNotNull();
            assertThat(userInfo.getSnsId()).isEqualTo("111");
            assertThat(userInfo.getSnsType()).isEqualTo(SnsType.Kakao);
            assertThat(userInfo.getStudentId()).isEqualTo("2019212962");
            assertThat(userInfo.getName()).isEqualTo("최민수");
        }
    }

    @Nested
    @DisplayName("회원 검색")
    class UserSearchTest {
        @Test
        @DisplayName("성공 - 회원 검색 성공")
        void success() {
            // given
            User user = createUser();
            String name = "최민수";
            String studentId = "2019212962";

            given(userRepository.findByNameAndStudentId(eq(name), eq(studentId))).willReturn(Optional.of(user));

            // when
            userService.searchUser(user, name, studentId);

            // then
            then(userRepository).should(times(1)).findByNameAndStudentId(eq(name), eq(studentId));
        }

        @Test
        @DisplayName("실패 - 회원을 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() {
            // given
            User user = createUser();
            String name = "최민수";
            String studentId = "2019212962";

            given(userRepository.findByNameAndStudentId(eq(name), eq(studentId))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> userService.searchUser(user, name, studentId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            // then
            then(userRepository).should(times(1)).findByNameAndStudentId(eq(name), eq(studentId));
        }
    }

    @Nested
    @DisplayName("이용약관 동의")
    class TosAgreeTest {
        @Test
        @DisplayName("성공 - 이용약관 동의 완료")
        void success() {
            // given
            User user = createUser(false, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.of(user));

            // when
            userService.agreeTos(user);

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }

        @Test
        @DisplayName("실패 - 사용자를 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() {
            // given
            User user = createUser(false, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> userService.agreeTos(user))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }

        @Test
        @DisplayName("실패 - 이미 이용약관에 동의한 경우")
        void whenUserAlreadyAgreedTos_mustReturnError() {
            // given
            User user = createUser(true, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.of(user));

            // when
            assertThatThrownBy(() -> userService.agreeTos(user))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyAgreeTosError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }
    }

    @Nested
    @DisplayName("TOS 철회 및 EMS 정보 삭제")
    class TosRescindTest {
        @Test
        @DisplayName("성공 - TOS 철회 및 EMS 정보 삭제 완료")
        void success() {
            // given
            User user = createUser(true, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.of(user));

            // when
            userService.rescindTos(user);

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }

        @Test
        @DisplayName("실패 - 사용자를 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() {
            // given
            User user = createUser(false, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> userService.rescindTos(user))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }
    }

    @Nested
    @DisplayName("EMS 로그인 완료")
    class EmsLoginCompleteTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User user = createUser(true, false);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.of(user));

            UserEmsLoginCompleteRequestDto request = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");

            // when
            userService.completeEmsLogin(user, request);

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않을 경우")
        void whenUserNotFound_mustReturnError() {
            // given
            User user = createUser(true, false);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.empty());

            UserEmsLoginCompleteRequestDto request = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");

            // when
            assertThatThrownBy(() -> userService.completeEmsLogin(user, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }

        @Test
        @DisplayName("실패 - 사용자가 이미 EMS 로그인을 한 경우")
        void whenUserAlreadyLoggedInEMS_mustReturnError() {
            // given
            User user = createUser(true, true);
            given(userRepository.findById(eq(user.getId()))).willReturn(Optional.of(user));

            UserEmsLoginCompleteRequestDto request = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");

            // when
            assertThatThrownBy(() -> userService.completeEmsLogin(user, request))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyEmsLoginError.getMessage());

            // then
            then(userRepository).should(times(1)).findById(eq(user.getId()));
        }
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .snsId("111")
                .snsType(SnsType.Kakao)
                .studentId("2019212962")
                .name("최민수")
                .isTosAccepted(false)
                .isEmsLoggedIn(false)
                .build();
    }

    private User createUser(boolean isTosAccepted, boolean isEmsLoggedIn) {
        return User.builder()
                .id(1L)
                .snsId("111")
                .snsType(SnsType.Kakao)
                .studentId("2019212962")
                .name("최민수")
                .isTosAccepted(isTosAccepted)
                .isEmsLoggedIn(isEmsLoggedIn)
                .build();
    }
}