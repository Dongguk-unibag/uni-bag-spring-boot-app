package org.uni_bag.uni_bag_spring_boot_app.controller.fcm;

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
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.fcm.FcmTokenSaveResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.fcm.FcmService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FcmController.class)
class FcmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FcmService fcmService;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("유저 FCM 토큰 저장")
    class FcmTokenSaveTest {
        @Test
        @WithMockUser
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long userId = 1L;
            String fcmToken = "d6s5YYBcRbSogj9YijTalS:APA91bG8eSQ6IVTNGp6E-aD5DYw62J7k013Evn";

            FcmTokenSaveRequestDto request = new FcmTokenSaveRequestDto(fcmToken);
            FcmTokenSaveResponseDto fcmTokenSaveResponseDto = new FcmTokenSaveResponseDto(userId, fcmToken);

            given(fcmService.saveFcmToken(any(), any())).willReturn(fcmTokenSaveResponseDto);

            // when & then
            mockMvc.perform(post("/api/fcm/token")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.fcmToken").value(fcmToken));

        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 요청 바디 내 FCM 토큰이 없을 경우")
        void whenFcmTokenIsMissing_thenReturnsBadRequest() throws Exception {
            // given
            Long userId = 1L;

            FcmTokenSaveRequestDto request = new FcmTokenSaveRequestDto(null);
            FcmTokenSaveResponseDto fcmTokenSaveResponseDto = new FcmTokenSaveResponseDto(userId, null);

            given(fcmService.saveFcmToken(any(User.class), any(FcmTokenSaveRequestDto.class))).willReturn(fcmTokenSaveResponseDto);

            // when & then
            mockMvc.perform(post("/api/fcm/token")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.NotValidRequestError.name()))
                    .andExpect(jsonPath("$.message").value(HttpErrorCode.NotValidRequestError.getMessage()));

        }
    }
}