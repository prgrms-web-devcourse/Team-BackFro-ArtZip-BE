package com.prgrms.artzip.exhibition.repository;

import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

    @Query("SELECT COUNT(EL) from ExhibitionLike EL WHERE EL.exhibition.id = :exhibitionId")
    Long countByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    @Query("select count(el) from ExhibitionLike el where el.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT EL from ExhibitionLike EL WHERE EL.user.id = :userId and EL.exhibition.id = :exhibitionId")
    Optional<ExhibitionLike> findByUserIdAndExhibitionId(@Param("userId") Long userId,
                                                         @Param("exhibitionId") Long exhibitionId);
}
