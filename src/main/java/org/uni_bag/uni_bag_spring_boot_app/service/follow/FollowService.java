package org.uni_bag.uni_bag_spring_boot_app.service.follow;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uni_bag.uni_bag_spring_boot_app.config.HttpErrorCode;
import org.uni_bag.uni_bag_spring_boot_app.domain.Follow;
import org.uni_bag.uni_bag_spring_boot_app.domain.User;
import org.uni_bag.uni_bag_spring_boot_app.dto.friend.*;
import org.uni_bag.uni_bag_spring_boot_app.exception.HttpErrorException;
import org.uni_bag.uni_bag_spring_boot_app.repository.FollowRepository;
import org.uni_bag.uni_bag_spring_boot_app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowResponseDto follow(User follower, FollowRequestDto followRequestDto  ) {
        User followee = userRepository.findById(followRequestDto.getFollowee())
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        if(followRepository.existsByFollowerAndFollowee(follower, followee)){
            throw new HttpErrorException(HttpErrorCode.AlreadyExistFollowError);
        }

        followRepository.save(Follow.of(follower, followee));

        return FollowResponseDto.of(followee);
    }

    public UnfollowResponseDto unfollow(User follower, @Valid UnfollowRequestDto unfollowRequestDto) {
        User followee = userRepository.findById(unfollowRequestDto.getFollowee())
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.UserNotFoundError));

        Follow foundFollow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new HttpErrorException(HttpErrorCode.NoSuchFollowError));

        followRepository.delete(foundFollow);

        return UnfollowResponseDto.of(followee);
    }

    public FolloweeListReadResponseDto getFolloweeList(User follower) {
        List<Follow> follows = followRepository.findAllByFollower(follower);
        return FolloweeListReadResponseDto.of(follows);
    }
}
