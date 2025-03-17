package org.uni_bag.uni_bag_spring_boot_app.service.lectureSearch;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.dto.lectureSearch.LectureSearchResponseDto;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.DgLectureSpecifications;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LectureSearchService {
    private final DgLectureRepository dgLectureRepository;

    public LectureSearchResponseDto searchLecture(Long cursorId, int year, int semester,
                                                  String oc, String od, String om,
                                                  Integer grade, String professor, String lectureName) {

        Specification<DgLecture> spec = (root, query, criteriaBuilder) -> null;

        spec = spec.and(DgLectureSpecifications.yearEquals(year));
        spec = spec.and(DgLectureSpecifications.semesterEquals(semester));
        if (oc != null) spec = spec.and(DgLectureSpecifications.ocEquals(oc));
        if (od != null) spec = spec.and(DgLectureSpecifications.odEquals(od));
        if (om != null) spec = spec.and(DgLectureSpecifications.omEquals(om));
        if (grade != null) spec = spec.and(DgLectureSpecifications.gradeEquals(grade+"학년"));
        if (professor != null) spec = spec.and(DgLectureSpecifications.professorEquals(professor));
        if (lectureName != null) spec = spec.and(DgLectureSpecifications.lectureNameEquals(lectureName));
        spec = spec.and(DgLectureSpecifications.idGreaterThan(cursorId));
        spec = spec.and(DgLectureSpecifications.joinLectureTimes());

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        List<DgLecture> lectures = dgLectureRepository.findAll(spec, pageable).stream().toList();

        return LectureSearchResponseDto.from(lectures);
    }
}
