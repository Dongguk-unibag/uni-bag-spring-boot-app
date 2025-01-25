package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

import java.util.List;

@Getter
@AllArgsConstructor
public class LectureTimeColor {
    private List<DgLectureTime> dgLectureTime;
    private String lectureColor;
}
