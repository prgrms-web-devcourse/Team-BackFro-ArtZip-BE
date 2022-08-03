package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

  @Query("SELECT COUNT(EL) from ExhibitionLike EL WHERE EL.exhibition.id = :exhibitionId")
  Long countByExhibitionId(@Param("exhibitionId") Long exhibitionId);

  @Query("SELECT EL from ExhibitionLike EL WHERE EL.exhibition.id = :exhibitionId and EL.user.id = :userId")
  Optional<ExhibitionLike> findByExhibitionIdAndUserId(@Param("exhibitionId") Long exhibitionId,
      @Param("userId") Long userId);
}
