package com.prgrms.artzip.comment.domain.repository;

import com.prgrms.artzip.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) from Comment c WHERE c.user.id = :userId")
    Long countByUser(@Param("userId") Long userId);
}
