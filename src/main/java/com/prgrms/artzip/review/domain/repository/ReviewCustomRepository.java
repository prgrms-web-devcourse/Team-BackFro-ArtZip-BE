package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

  /**
   * Retrieves a review DTO by review id.
   * @param reviewId must not be null
   * @param userId id of the logged-in user
   * @return the ReviewWithLikeAndCommentCount DTO
   */
  Optional<ReviewWithLikeAndCommentCount> findReviewByReviewId(Long reviewId, Long userId);

  /**
   * returns all reviews(ReviewWithLikeAndCommentCount DTO) with the given parameters.
   * @param exhibitionId
   * @param userId id of the logged-in user
   * @param pageable must not be null
   * @return the list of ReviewWithLikeAndCommentCount DTO with pagination
   */
  Page<ReviewWithLikeAndCommentCount> findReviews(
      Long exhibitionId, Long userId, Pageable pageable);

  /**
   * returns all reviews(ReviewWithLikeAndCommentCount DTO) with the given parameters.
   * @param currentUserId id of the logged-in user
   * @param targetUserId must not be null
   * @param pageable must not be null
   * @return the list of ReviewWithLikeAndCommentCount DTO with pagination
   */
  Page<ReviewWithLikeAndCommentCount> findMyLikesReviews(
      Long currentUserId, Long targetUserId, Pageable pageable);

  /**
   * returns all reviews(ReviewWithLikeAndCommentCount DTO) with the given parameters.
   * @param currentUserId id of the logged-in user
   * @param targetUserId must not be null
   * @param pageable must not be null
   * @return the list of ReviewWithLikeAndCommentCount DTO with pagination
   */
  Page<ReviewWithLikeAndCommentCount> findMyReviews(
      Long currentUserId, Long targetUserId, Pageable pageable);
}
