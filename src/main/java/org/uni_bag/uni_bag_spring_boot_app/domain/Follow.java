package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User follower;

    @ManyToOne
    private User followee;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isSecondaryFriend = false;

    public static Follow of(final User follower, final User followee){
        return Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
    }

    public void updateSecondaryFriend(boolean isSecondaryFriend){
        this.isSecondaryFriend = isSecondaryFriend;
    }
}
