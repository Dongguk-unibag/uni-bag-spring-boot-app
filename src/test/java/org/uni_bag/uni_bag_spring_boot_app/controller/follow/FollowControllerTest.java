package org.uni_bag.uni_bag_spring_boot_app.controller.follow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.follow.FollowService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
class FollowControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowService followService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("팔로우 한 친구들 조회")
    class FolloweeListGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Follow follow1 = createFollow(1L, createUser(2L, "희진"), true);
            Follow follow2 = createFollow(2L, createUser(3L, "광래"), false);

            FolloweeListReadResponseDto responseDto = FolloweeListReadResponseDto.of(List.of(follow1, follow2));
            given(followService.getFolloweeList(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(get("/api/friend"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.followees.length()").value(responseDto.getFollowees().size()))
                    .andExpect(jsonPath("$.followees[0].followeeId").value(2L))
                    .andExpect(jsonPath("$.followees[0].followeeName").value("희진"));
        }
    }

    @Nested
    @DisplayName("팔로우")
    class FollowTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            FollowRequestDto request = new FollowRequestDto(1L);
            FollowResponseDto response = new FollowResponseDto(1L, "민수");
            given(followService.follow(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/friend/follow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.followeeId").value(1L))
                    .andExpect(jsonPath("$.followeeName").value("민수"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않은 Followee일 경우")
        void whenFolloweeDoesNotExist_MustReturnError() throws Exception {
            // given
            FollowRequestDto request = new FollowRequestDto(1L);
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(followService.follow(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(post("/api/friend/follow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 이미 팔로우를 한 경우")
        void whenFollowRelationshipAlreadyExists_MustReturnError() throws Exception {
            // given
            FollowRequestDto request = new FollowRequestDto(1L);
            HttpErrorCode alreadyExistFollowError = HttpErrorCode.AlreadyExistFollowError;
            given(followService.follow(any(), any())).willThrow(new HttpErrorException(alreadyExistFollowError));

            // when & then
            mockMvc.perform(post("/api/friend/follow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyExistFollowError.name()))
                    .andExpect(jsonPath("$.message").value(alreadyExistFollowError.getMessage()));
        }
    }

    @Nested
    @DisplayName("언팔로우")
    class UnfollowTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            UnfollowRequestDto request = new UnfollowRequestDto(1L);
            UnfollowResponseDto response = new UnfollowResponseDto(1L, "민수");
            given(followService.unfollow(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(post("/api/friend/unfollow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.unfolloweeId").value(1L))
                    .andExpect(jsonPath("$.unfolloweeName").value("민수"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않은 Unfollowee일 경우")
        void whenUnfolloweeDoesNotExist_MustReturnError() throws Exception {
            // given
            UnfollowRequestDto request = new UnfollowRequestDto(1L);
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(followService.unfollow(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(post("/api/friend/unfollow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenFollowRelationshipDoesNotExist_MustReturnError() throws Exception {
            // given
            UnfollowRequestDto request = new UnfollowRequestDto(1L);
            HttpErrorCode noSuchFollowError = HttpErrorCode.NoSuchFollowError;
            given(followService.unfollow(any(), any())).willThrow(new HttpErrorException(noSuchFollowError));

            // when & then
            mockMvc.perform(post("/api/friend/unfollow")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchFollowError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchFollowError.getMessage()));
        }
    }

    @Nested
    @DisplayName("Secondary 친구 조회")
    class SecondaryFriendGetTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            SecondaryFriendReadResponseDto response = SecondaryFriendReadResponseDto.fromEntity(createUser(1L, "민수"));
            given(followService.getSecondaryFriend(any())).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/friend/secondary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.friendId").value(1L))
                    .andExpect(jsonPath("$.friendName").value("민수"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenNoSecondaryFriendExists_MustReturnError() throws Exception {
            // given
            HttpErrorCode noSecondaryFriendError = HttpErrorCode.NoSecondaryFriendError;
            given(followService.getSecondaryFriend(any())).willThrow(new HttpErrorException(noSecondaryFriendError));

            // when & then
            mockMvc.perform(get("/api/friend/secondary"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSecondaryFriendError.name()))
                    .andExpect(jsonPath("$.message").value(noSecondaryFriendError.getMessage()));
        }
    }

    @Nested
    @DisplayName("Secondary 친구 등록")
    class SecondaryFriendUpdateTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            SecondaryFriendUpdateResponseDto response =
                    SecondaryFriendUpdateResponseDto.from(createFollow(1L, createUser(2L, "희진"), true));
            given(followService.updateSecondaryFriend(any(), any())).willReturn(response);

            // when & then
            mockMvc.perform(put("/api/friend/secondary/{friendId}", 1L).with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.friendId").value(2L))
                    .andExpect(jsonPath("$.secondaryFriend").value(true));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 존재하지 않은 followee일 경우")
        void whenFriendDoesNotExist_MustReturnError() throws Exception {
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(followService.updateSecondaryFriend(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(put("/api/friend/secondary/{friendId}", 1L).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 팔로우를 한 관계가 아닌 경우")
        void whenFollowRelationshipDoesNotExist_MustReturnError() throws Exception  {
            HttpErrorCode noSuchFollowError = HttpErrorCode.NoSuchFollowError;
            given(followService.updateSecondaryFriend(any(), any())).willThrow(new HttpErrorException(noSuchFollowError));

            // when & then
            mockMvc.perform(put("/api/friend/secondary/{friendId}", 1L).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSuchFollowError.name()))
                    .andExpect(jsonPath("$.message").value(noSuchFollowError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 이미 등록된 SecondaryFriend일 경우")
        void whenAlreadySecondaryFriend_MustReturnError() throws Exception  {
            HttpErrorCode alreadySecondaryFriendError = HttpErrorCode.AlreadySecondaryFriendError;
            given(followService.updateSecondaryFriend(any(), any())).willThrow(new HttpErrorException(alreadySecondaryFriendError));

            // when & then
            mockMvc.perform(put("/api/friend/secondary/{friendId}", 1L).with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadySecondaryFriendError.name()))
                    .andExpect(jsonPath("$.message").value(alreadySecondaryFriendError.getMessage()));
        }
    }

    @Nested
    @DisplayName("Secondary 친구 삭제")
    class SecondaryFriendDeleteTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            SecondaryFriendUpdateResponseDto response =
                    SecondaryFriendUpdateResponseDto.from(createFollow(1L, createUser(2L, "희진"), false));
            given(followService.deleteSecondaryFriend(any())).willReturn(response);

            // when & then
            mockMvc.perform(delete("/api/friend/secondary", 1L).with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.friendId").value(2L))
                    .andExpect(jsonPath("$.secondaryFriend").value(false));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - SecondaryFriend가 존재하지 않은 경우")
        void whenSecondaryFriendNotExist_MustReturnError() throws Exception  {
            HttpErrorCode noSecondaryFriendError = HttpErrorCode.NoSecondaryFriendError;
            given(followService.deleteSecondaryFriend(any())).willThrow(new HttpErrorException(noSecondaryFriendError));

            // when & then
            mockMvc.perform(delete("/api/friend/secondary", 1L).with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(noSecondaryFriendError.name()))
                    .andExpect(jsonPath("$.message").value(noSecondaryFriendError.getMessage()));
        }

    }

    private Follow createFollow(Long followId, User followee, boolean isSecondaryFriend) {
        return new Follow(followId, createUser(1L, "민수"), followee, isSecondaryFriend);
    }

    private User createUser(Long userId, String name) {
        return User.builder()
                .id(userId)
                .name(name)
                .build();
    }
}