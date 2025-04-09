package org.uni_bag.uni_bag_spring_boot_app.controller.assignment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.*;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.AssignmentService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExample;
import org.uni_bag.uni_bag_spring_boot_app.swagger.ApiErrorCodeExamples;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assigment")
@Tag(name = "과제 관리")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Operation(summary = "과제 리스트 조회")
    @JwtTokenErrorExample
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AssignmentListReadResponseDto.class)))
    @GetMapping()
    public ResponseEntity<AssignmentListReadResponseDto> getAssignmentList(@AuthenticationPrincipal User user){
        AssignmentListReadResponseDto responseDto = assignmentService.getAssignmentList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "과제 조회")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchAssignmentError)
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AssignmentReadResponseDto.class)))
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentReadResponseDto> getAssignment(@AuthenticationPrincipal User user,
                                                                   @Parameter(example = "1", required = true, description = "과제 아이디") @PathVariable Long assignmentId) {
        AssignmentReadResponseDto responseDto = assignmentService.getAssignment(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "과제 완료여부 토글")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchAssignmentError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentCompleteToggleResponseDto.class)))
    @PostMapping("/{assignmentId}/toggle")
    public ResponseEntity<AssignmentCompleteToggleResponseDto> toggleAssignmentComplete(@AuthenticationPrincipal User user,
                                                                                        @Parameter(example = "1", required = true, description = "과제 아이디") @PathVariable Long assignmentId){
        AssignmentCompleteToggleResponseDto responseDto = assignmentService.toggleAssignmentComplete(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "과제 생성")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchLectureError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentCreateResponseDto.class)))
    @PostMapping()
    public ResponseEntity<AssignmentCreateResponseDto> createAssignment(@AuthenticationPrincipal User user,
                                                                        @Valid @RequestBody AssignmentCreateRequestDto requestDto){
        AssignmentCreateResponseDto responseDto = assignmentService.createAssignment(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "과제 수정")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchLectureError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentUpdateResponseDto.class)))
    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignmentUpdateResponseDto> updateAssignment(@AuthenticationPrincipal User user,
                                                                        @Parameter(example = "1", required = true, description = "과제 아이디") @PathVariable Long assignmentId,
                                                                        @Valid @RequestBody AssignmentUpdateRequestDto requestDto){
        AssignmentUpdateResponseDto responseDto = assignmentService.updateAssignment(user, assignmentId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "과제 삭제")
    @JwtTokenErrorExample
    @ApiErrorCodeExamples(value = {
            @ApiErrorCodeExample(value = HttpErrorCode.NoSuchAssignmentError)
    })
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentDeleteResponseDto.class)))
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDeleteResponseDto> deleteAssignment(@AuthenticationPrincipal User user,
                                                                        @Parameter(example = "1", required = true, description = "과제 아이디") @PathVariable Long assignmentId){
        AssignmentDeleteResponseDto responseDto = assignmentService.deleteAssignment(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "완료된 과제 전체 삭제")
    @JwtTokenErrorExample
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentDeleteResponseDto.class)))
    @DeleteMapping("/completedAssignment")
    public ResponseEntity<AssignmentDeleteListResponseDto> deleteCompletedAssignment(@AuthenticationPrincipal User user){
        AssignmentDeleteListResponseDto responseDto = assignmentService.deleteCompletedAssignment(user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
