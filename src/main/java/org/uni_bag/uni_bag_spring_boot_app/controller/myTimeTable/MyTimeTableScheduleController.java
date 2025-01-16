package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableDeleteResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleDeleteRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleDeleteResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableScheduleService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my/timeTable/schedule")
@Tag(name = "개인 시간표 강의 일정 관리")
public class MyTimeTableScheduleController {
    private final MyTimeTableScheduleService myTimeTableScheduleService;

    @Operation(summary = "개인 시간표 강의 일정 등록")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchLectureError),
            @ApiErrorCodeExample(value = HttpErrorCode.SemesterMismatchException),
            @ApiErrorCodeExample(value = HttpErrorCode.OverLappingLectureError),
            @ApiErrorCodeExample(value = HttpErrorCode.AlreadyExistLectureScheduleError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyTimeTableScheduleCreateResponseDto.class)))
    @PostMapping
    public ResponseEntity<MyTimeTableScheduleCreateResponseDto> addMyTimeTableSchedule(@AuthenticationPrincipal User user,
                                                                                       @Valid @RequestBody MyTimeTableScheduleCreateRequestDto requestDto){
        MyTimeTableScheduleCreateResponseDto responseDto = myTimeTableScheduleService.createMyTimeTableSchedule(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "개인 시간표 강의 일정 삭제")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchLectureError),
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableScheduleError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyTimeTableScheduleDeleteResponseDto.class)))
    @DeleteMapping()
    public ResponseEntity<MyTimeTableScheduleDeleteResponseDto> deleteMyTimeTableSchedule(@AuthenticationPrincipal User user,
                                                                                          @Valid @RequestBody MyTimeTableScheduleDeleteRequestDto requestDto) {
        MyTimeTableScheduleDeleteResponseDto responseDto = myTimeTableScheduleService.deleteMyTimeTableSchedule(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

    }
}
