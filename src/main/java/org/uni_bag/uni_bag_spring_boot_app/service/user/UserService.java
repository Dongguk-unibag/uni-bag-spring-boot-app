package org.uni_bag.uni_bag_spring_boot_app.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.UserInfoDto;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    public UserInfoDto getUserInfo(User user) {
        return UserInfoDto.fromEntity(user);
    }
}
