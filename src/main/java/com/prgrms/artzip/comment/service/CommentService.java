package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Long getCommentCountByUserId(Long userId) {
        return commentRepository.countByUser(userId);
    }
}
