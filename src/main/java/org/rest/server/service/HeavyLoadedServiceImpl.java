package org.rest.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HeavyLoadedServiceImpl implements HeavyLoadedService {

    @Override
    @Async("threadPoolTaskExecutor")
    public AsyncResult<String> heavyLoadedCall(Long value) {
        String result = "Hello, " + value + "!";
        log.info("Executing call asynchronously in the thread " + Thread.currentThread().getName() + " to get value: \""
                 + result + "\"");

        try {
            Thread.sleep(Double.valueOf(Math.random() * 2000).longValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Heavy Loaded Call thread InterruptedException:", e);
        }

        return new AsyncResult<String>(result);
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public AsyncResult<String> extraHeavyLoadedCall(Long value) {
        String result = value + "*2 = " + (value * 2) + ".";
        log.info("Executing call asynchronously in the thread " + Thread.currentThread().getName() + " to get value: \""
                 + result + "\"");

        try {
            Thread.sleep(Double.valueOf(Math.random() * 2000).longValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Extra Heavy Loaded Call thread InterruptedException:", e);
        }

        return new AsyncResult<String>(result);
    }
}
