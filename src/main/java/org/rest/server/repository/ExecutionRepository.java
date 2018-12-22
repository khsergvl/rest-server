package org.rest.server.repository;

import org.rest.server.entity.Execution;
import org.springframework.data.repository.CrudRepository;

public interface ExecutionRepository extends CrudRepository<Execution, Long> {}
