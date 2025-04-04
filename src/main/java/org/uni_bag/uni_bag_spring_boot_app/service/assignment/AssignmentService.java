package org.uni_bag.uni_bag_spring_boot_app.service.assignment;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.Assignment;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.assignment.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.AssignmentRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {
    private final AssignmentNotificationService notificationService;

    private final AssignmentRepository assignmentRepository;
    private final DgLectureRepository dgLectureRepository;

    public AssignmentListReadResponseDto getAssignmentList(User user) {
        List<Assignment> foundAssignments = assignmentRepository.findAllByUser(user);
        return AssignmentListReadResponseDto.fromEntity(foundAssignments);
    }

    public AssignmentReadResponseDto getAssignment(User user, Long assignmentId) {
        Assignment foundAssignment = assignmentRepository.findByIdAndUser(assignmentId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchAssignmentError));

        return AssignmentReadResponseDto.fromEntity(foundAssignment);
    }

    public AssignmentCreateResponseDto createAssignment(User user, AssignmentCreateRequestDto requestDto) {
        DgLecture lecture = null;

        if (requestDto.getLectureId() != null){
            lecture = dgLectureRepository.findById(requestDto.getLectureId())
                    .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));
        }

        checkAssignmentTimeValid(requestDto.getStartDateTime(), requestDto.getEndDateTime());

        Assignment newAssignment = assignmentRepository.save(Assignment.of(user, lecture, requestDto));

        if(requestDto.getEndDateTime() != null) {
            notificationService.scheduleNotification(newAssignment);
        }

        return AssignmentCreateResponseDto.fromEntity(newAssignment);
    }

    public AssignmentUpdateResponseDto updateAssignment(User user, Long assignmentId, @Valid AssignmentUpdateRequestDto requestDto) {
        Optional<Assignment> foundAssignmentOptional = assignmentRepository.findByIdAndUser(assignmentId, user);

        if(foundAssignmentOptional.isEmpty()) {
            AssignmentCreateResponseDto responseDto = createAssignment(user, AssignmentCreateRequestDto.fromDto(requestDto));
            return AssignmentUpdateResponseDto.fromDto(responseDto);
        }

        Assignment foundAssignment = foundAssignmentOptional.get();
        DgLecture lecture = null;

        if (requestDto.getLectureId() != null){
            lecture = dgLectureRepository.findById(requestDto.getLectureId())
                    .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchLectureError));
        }

        checkAssignmentTimeValid(requestDto.getStartDateTime(), requestDto.getEndDateTime());

        foundAssignment.updateAssignment(requestDto, lecture);

        if(requestDto.getEndDateTime() != null) {
            notificationService.rescheduleNotification(foundAssignment);
        }

        return AssignmentUpdateResponseDto.fromEntity(foundAssignment);
    }

    public AssignmentDeleteResponseDto deleteAssignment(User user, Long assignmentId) {
        Assignment foundAssignment = assignmentRepository.findByIdAndUser(assignmentId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchAssignmentError));

        assignmentRepository.delete(foundAssignment);
        if(foundAssignment.getEndDateTime() != null) {
            notificationService.cancelNotification(foundAssignment);
        }

        return AssignmentDeleteResponseDto.fromEntity(foundAssignment);
    }

    public List<AssignmentDeleteResponseDto> deleteCompletedAssignment(User user) {
        List<Assignment> completedAssignments = assignmentRepository.findAllByUserAndIsCompletedTrue(user);
        assignmentRepository.deleteAll(completedAssignments);

        return AssignmentDeleteResponseDto.fromEntity(completedAssignments);
    }

    private void checkAssignmentTimeValid(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            if (startDateTime != null || endDateTime != null) {
                throw new HttpErrorException(HttpErrorCode.NotValidAssignmentTimeError);
            }
        } else if (startDateTime.isAfter(endDateTime)) {
            throw new HttpErrorException(HttpErrorCode.NotValidAssignmentTimeError);
        }
    }

    public AssignmentCompleteToggleResponseDto toggleAssignmentComplete(User user, Long assignmentId) {
        Assignment foundAssignment = assignmentRepository.findByIdAndUser(assignmentId, user)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchAssignmentError));

        foundAssignment.toggleCompleted();
        notificationService.cancelNotification(foundAssignment);

        return AssignmentCompleteToggleResponseDto.fromEntity(foundAssignment);
    }
}
