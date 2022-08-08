package com.prgrms.artzip.comment.repository;

import com.prgrms.artzip.comment.domain.CommentLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
  @Query("select cl from CommentLike cl where cl.user.id = :userId and cl.comment.id = :commentId")
  Optional<CommentLike> getCommentLikeByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true)
  @Query("delete from CommentLike cl where cl.comment.id = :commentId and cl.user.id = :userId")
  void deleteCommentLikeByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

  @Query("select count(cl) from CommentLike cl where cl.comment.id = :commentId")
  Long countCommentLikeByCommentId(@Param("commentId") Long commentId);
}
