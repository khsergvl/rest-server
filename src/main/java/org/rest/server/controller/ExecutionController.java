package org.rest.server.controller;

import org.rest.server.entity.Execution;
import org.rest.server.service.ExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ExecutionController {

    private ExecutionService service;

    public ExecutionController(ExecutionService service) {
        this.service = service;
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<Long, String>> postExecutions(@RequestBody Long[] values) {
        if (values.length > 0) {
            return new ResponseEntity<Map<Long, String>>(service.execute(values), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<Map<Long, String>>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/execute")
    public Iterable<Execution> getExecutions(@RequestBody(required = false) Long[] values) {
        return service.fetchExecutions(values);
    }
}
