package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Dummy Service component to test the unit test.
 * This service writes some information to the log, and the unit test will check if that actually happens.
 */
@Component
public class SomeService {

    /**
     * Ok, I normally just use the lombok annotation to add the logger.
     * Does not matter for the functioning of this example.
     */
    private static final Logger log = LoggerFactory.getLogger(SomeService.class);

    /**
     * Some dummy service method to test.
     * It just logs some data for this example.
     * @param argument data to log
     */
    public void someAction(String argument) {
        MDC.put("arg", argument);

        log.info("Test SomeService {}", argument);
        log.error("SomeFailure!", new RuntimeException("Oops"));

        MDC.remove("arg");
    }

}
