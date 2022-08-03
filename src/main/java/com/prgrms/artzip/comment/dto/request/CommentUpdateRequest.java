package com.prgrms.artzip.comment.dto.request;

import javax.validation.constraints.NotBlank;

public record CommentUpdateRequest(@NotBlank String content) {
}
