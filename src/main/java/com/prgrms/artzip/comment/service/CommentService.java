package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.domain.CommentLike;
import com.prgrms.artzip.comment.dto.projection.CommentSimpleProjection;
import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.comment.dto.response.CommentInfo;
import com.prgrms.artzip.comment.dto.response.CommentLikeResponse;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.dto.response.CommentResponseQ;
import com.prgrms.artzip.comment.dto.response.CommentsResponse;
import com.prgrms.artzip.comment.repository.CommentLikeRepository;
import com.prgrms.artzip.comment.repository.CommentRepository;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.error.exception.DuplicateRequestException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {
  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final CommentUtilService commentUtilService;
  private final CommentLikeRepository commentLikeRepository;

  @Transactional(readOnly = true)
  public CommentsResponse getCommentsByReviewId(Long reviewId, User user, Pageable pageable) {
    Page<CommentSimpleProjection> comments = commentRepository.getCommentsByReviewIdQ(reviewId, Objects.nonNull(user) ? user.getId() : null, pageable);
    return new CommentsResponse(new PageResponse<>(comments.map(CommentResponseQ::new)), commentRepository.getCommentCountByReviewId(reviewId));
  }

  public CommentResponse createComment(CommentCreateRequest request, Long reviewId, User user) {
    checkLogin(user);
    Review review = getReview(reviewId);
    Comment parent = null;
    if (Objects.nonNull(request.parentId())) {
      parent = commentUtilService.getComment(request.parentId());
      checkChild(parent);
    }
    Comment comment = commentRepository.save(Comment.builder()
        .content(request.content())
        .user(user)
        .review(review)
        .parent(parent)
        .build()
    );
    return new CommentResponse(comment, user, new ArrayList<>());
  }

  public CommentResponse updateComment(CommentUpdateRequest request, Long commentId, User user) {
    checkLogin(user);
    Comment comment = commentUtilService.getComment(commentId);
    if (comment.getIsDeleted()) throw new DuplicateRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
    checkOwner(comment, user);
    comment.setContent(request.content());
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, user, children);
  }

  public CommentResponse deleteComment(Long commentId, User user) {
    checkLogin(user);
    Comment comment = commentUtilService.getComment(commentId);
    checkOwner(comment, user);
    comment.softDelete();
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, user, children);
  }

  @Transactional(readOnly = true)
  public Page<CommentInfo> getChildren(Long commentId, User user, Pageable pageable) {
    Comment parent = commentUtilService.getComment(commentId);
    checkChild(parent);
    Page<Comment> children = commentRepository.getCommentsOfParent(commentId, pageable);
    return children.map(child -> new CommentInfo(child, user));
  }

  @Transactional(readOnly = true)
  public Long getCommentCountByUserId(Long userId) {
    return commentRepository.countByUserId(userId);
  }

  public CommentLikeResponse toggleCommentLike(Long commentId, User user) {
    checkLogin(user);
    Comment comment = commentUtilService.getComment(commentId);
    Optional<CommentLike> commentLike = commentLikeRepository
        .getCommentLikeByCommentIdAndUserId(commentId, user.getId());
    boolean isLiked;
    if (commentLike.isPresent()) {
      commentLikeRepository.deleteCommentLikeByCommentIdAndUserId(commentId, user.getId());
      isLiked = false;
    } else {
      commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
      isLiked = true;
    }
    return CommentLikeResponse.builder()
        .commentId(commentId)
        .isLiked(isLiked)
        .likeCount(commentLikeRepository.countCommentLikeByCommentId(commentId))
        .build();
  }

  private void checkChild(Comment parent) {
    if (Objects.nonNull(parent.getParent())) {
      throw new InvalidRequestException(ErrorCode.CHILD_CANT_BE_PARENT);
    }
  }

  private Review getReview(Long reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
  }

  private void checkOwner(Comment comment, User user) {
    if (!Objects.equals(comment.getUser().getId(), user.getId())) {
      throw new AuthErrorException(ErrorCode.RESOURCE_PERMISSION_DENIED);
    }
  }

  private void checkLogin(User user) {
    if (Objects.isNull(user)) {
      throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }
  }
}
