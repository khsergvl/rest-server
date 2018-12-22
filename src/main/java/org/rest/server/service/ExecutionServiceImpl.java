package org.rest.server.service;

import static java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rest.server.entity.Execution;
import org.rest.server.repository.ExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ExecutionServiceImpl implements ExecutionService {

    private final Map<Long, String> executions;

    private final HeavyLoadedService heavyLoadedService;
    private final ExecutionRepository repository;
    private final Queue<ExecutionFuture> executionQueue;

    public ExecutionServiceImpl(HeavyLoadedService heavyLoadedService, ExecutionRepository repository) {
        this.executions = new HashMap<>();
        this.heavyLoadedService = heavyLoadedService;
        this.repository = repository;
        this.executionQueue = new ConcurrentLinkedQueue<ExecutionFuture>();
    }

    @Override
    public synchronized Map<Long, String> execute(Long[] values) {
        executions.clear();

        for (Long value : values) {
            log.info("Evaluate payload for value: " + value);
            executionQueue.add(new ExecutionFuture(
                    value, heavyLoadedService.heavyLoadedCall(value), heavyLoadedService.extraHeavyLoadedCall(value)));
        }

        while (!executionQueue.isEmpty()) {
            try {
                ExecutionFuture executionFuture = executionQueue.peek();

                if (executionFuture.getHeavyLoadedCall().isDone()
                        && executionFuture.getExtraHeavyLoadedCall().isDone()) {
                    String combinedLoad = combineLoad(executionFuture.getHeavyLoadedCall().get(),
                            executionFuture.getExtraHeavyLoadedCall().get());

                    executions.merge(executionFuture.getValue(), combinedLoad, (oldVal, newVal) -> oldVal + newVal);

                    executionQueue.remove();
                }
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Execute thread Exception: ", e);
            }
        }

        persistExecutions(executions);

        return executions;
    }

    @Override
    public synchronized Iterable<Execution> fetchExecutions(Long[] values) {
        try {
            if (values != null && values.length > 0) {
                return repository.findAllById(Arrays.asList(values));
            }
            else {
                return repository.findAll();
            }
        } catch (Exception e) {
            log.warn("Exception during persistence operations: ", e);
            return new ArrayList<Execution>();
        }
    }

    private void persistExecutions(Map<Long, String> executions) {
        for (Entry<Long, String> execution : executions.entrySet()) {
            try {
                long id = execution.getKey();

                if (!repository.findById(id).isPresent()) {
                    repository.save(new Execution(id, execution.getValue(), System.currentTimeMillis()));
                }
            } catch (Exception e) {
                log.warn("Exception during persistence operations: ", e);
            }
        }
    }

    @Data
    @AllArgsConstructor
    static class ExecutionFuture {
        private long value;
        private Future<String> heavyLoadedCall;
        private Future<String> extraHeavyLoadedCall;
    }
}
