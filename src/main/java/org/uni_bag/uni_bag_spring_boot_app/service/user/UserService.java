package org.uni_bag.uni_bag_spring_boot_app.service.user;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.user.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserInfoDto getUserInfo(User user) {
        return UserInfoDto.fromEntity(user);
    }

    public UserSearchResponseDto searchUser(User user, String name, String studentId) {
        User foundUser = userRepository.findByNameAndStudentId(name, studentId)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        return UserSearchResponseDto.fromEntity(foundUser);
    }

    public UserTosAgreementResponseDto agreeTos(User user) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(foundUser.isTosAccepted()) {
            throw new HttpErrorException(HttpErrorCode.AlreadyAgreeTosError);
        }

        foundUser.agreeTos();
        return UserTosAgreementResponseDto.createResponse();
    }

    public UserTosRescissionResponseDto rescindTos(User user) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        foundUser.rescindTos();
        foundUser.deleteEmsInformation();
        return UserTosRescissionResponseDto.createResponse();
    }


    public UserEmsLoginCompleteResponseDto completeEmsLogin(User user, @Valid UserEmsLoginCompleteRequestDto requestDto) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(foundUser.isEmsLoggedIn()){
            throw new HttpErrorException(HttpErrorCode.AlreadyEmsLoginError);
        }

        foundUser.completeEmsLogin(requestDto);
        return UserEmsLoginCompleteResponseDto.createResponse();
    }
}
