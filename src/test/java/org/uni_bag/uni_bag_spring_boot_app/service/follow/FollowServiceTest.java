package org.uni_bag.uni_bag_spring_boot_app.service.follow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.FollowRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.UnfollowRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.FollowRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("팔로우")
    class FollowTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            FollowRequestDto requestDto = new FollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            given(userRepository.findById(eq(requestDto.getFolloweeId()))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(false);

            // when
            followService.follow(follower, requestDto);

            // then
            then(userRepository).should(times(1)).findById(eq(requestDto.getFolloweeId()));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(times(1)).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 Followee일 경우")
        void whenFolloweeDoesNotExist_MustReturnError() {
            // given
            FollowRequestDto requestDto = new FollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            given(userRepository.findById(eq(requestDto.getFolloweeId()))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.follow(follower, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            then(userRepository).should(times(1)).findById(eq(requestDto.getFolloweeId()));
            then(followRepository).should(never()).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 이미 팔로우를 한 경우")
        void whenFollowRelationshipAlreadyExists_MustReturnError() {
            // given
            FollowRequestDto requestDto = new FollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            given(userRepository.findById(eq(requestDto.getFolloweeId()))).willReturn(Optional.of(followee));
            given(followRepository.existsByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(true);

            // when & then
            assertThatThrownBy(() -> followService.follow(follower, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadyExistFollowError.getMessage());

            then(userRepository).should(times(1)).findById(eq(requestDto.getFolloweeId()));
            then(followRepository).should(times(1)).existsByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("언팔로우")
    class UnfollowTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            UnfollowRequestDto requestDto = new UnfollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");
            given(userRepository.findById(eq(requestDto.getUnfolloweeId()))).willReturn(Optional.of(followee));

            Follow follow = createFollow(1L, follower, followee, false);
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(Optional.of(follow));

            // when
            followService.unfollow(follower, requestDto);

            // then
            then(userRepository).should(times(1)).findById(eq(requestDto.getUnfolloweeId()));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(times(1)).delete(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 Unfollowee일 경우")
        void whenUnfolloweeDoesNotExist_MustReturnError() {
            // given
            UnfollowRequestDto requestDto = new UnfollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            given(userRepository.findById(eq(requestDto.getUnfolloweeId()))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.unfollow(follower, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            then(userRepository).should(times(1)).findById(eq(requestDto.getUnfolloweeId()));
            then(followRepository).should(never()).findByFollowerAndFollowee(eq(follower), any());
            then(followRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenFollowRelationshipDoesNotExist_MustReturnError() {
            // given
            UnfollowRequestDto requestDto = new UnfollowRequestDto(1L);
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");
            given(userRepository.findById(eq(requestDto.getUnfolloweeId()))).willReturn(Optional.of(followee));
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.unfollow(follower, requestDto))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchFollowError.getMessage());

            then(userRepository).should(times(1)).findById(eq(requestDto.getUnfolloweeId()));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), any());
            then(followRepository).should(never()).delete(any());
        }
    }

    @Nested
    @DisplayName("팔로우 한 친구들 조회")
    class FollowListGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User follower = createUser(1L, "민수");
            User followee1 = createUser(2L, "희진");
            User followee2 = createUser(3L, "광래");

            Follow follow1 = createFollow(1L, follower, followee1, true);
            Follow follow2 = createFollow(2L, follower, followee2, false);

            given(followRepository.findAllByFollower(eq(follower))).willReturn(List.of(follow1, follow2));

            // when
            followService.getFolloweeList(follower);

            // then
            then(followRepository).should(times(1)).findAllByFollower(eq(follower));
        }
    }

    @Nested
    @DisplayName("Secondary 친구 등록")
    class SecondaryFriendUpdateTest {
        @Test
        @DisplayName("성공 - 기존에 등록된 Secondary 친구가 없는 경우")
        void whenNoExistingSecondaryFriend_MustReturnSuccess() {
            // given
            Long friendId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(friendId, "희진");

            Follow follow = createFollow(1L, follower, followee, false);

            given(userRepository.findById(eq(friendId))).willReturn(Optional.of(followee));
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(Optional.of(follow));
            given(followRepository.findByFollowerAndIsSecondaryFriend(follower, true)).willReturn(Optional.empty());

            // when
            followService.updateSecondaryFriend(follower, friendId);

            // then
            then(userRepository).should(times(1)).findById(eq(friendId));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(follower, true);
        }

        @Test
        @DisplayName("성공 - 기존에 등록된 Secondary 친구가 있는 경우")
        void whenExistingSecondaryFriendPresent_MustReturnSuccess() {
            // given
            Long friendId = 2L;
            User follower = createUser(1L, "민수");
            User followee1 = createUser(friendId, "희진");
            User followee2 = createUser(3L, "광래");

            Follow follow1 = createFollow(1L, follower, followee1, false);
            Follow follow2 = createFollow(1L, follower, followee2, true);

            given(userRepository.findById(eq(friendId))).willReturn(Optional.of(followee1));
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee1))).willReturn(Optional.of(follow1));
            given(followRepository.findByFollowerAndIsSecondaryFriend(follower, true)).willReturn(Optional.of(follow2));

            // when
            followService.updateSecondaryFriend(follower, friendId);

            // then
            then(userRepository).should(times(1)).findById(eq(friendId));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), eq(followee1));
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(follower, true);
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 followee일 경우")
        void whenFriendDoesNotExist_MustReturnError() {
            // given
            Long friendId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(friendId, "희진");

            given(userRepository.findById(eq(friendId))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.updateSecondaryFriend(follower, friendId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.UserNotFoundError.getMessage());

            then(userRepository).should(times(1)).findById(eq(friendId));
            then(followRepository).should(never()).findByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(never()).findByFollowerAndIsSecondaryFriend(follower, true);

        }

        @Test
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenFollowRelationshipDoesNotExist_MustReturnError() {
            // given
            Long friendId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(friendId, "희진");

            given(userRepository.findById(eq(friendId))).willReturn(Optional.of(followee));
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.updateSecondaryFriend(follower, friendId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSuchFollowError.getMessage());

            then(userRepository).should(times(1)).findById(eq(friendId));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(never()).findByFollowerAndIsSecondaryFriend(follower, true);
        }

        @Test
        @DisplayName("실패 - 이미 등록된 SecondaryFriend일 경우")
        void whenAlreadySecondaryFriend_MustReturnError() {
            // given
            Long friendId = 2L;
            User follower = createUser(1L, "민수");
            User followee = createUser(friendId, "희진");

            Follow follow = createFollow(1L, follower, followee, true);

            given(userRepository.findById(eq(friendId))).willReturn(Optional.of(followee));
            given(followRepository.findByFollowerAndFollowee(eq(follower), eq(followee))).willReturn(Optional.of(follow));

            // when & then
            assertThatThrownBy(() -> followService.updateSecondaryFriend(follower, friendId))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.AlreadySecondaryFriendError.getMessage());

            then(userRepository).should(times(1)).findById(eq(friendId));
            then(followRepository).should(times(1)).findByFollowerAndFollowee(eq(follower), eq(followee));
            then(followRepository).should(never()).findByFollowerAndIsSecondaryFriend(follower, true);
        }
    }

    @Nested
    @DisplayName("Secondary 친구 삭제")
    class SecondaryFriendDeleteTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            Follow follow = createFollow(1L, follower, followee, true);
            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.of(follow));

            // when
            followService.deleteSecondaryFriend(follower);

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
        }

        @Test
        @DisplayName("실패 - SecondaryFriend가 존재하지 않은 경우")
        void whenSecondaryFriendNotExist_MustReturnError() {
            // given
            User follower = createUser(1L, "민수");

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.deleteSecondaryFriend(follower))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSecondaryFriendError.getMessage());

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
        }
    }

    @Nested
    @DisplayName("Secondary 친구 조회")
    class SecondaryFriendGetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User follower = createUser(1L, "민수");
            User followee = createUser(2L, "희진");

            Follow follow = createFollow(1L, follower, followee, true);

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.of(follow));

            // when
            followService.getSecondaryFriend(follower);

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
        }

        @Test
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenNoSecondaryFriendExists_MustReturnError() {
            User follower = createUser(1L, "민수");

            given(followRepository.findByFollowerAndIsSecondaryFriend(eq(follower), eq(true))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followService.getSecondaryFriend(follower))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.NoSecondaryFriendError.getMessage());

            // then
            then(followRepository).should(times(1)).findByFollowerAndIsSecondaryFriend(eq(follower), eq(true));
        }
    }

    private Follow createFollow(Long followId, User follower, User followee, boolean isSecondaryFriend) {
        return new Follow(followId, follower, followee, isSecondaryFriend);
    }

    private User createUser(Long userId, String name) {
        return User.builder()
                .id(userId)
                .name(name)
                .build();
    }

}