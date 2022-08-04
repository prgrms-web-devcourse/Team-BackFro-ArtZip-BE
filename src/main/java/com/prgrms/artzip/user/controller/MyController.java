package com.prgrms.artzip.user.controller;

import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.exibition.service.ExhibitionLikeService;
import com.prgrms.artzip.exibition.service.ExhibitionService;
import com.prgrms.artzip.review.service.ReviewLikeService;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@Api(tags = {"users-me"})
@RestController
@RequestMapping("api/v1/users/me")
@SuppressWarnings({"rawtypes", "unchecked"})
public class MyController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final UserService userService;

    private final ReviewService reviewService;

    private final CommentService commentService;

    private final ExhibitionService exhibitionService;

    private final ExhibitionLikeService exhibitionLikeService;

    private final ReviewLikeService reviewLikeService;

    public MyController(UserService userService, ReviewService reviewService, CommentService commentService, ExhibitionService exhibitionService, ExhibitionLikeService exhibitionLikeService, ReviewLikeService reviewLikeService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.commentService = commentService;
        this.exhibitionService = exhibitionService;
        this.exhibitionLikeService = exhibitionLikeService;
        this.reviewLikeService = reviewLikeService;
    }

    @GetMapping("/info")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo (@CurrentUser User user) {
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .email(user.getEmail())
                .reviewCount(reviewService.getReviewCountByUserId(user.getId()))
                .exhibitionLikeCount(exhibitionLikeService.getExhibitionLikeCountByUserId(user.getId()))
                .reviewLikeCount(reviewLikeService.getReviewLikeCountByUserId(user.getId()))
                .commentCount(commentService.getCommentCountByUserId(user.getId())).build();
        ApiResponse apiResponse = ApiResponse.builder()
                .message("유저 정보 조회 성공하였습니다.")
                .status(OK.value())
                .data(userResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
