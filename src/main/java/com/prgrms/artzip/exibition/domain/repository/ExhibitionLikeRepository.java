package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.ExhibitionLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, ExhibitionLikeId> {

  @Query("SELECT COUNT(EL) from ExhibitionLike EL WHERE EL.exhibition.id = :exhibitionId")
  Long countByExhibitionId(@Param("exhibitionId") Long exhibitionId);
}
