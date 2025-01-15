package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleCreateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleDeleteRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTableSchedule.MyTimeTableScheduleDeleteResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my/timeTable/schedule")
public class MyTimeTableScheduleController {
    private final MyTimeTableScheduleService myTimeTableScheduleService;

    @PostMapping
    public ResponseEntity<MyTimeTableScheduleCreateResponseDto> addMyTimeTableSchedule(@AuthenticationPrincipal User user,
                                                                                       @Valid @RequestBody MyTimeTableScheduleCreateRequestDto requestDto){
        MyTimeTableScheduleCreateResponseDto responseDto = myTimeTableScheduleService.createMyTimeTableSchedule(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping()
    public ResponseEntity<MyTimeTableScheduleDeleteResponseDto> deleteMyTimeTableSchedule(@AuthenticationPrincipal User user,
                                                                                          @Valid @RequestBody MyTimeTableScheduleDeleteRequestDto requestDto) {
        MyTimeTableScheduleDeleteResponseDto responseDto = myTimeTableScheduleService.deleteMyTimeTableSchedule(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

    }
}
