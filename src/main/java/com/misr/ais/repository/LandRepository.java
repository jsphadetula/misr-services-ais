package com.misr.ais.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.misr.ais.model.Land;

public interface LandRepository extends CrudRepository<Land, Long> {

  @Query(value = "SELECT l FROM Land l", countProjection = "1")
  Page<Land> findAllPaged(Pageable pageable);

}
