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

    private final HeavyLoadedService heavyLoadedService;
    private final ExecutionRepository repository;

    public ExecutionServiceImpl(HeavyLoadedService heavyLoadedService, ExecutionRepository repository) {
        this.heavyLoadedService = heavyLoadedService;
        this.repository = repository;
    }

    @Override
    public Map<Long, String> execute(Long[] values) {
        Map<Long, String> executions = new HashMap<>();
        Queue<ExecutionFuture> executionQueue = new ConcurrentLinkedQueue<ExecutionFuture>();

        for (Long value : values) {
            log.info("Evaluate payload for value: " + value + " in the thread " + Thread.currentThread().getName());
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
    public Iterable<Execution> fetchExecutions(Long[] values) {
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
                repository.insertIfnotExist(execution.getKey(), execution.getValue(), System.currentTimeMillis());
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
