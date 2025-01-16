package org.uni_bag.uni_bag_spring_boot_app.controller.myTimeTable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.myTimeTable.*;
import org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable.MyTimeTableService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my/timeTable")
public class MyTimeTableController {
    private final MyTimeTableService myTimeTableService;

    @GetMapping("/{timeTableId}")
    public ResponseEntity<MyTimeTableReadResponseDto> getMyTimeTable(@AuthenticationPrincipal User user, @PathVariable Long timeTableId) {
        MyTimeTableReadResponseDto responseDto = myTimeTableService.getMyTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<MyTimeTableListReadResponseDto> getMyTimeTableList(@AuthenticationPrincipal User user) {
        MyTimeTableListReadResponseDto responseDto = myTimeTableService.getMyTimeTableList(user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MyTimeTableCreateResponseDto> createTimeTable(@AuthenticationPrincipal User user,
                                                                        @Valid @RequestBody MyTimeTableCreateRequestDto requestDto){
        MyTimeTableCreateResponseDto responseDto = myTimeTableService.createMyTimeTable(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/order")
    public ResponseEntity<MyTimeTableOrderUpdateResponseDto> updateMyTimeTableOrder(@AuthenticationPrincipal User user,
                                                                               @Valid @RequestBody MyTimeTableOrderUpdateRequestDto requestDto){
        MyTimeTableOrderUpdateResponseDto responseDto = myTimeTableService.updateMyTimeTableOrder(user, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    @DeleteMapping("/{timeTableId}")
    public ResponseEntity<MyTimeTableDeleteResponseDto> deleteTimeTable(@AuthenticationPrincipal User user, @PathVariable Long timeTableId) {
        MyTimeTableDeleteResponseDto responseDto = myTimeTableService.deleteTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
