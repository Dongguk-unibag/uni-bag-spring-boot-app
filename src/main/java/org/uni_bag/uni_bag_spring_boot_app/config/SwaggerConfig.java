package org.uni_bag.uni_bag_spring_boot_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.uni_bag.uni_bag_spring_boot_app.exception.ErrorResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ExampleHolder;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("학점가방 API 문서") // API의 제목
                .description("학점가방 API 명세서입니다.") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(
                    ApiErrorCodeExamples.class);

            JwtTokenErrorExample jwtTokenErrorExample = handlerMethod.getMethodAnnotation(JwtTokenErrorExample.class);

            generateErrorCodeResponseExample(
                    operation,
                    jwtTokenErrorExample,
                    apiErrorCodeExamples == null ? null : apiErrorCodeExamples.value()
            );


            return operation;
        };
    }

    private void generateErrorCodeResponseExample(Operation operation, JwtTokenErrorExample jwtTokenErrorExample, ApiErrorCodeExample[] apiErrorCodeExamples) {
        ApiResponses responses = operation.getResponses();

        List<ExampleHolder> exampleHolders = new ArrayList<>();

        if (jwtTokenErrorExample != null) {
            List<ExampleHolder> jwtExampleHolders = getJwtExampleHolders();
            exampleHolders.addAll(jwtExampleHolders);
        }

        if (apiErrorCodeExamples != null) {
            List<ExampleHolder> apiExampleHolders = Arrays.stream(apiErrorCodeExamples)
                    .map(apiErrorCodeExample -> ExampleHolder.builder()
                            .holder(getSwaggerExample(apiErrorCodeExample.value(), apiErrorCodeExample.description()))
                            .code(apiErrorCodeExample.value().getHttpStatus().value())
                            .name(apiErrorCodeExample.value().name())
                            .build()
                    ).toList();

            exampleHolders.addAll(apiExampleHolders);
        }


        // 에러 코드별로 그룹
        Map<Integer, List<ExampleHolder>> statusWithApiExampleHolder = exampleHolders.stream().collect(Collectors.groupingBy(ExampleHolder::getCode));

        // ExampleHolders를 ApiResponses에 추가
        addExamplesToResponses(responses, statusWithApiExampleHolder);
    }


    // ErrorResponseDto 형태의 예시 객체 생성
    private Example getSwaggerExample(HttpErrorCode errorCode, String description) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.from(errorCode);
        Example example = new Example();
        example.setValue(errorResponseDto);
        if (!description.isEmpty()) {
            example.setDescription(description);
        }

        return example;
    }

    // exampleHolder를 ApiResponses에 추가
    private void addExamplesToResponses(ApiResponses responses,
                                        Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();

                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(),
                                    exampleHolder.getHolder()
                            )
                    );
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(status), apiResponse);
                }
        );
    }

    private List<ExampleHolder> getJwtExampleHolders() {
        return new ArrayList<>(Arrays.asList(
                ExampleHolder.of(
                        getSwaggerExample(HttpErrorCode.AccessDeniedError, "요청 헤더에 토큰이 없을 경우 발생합니다."),
                        HttpErrorCode.AccessDeniedError.name(),
                        HttpErrorCode.AccessDeniedError.getHttpStatus().value()
                ),
                ExampleHolder.of(
                        getSwaggerExample(HttpErrorCode.NotValidAccessTokenError, "유효하지 않은 엑세스 토큰을 보냈을 경우 발생합니다."),
                        HttpErrorCode.NotValidAccessTokenError.name(),
                        HttpErrorCode.NotValidAccessTokenError.getHttpStatus().value()
                ),
                ExampleHolder.of(
                        getSwaggerExample(HttpErrorCode.ExpiredAccessTokenError, "만료된 엑세스 토큰을 보냈을 경우 발생합니다."),
                        HttpErrorCode.ExpiredAccessTokenError.name(),
                        HttpErrorCode.ExpiredAccessTokenError.getHttpStatus().value()
                ),
                ExampleHolder.of(
                        getSwaggerExample(HttpErrorCode.UserNotFoundError, "엑세스 토큰에 기록된 유저 정보가 존재하지 않을 경우 발생합니다."),
                        HttpErrorCode.UserNotFoundError.name(),
                        HttpErrorCode.UserNotFoundError.getHttpStatus().value()
                )
        ));
    }
}

