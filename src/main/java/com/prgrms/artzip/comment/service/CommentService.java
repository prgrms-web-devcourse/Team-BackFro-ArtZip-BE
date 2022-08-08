package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.comment.dto.response.CommentInfo;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.repository.CommentRepository;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

  @Transactional(readOnly = true)
  public Page<CommentResponse> getCommentsByReviewId(Long reviewId, Pageable pageable) {
    Page<Comment> parents = commentRepository.getCommentsByReviewId(reviewId, pageable);
    List<Comment> children = parents.getSize() > 0 ? commentRepository
        .getCommentsOfParents(parents.map(Comment::getId).toList()) : new ArrayList<>();
    return parents.map(
        p -> new CommentResponse(
            p, children.stream()
                .filter(c -> Objects.equals(c.getParent().getId(), p.getId()))
                .toList()
        )
    );
  }

  public CommentResponse createComment(CommentCreateRequest request, Long reviewId, User currentUser) {
    Review review = getReview(reviewId);
    Comment parent = null;
    if (Objects.nonNull(request.parentId())) {
      parent = commentUtilService.getComment(request.parentId());
      checkChild(parent);
    }
    Comment comment = commentRepository.save(Comment.builder()
        .content(request.content())
        .user(currentUser)
        .review(review)
        .parent(parent)
        .build()
    );
    return new CommentResponse(comment, new ArrayList<>());
  }

  public CommentResponse updateComment(CommentUpdateRequest request, Long commentId, User user) {
    Comment comment = commentUtilService.getComment(commentId);
    checkOwner(comment, user);
    comment.setContent(request.content());
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, children);
  }

  public CommentResponse deleteComment(Long commentId, User user) {
    Comment comment = commentUtilService.getComment(commentId);
    checkOwner(comment, user);
    comment.softDelete();
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, children);
  }

  @Transactional(readOnly = true)
  public Page<CommentInfo> getChildren(Long commentId, Pageable pageable) {
    Comment parent = commentUtilService.getComment(commentId);
    checkChild(parent);
    Page<Comment> children = commentRepository.getCommentsOfParent(commentId, pageable);
    return children.map(CommentInfo::new);
  }

  @Transactional(readOnly = true)
  public Long getCommentCountByUserId(Long userId) {
    return commentRepository.countByUserId(userId);
  }

  @Transactional(readOnly = true)
  public Long getCommentCountByReviewId(Long reviewId) {
    return commentRepository.countByReviewId(reviewId);
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
}
