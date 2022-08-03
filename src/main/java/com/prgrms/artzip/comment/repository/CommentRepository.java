package com.prgrms.artzip.comment.repository;

import com.prgrms.artzip.comment.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query(value = "select c from Comment c left join fetch c.user where c.review.id = :reviewId and c.parent is null",
      countQuery = "select count(c) from Comment c where c.review.id = :reviewId and c.parent is null")
  Page<Comment> getCommentsByReviewId(Long reviewId, Pageable pageable);

  @Query("select c from Comment c join fetch c.user where c.parent.id in :parentIds")
  List<Comment> getCommentsOfParents(List<Long> parentIds);

  @Query(value = "select c from Comment c join fetch c.user where c.parent.id = :parentId",
      countQuery = "select count(c) from Comment c where c.parent.id = :parentId")
  Page<Comment> getCommentsOfParent(Long parentId, Pageable pageable);
}
