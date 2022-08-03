package com.prgrms.artzip.comment.dto.request;

import javax.validation.constraints.NotBlank;

public record CommentCreateRequest(@NotBlank String content, Long parentId) {
}
