package org.rest.server.service;

import java.util.Map;
import java.util.concurrent.Future;

public interface HeavyLoadedService {

    Future<String> heavyLoadedCall(Long value);

    Future<String> extraHeavyLoadedCall(Long value);
}
