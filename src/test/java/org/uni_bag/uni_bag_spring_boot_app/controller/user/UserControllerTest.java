package org.uni_bag.uni_bag_spring_boot_app.controller.user;

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
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("회원 정보 조회")
    class UserInfoGetTest {

        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            User user = createUser();
            UserInfoDto dto = UserInfoDto.fromEntity(user);

            given(userService.getUserInfo(any())).willReturn(dto);

            // when & then
            mockMvc.perform(get("/api/user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.snsId").value("111"))
                    .andExpect(jsonPath("$.snsType").value("Kakao"))
                    .andExpect(jsonPath("$.studentId").value("2019212962"))
                    .andExpect(jsonPath("$.name").value("최민수"))
                    .andExpect(jsonPath("$.tosAccepted").value("false"))
                    .andExpect(jsonPath("$.emsLoggedIn").value("false"));
        }


    }

    @Nested
    @DisplayName("회원 검색")
    class UserSearchTest {
        @Test
        @WithMockUser
        @DisplayName("성공 - 회원 검색 성공")
        void success() throws Exception {
            // given
            User user = createUser();
            UserSearchResponseDto responseDto = UserSearchResponseDto.fromEntity(user);

            given(userService.searchUser(any(), any(), any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(get("/api/user/search")
                            .param("name", "최민수")
                            .param("studentId", "2019212962")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value("1"))
                    .andExpect(jsonPath("$.studentId").value("2019212962"))
                    .andExpect(jsonPath("$.name").value("최민수"));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 회원을 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() throws Exception {
            // given
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(userService.searchUser(any(), any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(get("/api/user/search")
                            .param("name", "최민수")
                            .param("studentId", "2019212962")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }
    }

    @Nested
    @DisplayName("이용약관 동의")
    class TosAgreeTest {
        @Test
        @WithMockUser
        @DisplayName("성공 - 이용약관 동의 완료")
        void whenValidUser_mustAgreeTos() throws Exception {
            // given
            UserTosAgreementResponseDto responseDto = UserTosAgreementResponseDto.createResponse();

            given(userService.agreeTos(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(post("/api/user/tos/agreement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("이용약관 동의가 활성화 되었습니다."));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 사용자를 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() throws Exception {
            // given
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(userService.agreeTos(any())).willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(post("/api/user/tos/agreement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 이미 이용약관에 동의한 경우")
        void whenUserAlreadyAgreedTos_mustReturnError() throws Exception {
            // given
            HttpErrorCode alreadyAgreeTosError = HttpErrorCode.AlreadyAgreeTosError;
            given(userService.agreeTos(any())).willThrow(new HttpErrorException(alreadyAgreeTosError));

            // when & then
            mockMvc.perform(post("/api/user/tos/agreement")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyAgreeTosError.name()))
                    .andExpect(jsonPath("$.message").value(alreadyAgreeTosError.getMessage()));
        }
    }

    @Nested
    @DisplayName("TOS 철회 및 EMS 정보 삭제")
    class TosRescindTest {
        @Test
        @WithMockUser
        @DisplayName("성공 - TOS 철회 및 EMS 정보 삭제 완료")
        void whenValidUser_mustRescindTosAndDeleteEmsInfo() throws Exception {
            // given
            UserTosRescissionResponseDto responseDto = UserTosRescissionResponseDto.createResponse();

            given(userService.rescindTos(any())).willReturn(responseDto);

            // when & then
            mockMvc.perform(post("/api/user/tos/rescission")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value( "이용약관 동의가 철회 되었습니다."));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 사용자를 찾을 수 없는 경우")
        void whenUserNotFound_mustReturnError() throws Exception {
            // given
            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(userService.rescindTos(any()))
                    .willThrow(new HttpErrorException(userNotFoundError));

            // when & then
            mockMvc.perform(post("/api/user/tos/rescission")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }
    }

    @Nested
    @DisplayName("EMS 로그인 완료")
    class EmsLoginCompleteTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            UserEmsLoginCompleteRequestDto requestDto = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");
            UserEmsLoginCompleteResponseDto responseDto = UserEmsLoginCompleteResponseDto.createResponse();

            given(userService.completeEmsLogin(any(), any())).willReturn(responseDto);

            mockMvc.perform(post("/api/user/emsLogin/complete")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("EMS의 일부 계정 정보가 서비스 서버에 반영되었습니다."));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 사용자가 존재하지 않을 경우")
        void whenUserNotFound_mustReturn404() throws Exception {
            UserEmsLoginCompleteRequestDto requestDto = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");

            HttpErrorCode userNotFoundError = HttpErrorCode.UserNotFoundError;
            given(userService.completeEmsLogin(any(), any())).willThrow(new HttpErrorException(userNotFoundError));

            mockMvc.perform(post("/api/user/emsLogin/complete")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value(userNotFoundError.name()))
                    .andExpect(jsonPath("$.message").value(userNotFoundError.getMessage()));
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 사용자가 이미 EMS 로그인을 한 경우")
        void whenUserAlreadyLoggedInEMS_mustReturn400() throws Exception {
            UserEmsLoginCompleteRequestDto requestDto = new UserEmsLoginCompleteRequestDto("최민수", "2019212962");

            HttpErrorCode alreadyEmsLoginError = HttpErrorCode.AlreadyEmsLoginError;
            given(userService.completeEmsLogin(any(), any())).willThrow(new HttpErrorException(alreadyEmsLoginError));

            mockMvc.perform(post("/api/user/emsLogin/complete")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value(alreadyEmsLoginError.name()))
                    .andExpect(jsonPath("$.message").value(alreadyEmsLoginError.getMessage()));
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
}