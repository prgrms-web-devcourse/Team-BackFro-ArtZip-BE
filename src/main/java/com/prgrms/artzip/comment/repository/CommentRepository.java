package com.prgrms.artzip.comment.repository;

import com.prgrms.artzip.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
    @Query("SELECT COUNT(c) from Comment c WHERE c.user.id = :userId and c.isDeleted = false")
    Long countByUserId(@Param("userId") Long userId);

    @Query("select c from Comment c where c.parent.id in :parentIds and c.isDeleted = false")
    List<Comment> getCommentsOfParents(List<Long> parentIds);

    @Query(value = "select c from Comment c join fetch c.user where c.parent.id = :parentId",
            countQuery = "select count(c) from Comment c where c.parent.id = :parentId")
    Page<Comment> getCommentsOfParent(Long parentId, Pageable pageable);

    @Query("select count (c) from Comment c where c.review.id = :reviewId and c.isDeleted = false and (c.parent is null or c.parent.isDeleted = false)")
    Integer getCommentCountByReviewId(Long reviewId);
}