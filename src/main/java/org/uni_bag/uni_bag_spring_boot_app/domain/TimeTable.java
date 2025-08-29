package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "time_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private int academicYear;

    @Column(nullable = false)
    private int semester;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isPrimary = false;

    public static TimeTable of(int year, int semester, User user, int tableOrder) {
        return TimeTable.builder()
                .academicYear(year)
                .semester(semester)
                .user(user)
                .build();
    }

    public void updatePrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
