package org.uni_bag.uni_bag_spring_boot_app.service.myTimeTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;

@Getter
@AllArgsConstructor
public class LectureColor{
    private DgLecture lecture;
    private String lectureColor;
}

