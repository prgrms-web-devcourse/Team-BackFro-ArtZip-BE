package com.prgrms.artzip.exhibition.repository;

import com.prgrms.artzip.exhibition.domain.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>,
    ExhibitionCustomRepository {

}
