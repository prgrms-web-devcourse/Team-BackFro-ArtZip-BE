package com.prgrms.artzip.comment.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.prgrms.artzip.comment.domain.repository.CommentRepository;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private User user = new User("test@gmail.com", "Emily", List.of(new Role(Authority.USER)));

    @Test
    @DisplayName("유저가 작성한 댓글 개수 반환 테스트")
    void testGetCommentCountByUserId() {
        // given
        when(commentRepository.countByUserId(1L)).thenReturn(3L);
        // when
        Long commentCount = commentService.getCommentCountByUserId(1L);
        // then
        assertThat(commentCount).isEqualTo(3L);
        verify(commentRepository).countByUserId(1L);
    }
}