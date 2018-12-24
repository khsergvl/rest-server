package org.rest.server.repository;

import org.rest.server.entity.Execution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ExecutionRepository extends CrudRepository<Execution, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT IGNORE INTO execution (id, execution, ts) VALUES (:id, :execution, :ts)", nativeQuery = true)
    void insertIfnotExist(@Param("id") long id, @Param("execution") String execution, @Param("ts") long ts);
}
