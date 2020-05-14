package com.example.demo;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * Custom log appender, which stores all log messages per level in a list.
 * You can create an instance of this in your unit test, run your test, and then use
 * this class to look for specific log messages from the component under test.
 *
 * Can be useful for cases where you need to know if the tested piece of code does
 * properly write a required log message in a certain test case.
 *
 * Of course you could enhance this class with regex-matching, and nicer logging to standard out.
 * Or setting a maximum number of messages to store. Or you can store log exceptions also if you want.
 * Or... store the MDCPropertyMap also for checking.
 */
public class MyRecordingLogAppender extends ConsoleAppender {
    private final Map<String, List<String>> logMessagesByLevel = new HashMap<>();

    /**
     * Create appender, and connect to log framework instead of any other appenders.
     */
    public MyRecordingLogAppender() {
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.addAppender(this);
    }

    /**
     * Called for every log statement. Store message in the list, and send to standard output.
     * @param eventObject log event
     */
    @Override
    public void doAppend(final Object eventObject) {
        final LoggingEvent loggingEvent = (LoggingEvent) eventObject;
        final String formattedMessage = loggingEvent.getFormattedMessage();
        final String level = loggingEvent.getLevel().levelStr;

        // Show level and message on standard out
        System.out.println(new Date() + " " + level + " >>> " + formattedMessage);

        // If we have a cause, show it's class and message
        final IThrowableProxy throwableProxy = loggingEvent.getThrowableProxy();
        if (throwableProxy != null) {
            System.out.println("  " + throwableProxy.getClassName() + " " + throwableProxy.getMessage());
        }

        // If we have MDC log context, show it also
        Map<String, String> mdcPropertyMap = loggingEvent.getMDCPropertyMap();
        if (!mdcPropertyMap.isEmpty()) {
            System.out.println("  " + mdcPropertyMap);
        }

        // Store message in memory
        final List<String> messages = logMessagesByLevel.getOrDefault(level, new ArrayList<>());
        logMessagesByLevel.put(level, messages);
        messages.add(formattedMessage);
    }

    /**
     * Remove all stored messages. You can run this in your test method between two separate test/check cases.
     * Or if you are writing an insane number of log messages in your test, causing out of memory otherwise.
     */
    public void clearMessages() {
        logMessagesByLevel.clear();
    }

    /**
     * Check if a message with given substring and level exists.
     * @param level     level
     * @param substring (partial) text to search for
     * @return true if any matching message found
     */
    public boolean containsMessage(final String level, final String substring) {
        return (!findMessages(level, substring).isEmpty());
    }

    /**
     * Retrieve a list of matching messages with given substring and level.
     * @param level     level
     * @param substring (partial) text to search for
     * @return List of matching message, or empty list if none found
     */
    public List<String> findMessages(final String level, final String substring) {
        List<String> result = new ArrayList<>();
        final List<String> messages = logMessagesByLevel.getOrDefault(level, new ArrayList<>());
        for (final String message : messages) {
            if (message.contains(substring)) {
                result.add(message);
            }
        }
        return result;
    }

}