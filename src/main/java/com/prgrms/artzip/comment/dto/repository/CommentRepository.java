package com.prgrms.artzip.comment.dto.repository;

import com.prgrms.artzip.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
