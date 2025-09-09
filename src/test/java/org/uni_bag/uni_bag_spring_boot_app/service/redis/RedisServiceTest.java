package org.uni_bag.uni_bag_spring_boot_app.service.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    @InjectMocks
    private RedisService redisService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

   @Nested
   @DisplayName("저장")
   class SaveTest {
       @Test
       @DisplayName("성공")
       void success() {
           // given
           String key = "accessToken";
           String value = "12345";

           given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

           // when
           redisService.save(key, value);

           // then
           then(valueOperations).should().set(eq(key), eq(value));
       }
   }

    @Nested
    @DisplayName("조회")
    class GetTest {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            String key = "accessToken";
            String value = "12345";

            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("accessToken")).willReturn("12345");

            // when
            Optional<String> result = redisService.get(key);

            // then
            assertThat(result).isPresent();
            assertThat(result).get().isEqualTo(value);
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 키로 접근한 경우")
        void whenKeyDoesNotExist_mustReturnEmptyOptional() {
            // given
            String key = "accessToken";
            String value = "12345";

            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("accessToken")).willReturn(null);

            // when
            Optional<String> result = redisService.get(key);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteTest {
        @Test
        @DisplayName("성공")
        void uccess() {
            // given
            String key = "accessToken";
            String value = "12345";

            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.getAndDelete(eq(key))).willReturn(value);

            // when
            redisService.delete(key);

            // then
            then(stringRedisTemplate).should(times(1)).opsForValue();
            then(valueOperations).should(times(1)).getAndDelete(eq(key));
        }

        @Test
        @DisplayName("실패 - 존재하지 않은 키로 접근한 경우")
        void whenKeyDoesNotExist_mustThrowError() {
            // given
            String key = "accessToken";

            given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.getAndDelete(eq(key))).willReturn(null);

            // when & then
            assertThatThrownBy(() -> redisService.delete(key))
                    .isInstanceOf(HttpErrorException.class)
                    .hasMessage(HttpErrorCode.InternalServerError.getMessage());

            then(stringRedisTemplate).should(times(1)).opsForValue();
            then(valueOperations).should(times(1)).getAndDelete(eq(key));
        }
    }
}