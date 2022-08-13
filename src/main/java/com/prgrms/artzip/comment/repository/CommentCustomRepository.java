package com.prgrms.artzip.comment.repository;

import com.prgrms.artzip.comment.dto.projection.CommentSimpleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {

  Page<CommentSimpleProjection> getCommentsByReviewIdQ(Long reviewId, Long userId, Pageable pageable);
}
