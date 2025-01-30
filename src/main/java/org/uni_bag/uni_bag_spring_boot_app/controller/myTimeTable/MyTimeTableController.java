package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my/timeTable")
@Tag(name = "개인 시간표 관리")
public class MyTimeTableController {
    private final MyTimeTableService myTimeTableService;

    @Operation(summary = "특정 개인 시간표 조회")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MyTimeTableReadResponseDto.class)))
    @GetMapping("/{timeTableId}")
    public ResponseEntity<MyTimeTableReadResponseDto> getMyTimeTable(@AuthenticationPrincipal User user,
                                                                     @Parameter(description = "조회할 시간표 아이디", required = true, example = "1")
                                                                     @PathVariable Long timeTableId) {
        MyTimeTableReadResponseDto responseDto = myTimeTableService.getMyTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "개인 시간표 리스트 조회")
    @JwtTokenErrorExample()
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MyTimeTableListReadResponseDto.class)))
    @GetMapping()
    public ResponseEntity<MyTimeTableListReadResponseDto> getMyTimeTableList(@AuthenticationPrincipal User user) {
        MyTimeTableListReadResponseDto responseDto = myTimeTableService.getMyTimeTableList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "특정 시간표에 대한 강의 조회")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MyEnrolledLectureReadResponseDto.class)))
    @GetMapping("/{timeTableId}/lecture")
    public ResponseEntity<MyEnrolledLectureReadResponseDto> getMyEnrolledLecture(@AuthenticationPrincipal User user,
                                                                                 @Parameter(example = "1", required = true, description = "찾고자하는 시간표 아이디") @PathVariable Long timeTableId) {
        MyEnrolledLectureReadResponseDto responseDto = myTimeTableService.getMyEnrolledLecture(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "primary 강의 시간표 조회")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MyTimetableGetResponseDto.class)))
    @GetMapping("/primary")
    public ResponseEntity<MyTimetableGetResponseDto> getMyPrimaryTimetable(@AuthenticationPrincipal User user) {
        MyTimetableGetResponseDto responseDto = myTimeTableService.getMyPrimaryTimetable(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "개인 시간표 추가")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.AlreadyExistSeasonTable),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyTimeTableCreateResponseDto.class)))
    @PostMapping
    public ResponseEntity<MyTimeTableCreateResponseDto> createTimeTable(@AuthenticationPrincipal User user,
                                                                        @Valid @RequestBody MyTimeTableCreateRequestDto requestDto) {
        MyTimeTableCreateResponseDto responseDto = myTimeTableService.createMyTimeTable(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "개인 시간표 순위 수정",
            description = "특정 시간표를 Primary 혹은 Secondary 시간표로 지정할 수 있습니다."
    )
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
            @ApiErrorCodeExample(value = HttpErrorCode.SameTableOrderError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyTimeTableOrderUpdateResponseDto.class)))
    @PatchMapping("/order")
    public ResponseEntity<MyTimeTableOrderUpdateResponseDto> updateMyTimeTableOrder(@AuthenticationPrincipal User user,
                                                                                    @Valid @RequestBody MyTimeTableOrderUpdateRequestDto requestDto) {
        MyTimeTableOrderUpdateResponseDto responseDto = myTimeTableService.updateMyTimeTableOrder(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "개인 시간표 삭제")
    @JwtTokenErrorExample()
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchTimeTableError),
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = MyTimeTableDeleteResponseDto.class)))
    @DeleteMapping("/{timeTableId}")
    public ResponseEntity<MyTimeTableDeleteResponseDto> deleteTimeTable(@AuthenticationPrincipal User user, @PathVariable Long timeTableId) {
        MyTimeTableDeleteResponseDto responseDto = myTimeTableService.deleteTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
