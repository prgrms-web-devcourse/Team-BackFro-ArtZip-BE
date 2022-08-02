package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.repository.CommentRepository;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {
  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;

  public Page<CommentResponse> getCommentsByReviewId(Long reviewId, Pageable pageable) {
    Page<Comment> parents = commentRepository.getCommentsByReviewId(reviewId, pageable);
    List<Comment> children = commentRepository
        .getCommentsOfParents(parents.map(Comment::getId).toList());
    return parents.map(
        p -> new CommentResponse(
            p, children.stream()
                .filter(c -> Objects.equals(c.getParent().getId(), p.getId()))
                .toList()
        )
    );
  }

  public CommentResponse createComment(CommentCreateRequest request, Long reviewId, Long userId) {
    Review review = getReview(reviewId);
    User user = getUser(userId);
    Comment parent = null;
    if (Objects.nonNull(request.parentId())) {
      parent = getComment(request.parentId());
    }
    Comment comment = commentRepository.save(Comment.builder()
        .content(request.content())
        .user(user)
        .review(review)
        .parent(parent)
        .build()
    );
    return new CommentResponse(comment, new ArrayList<>());
  }

  public CommentResponse updateComment(CommentUpdateRequest request, Long commentId) {
    Comment comment = getComment(commentId);
    comment.setContent(request.content());
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, children);
  }

  public CommentResponse deleteComment(Long commentId) {
    Comment comment = getComment(commentId);
    comment.softDelete();
    List<Comment> children = commentRepository.getCommentsOfParents(List.of(commentId));
    return new CommentResponse(comment, children);
  }

  private Review getReview(Long reviewId) {
    Optional<Review> opt = reviewRepository.findById(reviewId);
    if (opt.isEmpty()) {
      throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
    }
    return opt.get();
  }

  private User getUser(Long userId) {
    Optional<User> opt = userRepository.findById(userId);
    if (opt.isEmpty()) {
      throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
    }
    return opt.get();
  }

  private Comment getComment(Long commentId) {
    Optional<Comment> opt = commentRepository.findById(commentId);
    if (opt.isEmpty()) {
      throw new NotFoundException(ErrorCode.COMMENT_NOT_FOUND);
    }
    return opt.get();
  }
}
