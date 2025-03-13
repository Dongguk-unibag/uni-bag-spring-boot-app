package org.uni_bag.uni_bag_spring_boot_app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.uni_bag.uni_bag_spring_boot_app.constant.SnsType;
import org.uni_bag.uni_bag_spring_boot_app.dto.auth.oauth.userInfo.OAuthUserInfoDto;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserEmsLoginCompleteRequestDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Entity(name = "users")
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class User implements OAuth2User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String snsId;

    @Column(nullable = false, length = 50)
    private SnsType snsType;

    @Column(length = 20)
    private String studentId;

    @Column()
    private String name;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isTosAccepted = false;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isEmsLoggedIn = false;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean isAdmin = false;

    private String authority;


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton((GrantedAuthority) () -> authority);
    }

    @Override
    public String getName() {
        return name;
    }

    public void agreeTos(){
        this.isTosAccepted = true;
    }

    public void rescindTos(){
        this.isTosAccepted = false;
    }

    public void completeEmsLogin(UserEmsLoginCompleteRequestDto requestDto){
        this.isEmsLoggedIn = true;
        this.name = requestDto.getName();
        this.studentId = requestDto.getStudentId();
    }

    public void deleteEmsInformation(){
        this.isEmsLoggedIn = false;
        this.name = null;
        this.studentId = null;
    }

    public static User from(OAuthUserInfoDto dto){
        return User.builder()
                .snsId(dto.getSnsId())
                .snsType(dto.getSnsType())
                .studentId(null)
                .name(null)
                .build();
    }
}
