package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Long getCommentCountByUserId(Long userId) {
        return commentRepository.countByUser(userId);
    }
}
