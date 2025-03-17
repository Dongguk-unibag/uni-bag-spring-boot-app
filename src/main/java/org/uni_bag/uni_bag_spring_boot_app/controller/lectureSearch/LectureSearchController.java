package org.uni_bag.uni_bag_spring_boot_app.controller.lectureSearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch.LectureSearchResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.lectureSearch.LectureSearchService;
import org.uni_bag.uni_bag_spring_boot_app.swagger.JwtTokenErrorExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture/search")
@Tag(name = "강의 검색")
public class LectureSearchController {
    private final LectureSearchService lectureSearchService;

    @Operation(summary = "강의 검색")
    @JwtTokenErrorExample()
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LectureSearchResponseDto.class)))
    @GetMapping("/{year}/{semester}")
    public ResponseEntity<LectureSearchResponseDto> searchLecture(@Parameter(example = "2024", required = true, description = "년도") @PathVariable int year,
                                                                  @Parameter(example = "3", required = true, description = "학기") @PathVariable int semester,
                                                                  @Parameter(example = "1", description = "cursorId") @RequestParam(required = false, defaultValue = "0") Long cursorId,
                                                                  @Parameter(example = "불교문화대학", description = "개설대학") @RequestParam(required = false) String oc,
                                                                  @Parameter(example = "불교학부", description = "개설학과") @RequestParam(required = false) String od,
                                                                  @Parameter(example = "불교학전공", description = "개설전공") @RequestParam(required = false) String om,
                                                                  @Parameter(example = "1", description = "학년")    @RequestParam(required = false) Integer grade,
                                                                  @Parameter(example = "홍길동", description = "담당교수") @RequestParam(required = false) String professor,
                                                                  @Parameter(example = "강의 이름", description = "대승불교개론") @RequestParam(required = false) String lectureName) {
        LectureSearchResponseDto responseDto = lectureSearchService.searchLecture(cursorId, year, semester,oc, od, om, grade, professor, lectureName);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
