package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test SomeService. There is a silly amount of comments in here ;-)
 * That's not normal practice, just here to explain the use of the MyRecordingLogAppender a bit.
 */
@SpringBootTest
public class SomeServiceTest {

    @Autowired
    private SomeService someService;

    @Test
    public void myServiceTest() {
        // Just for fun, set a correlationId to match shown logs for this test to each other.
        MDC.put("correlationId", "myServiceTest-" + System.currentTimeMillis());

        // Start up the log collector, it disables other log appenders, and stores all log messages internally.
        final MyRecordingLogAppender myAppender = new MyRecordingLogAppender();

        // Check that our expected log message is NOT yet in the appender.
        assertThat(myAppender.containsMessage("INFO", "Hello")).isFalse();

        // Execute the service under test, which should add data to the log.
        someService.someAction("Hello!");

        // Now check that the expected log entry was created.
        assertThat(myAppender.containsMessage("INFO", "Hello")).isTrue();

        // You can clear the list of recorded messages, in case you need a clean slate for a next test in the same method.
        myAppender.clearMessages();

        // Prove that our previous log message is REMOVED from the appender.
        assertThat(myAppender.containsMessage("INFO", "Hello")).isFalse();

        // Execute the service under test a second time, which should add data again to the log.
        someService.someAction("Foo?");

        // You can also "search" for messages, to get the message text.
        List<String> messages = myAppender.findMessages("ERROR", "Fail");

        // For further use afterwards...
        assertThat(messages.get(0)).isEqualTo("SomeFailure!");

        // Or check how many matching ones we got...
        assertThat(messages.size()).isEqualTo(1);

        // Remove MDC correlationId
        MDC.remove("correlationId");
    }

}
