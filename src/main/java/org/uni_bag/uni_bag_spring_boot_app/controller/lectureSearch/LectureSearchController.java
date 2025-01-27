package org.uni_bag.uni_bag_spring_boot_app.controller.lectureSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch.LectureSearchResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.service.lectureSearch.LectureSearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lecture/search")
public class LectureSearchController {
    private final LectureSearchService lectureSearchService;

    @GetMapping("/{year}/{semester}")
    public ResponseEntity<LectureSearchResponseDto> searchLecture(@PathVariable int year, @PathVariable int semester,
                                                                     @RequestParam(required = false) String oc, @RequestParam(required = false) String od, @RequestParam(required = false) String om,
                                                                     @RequestParam(required = false) Integer grade,
                                                                     @RequestParam(required = false) String professor, @RequestParam(required = false) String lectureName) {
        LectureSearchResponseDto responseDto = lectureSearchService.searchLecture(year, semester,oc, od, om, grade, professor, lectureName);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
