package org.uni_bag.uni_bag_spring_boot_app.controller.friendTimeTable;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableListReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.friendTimeTable.FriendTimeTableReadResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.friendTimeTable.FriendTimeTableService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend/timeTable")
public class FriendTimeTableController {
    private final FriendTimeTableService friendTimeTableService;

    @GetMapping()
    public ResponseEntity<FriendTimeTableListReadResponseDto> getFriendTimeTableList(@AuthenticationPrincipal User user,
                                                                                     @RequestParam Long friendId) {
        FriendTimeTableListReadResponseDto responseDto = friendTimeTableService.getFriendTimeTableList(user, friendId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{timeTableId}")
    public ResponseEntity<FriendTimeTableReadResponseDto> getFriendTimeTable(@AuthenticationPrincipal User user,
                                                                             @PathVariable Long timeTableId) {
        FriendTimeTableReadResponseDto responseDto = friendTimeTableService.getFriendTimeTable(user, timeTableId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
