package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.ExhibitionLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, ExhibitionLikeId> {

}
