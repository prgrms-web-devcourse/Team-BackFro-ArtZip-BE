package com.prgrms.artzip.comment.repository;

import com.prgrms.artzip.comment.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  @Query(value = "select c from Comment c left join fetch c.user where c.review.id = :reviewId and c.parent is null",
  countQuery = "select count(c) from Comment c where c.review.id = :reviewId and c.parent is null")
  Page<Comment> getCommentsByReviewId(Long reviewId, Pageable pageable);

  @Query("select c from Comment c join fetch c.user where c.parent.id in :parentIds and c.parent is not null")
  List<Comment> getCommentsOfParents(List<Long> parentIds);

  @Modifying(clearAutomatically = true)
  @Query("update Comment c set c.isDeleted = true where c.id = :commentId")
  int deleteCommentById(Long commentId);
}
