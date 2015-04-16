import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %logger{35} - %msg%n"
    }
}

appender("FILE", FileAppender) {
    file = "log/message.log"

    filter(LevelFilter) {
        level = INFO
        onMatch = ACCEPT
        onMismatch = DENY
    }

    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative [%thread] - %msg%n"
        pattern = "%d{HH:mm:ss.SSS} %-4relative %-5level [%thread] %logger{35} - %msg%n"
    }
}

root(DEBUG, ["CONSOLE", "FILE"])