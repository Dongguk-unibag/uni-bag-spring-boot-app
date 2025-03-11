package org.uni_bag.uni_bag_spring_boot_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssignmentAlarmType {
    YESTERDAY_9AM("1"),
    ONE_HOUR_AGO("2");

    private final String assigmentSubId;
}
