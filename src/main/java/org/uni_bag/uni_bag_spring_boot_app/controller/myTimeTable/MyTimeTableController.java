package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableCreateRequestDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableCreateResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.MyTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my/timeTable")
public class MyTimeTableController {
    private final MyTimeTableService myTimeTableService;

    @GetMapping("/{timeTableId}")
    public ResponseEntity<MyTimeTableReadResponseDto> getMyTimeTableSchedule(@AuthenticationPrincipal User user, @PathVariable Long timeTableId) {
        MyTimeTableReadResponseDto responseDto = myTimeTableService.getMyTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MyTimeTableCreateResponseDto> createTimeTable(@AuthenticationPrincipal User user,
                                                                        @Valid @RequestBody MyTimeTableCreateRequestDto requestDto){
        MyTimeTableCreateResponseDto responseDto = myTimeTableService.createMyTimeTable(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
