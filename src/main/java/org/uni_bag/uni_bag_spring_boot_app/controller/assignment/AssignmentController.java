package org.uni_bag.uni_bag_spring_boot_app.controller.assignment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.*;
import org.uni_bag.uni_bag_spring_boot_app.service.assignment.AssignmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assigment")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @GetMapping()
    public ResponseEntity<AssignmentListReadResponseDto> getAssignmentList(@AuthenticationPrincipal User user){
        AssignmentListReadResponseDto responseDto = assignmentService.getAssignmentList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentReadResponseDto> getAssignment(@AuthenticationPrincipal User user,
                                                                   @PathVariable Long assignmentId) {
        AssignmentReadResponseDto responseDto = assignmentService.getAssignment(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/{assignmentId}/toggle")
    public ResponseEntity<AssignmentCompleteToggleResponseDto> toggleAssignmentComplete(@AuthenticationPrincipal User user,
                                                                                        @PathVariable Long assignmentId){
        AssignmentCompleteToggleResponseDto responseDto = assignmentService.toggleAssignmentComplete(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<AssignmentCreateResponseDto> createAssignment(@AuthenticationPrincipal User user,
                                                                        @Valid @RequestBody AssignmentCreateRequestDto requestDto){
        AssignmentCreateResponseDto responseDto = assignmentService.createAssignment(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignmentUpdateResponseDto> updateAssignment(@AuthenticationPrincipal User user,
                                                                        @PathVariable Long assignmentId,
                                                                        @Valid @RequestBody AssignmentUpdateRequestDto requestDto){
        AssignmentUpdateResponseDto responseDto = assignmentService.updateAssignment(user, assignmentId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDeleteResponseDto> deleteAssignment(@AuthenticationPrincipal User user,
                                                                        @PathVariable Long assignmentId){
        AssignmentDeleteResponseDto responseDto = assignmentService.deleteAssignment(user, assignmentId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
