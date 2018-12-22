package org.rest.server.service;

import org.rest.server.entity.Execution;
import java.util.Map;

public interface ExecutionService {

    Map<Long, String> execute(Long[] values);

    Iterable<Execution> fetchExecutions(Long[] values);

    default String combineLoad(String s1, String s2) {
        return s1 + " " + s2;
    }
}
