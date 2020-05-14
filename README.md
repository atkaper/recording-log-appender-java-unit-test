# Recording Log Appender for Java Unit Testing

See also blog post: https://www.kaper.com/java/recording-log-appender-for-java-unit-testing/

A simple Java Utility class to help out in Unit Testing the proper functioning of log statements in code under test - Java, Spring-Boot, Logback.

The code of interest is in these files:
```
# The log recorder class:
src/test/java/com/example/demo/MyRecordingLogAppender.java

# The dummy service implementation to run a unit test for:
src/main/java/com/example/demo/SomeService.java

# The unittest for the dummy service, to demo the use of the log recorder:
src/test/java/com/example/demo/SomeServiceTest.java
```

The "trick" of the MyRecordingLogAppender helper class is that it extends a normal ch.qos.logback.core.ConsoleAppender (line 28), and as soon as you create an instance of the helper, it will remove all existing console appenders (line 36), and put itself in there as the only appender (line 37).

Then in the doAppend method (line 45), the log events will arrive. And those are stored in a HashMap with a List of messages per log level.

The exposed methods to use in your tests are:
```
clearMessages - to wipe all stored log data.
containsMessage - to check if a certain message matching a substring exists.
findMessages - get collected log messages which match a given substring.
```

Example run:
```
./mvnw clean install
```

And some example output:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.7.RELEASE)

2020-05-13 21:51:57.324  INFO 16671 --- [           main] com.example.demo.SomeServiceTest         : Starting SomeServiceTest on tk42 with PID 16671 (started by thijs in /home/thijs/webdemo/demo)
2020-05-13 21:51:57.325  INFO 16671 --- [           main] com.example.demo.SomeServiceTest         : No active profile set, falling back to default profiles: default
2020-05-13 21:51:57.604  INFO 16671 --- [           main] com.example.demo.SomeServiceTest         : Started SomeServiceTest in 0.427 seconds (JVM running for 1.024)
Wed May 13 21:51:57 CEST 2020 INFO >>> Test SomeService Hello!
  {arg=Hello!, correlationId=myServiceTest-1589399517743}
Wed May 13 21:51:57 CEST 2020 ERROR >>> SomeFailure!
  java.lang.RuntimeException Oops
  {arg=Hello!, correlationId=myServiceTest-1589399517743}
Wed May 13 21:51:57 CEST 2020 INFO >>> Test SomeService Foo?
  {correlationId=myServiceTest-1589399517743, arg=Foo?}
Wed May 13 21:51:57 CEST 2020 ERROR >>> SomeFailure!
  java.lang.RuntimeException Oops
  {correlationId=myServiceTest-1589399517743, arg=Foo?}
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.902 s - in com.example.demo.SomeServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

