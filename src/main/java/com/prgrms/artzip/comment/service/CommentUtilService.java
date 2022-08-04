package com.prgrms.artzip.comment.service;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.repository.CommentRepository;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentUtilService {

  private final CommentRepository commentRepository;

  public Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
  }
}
