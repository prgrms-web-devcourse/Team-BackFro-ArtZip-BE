package com.prgrms.artzip.comment.controller;

import com.prgrms.artzip.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
  private final CommentService commentService;
}
