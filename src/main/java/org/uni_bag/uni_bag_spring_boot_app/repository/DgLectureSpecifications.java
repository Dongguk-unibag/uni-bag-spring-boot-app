package org.uni_bag.uni_bag_spring_boot_app.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLecture;
import org.uni_bag.uni_bag_spring_boot_app.domain.DgLectureTime;

public class DgLectureSpecifications {
    public static Specification<DgLecture> joinLectureTimes() {
        return (root,  query, criteriaBuilder) -> {
            Join<DgLectureTime, DgLecture> lectureJoin = root.join("dgLectureTimes", JoinType.LEFT);
            return criteriaBuilder.conjunction(); // 단순히 Join만 하고 조건을 추가하지 않음
        };
    }

    public static Specification<DgLecture> yearEquals(Integer year) {
        return (root, query, cb) -> year != null ? cb.equal(root.get("year"), year) : null;
    }

    public static Specification<DgLecture> semesterEquals(Integer semester) {
        return (root, query, cb) -> semester != null ? cb.equal(root.get("semester"), semester) : null;
    }

    public static Specification<DgLecture> ocEquals(String oc) {
        return (root, query, cb) -> cb.equal(root.get("offeringCollege"), oc);
    }

    public static Specification<DgLecture> odEquals(String od) {
        return (root, query, cb) -> cb.equal(root.get("offeringDepartment"), od);
    }

    public static Specification<DgLecture> omEquals(String om) {
        return (root, query, cb) -> cb.equal(root.get("offeringMajor"), om);
    }
    public static Specification<DgLecture> gradeEquals(String grade) {
        return (root, query, cb) -> cb.equal(root.get("targetGrade"), grade);
    }

    public static Specification<DgLecture> professorLike(String professor) {
        return (root, query, cb) -> cb.like(root.get("instructor"), professor);
    }

    public static Specification<DgLecture> lectureNameLike(String lectureName) {
        return (root, query, cb) -> cb.like(root.get("courseName"), lectureName);
    }

    public static Specification<DgLecture> idGreaterThan(Long cursorId) {
        return (root, query, cb) -> cb.greaterThan(root.get("id"), cursorId);
    }
}
