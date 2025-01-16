package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int semester;

    private int tableOrder;

    public static TimeTable of(int year, int semester, User user, int tableOrder) {
        return TimeTable.builder()
                .year(year)
                .semester(semester)
                .user(user)
                .tableOrder(tableOrder)
                .build();
    }

    public void updateOrder(int order) {
        this.tableOrder = order;
    }
}
