package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.domain.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, ExhibitionCustomRepository {

}
