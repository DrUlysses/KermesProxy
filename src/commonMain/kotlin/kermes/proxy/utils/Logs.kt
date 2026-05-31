package kermes.proxy.utils

import io.github.oshai.kotlinlogging.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.Buffer
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.time.Clock
import kotlin.time.Instant

private var loggerInitialized: Boolean = false

private fun Int.padZeros() = toString().padStart(2, '0')

private fun Instant.toLogString() = toLocalDateTime(TimeZone.currentSystemDefault()).let {
    "${it.day.padZeros()}-${it.month.number.padZeros()}-${it.year} ${it.hour.padZeros()}:${it.minute.padZeros()}:${it.second.padZeros()}"
}

fun initFileLogging(
    fileName: String = "current.log",
    alsoPrintToConsole: Boolean = true
) {
    val loggerDir = workingDir + Path("logs")
    SystemFileSystem.createDirectories(loggerDir)
    KotlinLoggingConfiguration.loggerFactory = object : KLoggerFactory {
        override fun logger(name: String): KLogger = object : KLogger {
            var notWrittenMessages: List<String>? = null
            
            override val name: String = name

            override fun isLoggingEnabledFor(
                level: Level,
                marker: Marker?
            ): Boolean = true

            override fun at(
                level: Level,
                marker: Marker?,
                block: KLoggingEventBuilder.() -> Unit
            ) {
                if (level == Level.OFF) {
                    return
                }
                val event = KLoggingEvent(
                    level = level,
                    marker = marker,
                    loggerName = name,
                    eventBuilder = KLoggingEventBuilder().apply { block() }
                )
                try {
                    val formatted = KotlinLoggingConfiguration.direct.formatter.formatMessage(event)
                    val formattedWithTimeStamp = "${Clock.System.now().toLogString()} $formatted\n"
                    if (loggerInitialized) {
                        fun writeMessage(message: String) {
                            val bytes = message.encodeToByteArray()
                            Buffer().use { buffer ->
                                buffer.write(
                                    source = bytes,
                                    startIndex = 0,
                                    endIndex = bytes.size
                                )
                                SystemFileSystem.sink(
                                    path = loggerDir + Path(fileName),
                                    append = true
                                ).use {
                                    it.write(
                                        source = buffer,
                                        byteCount = bytes.size.toLong()
                                    )
                                }
                            }
                        }
                        if (!notWrittenMessages.isNullOrEmpty()) {
                            notWrittenMessages?.forEach { 
                                writeMessage(it)
                            }
                            notWrittenMessages = null
                        }
                        writeMessage(formattedWithTimeStamp)
                    } else {
                        notWrittenMessages = (notWrittenMessages ?: listOf()) + formattedWithTimeStamp
                    }
                    if (alsoPrintToConsole) {
                        println(formatted)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    loggerInitialized = true
}
