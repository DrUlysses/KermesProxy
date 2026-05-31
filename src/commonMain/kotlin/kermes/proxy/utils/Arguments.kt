package kermes.proxy.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

private val logger = KotlinLogging.logger {}

enum class Argument(val value: String) {
    ConfigPath("config"),
    WorkDir("workdir"),
    ConfigValue("set"),
    NoVersionCheck("no-version-check");

    companion object {
        fun byValueOrNull(
            value: String
        ): Argument? = Argument.entries.firstOrNull { entry ->
            entry.value == value
        }
    }
}

fun parseArguments(
    args: Array<String>
): Map<Argument, String> = buildMap {
    var index = 0
    while (args.getOrNull(index) != null) {
        val entry = args[index]
        if (entry.startsWith("--")) {
            when (Argument.byValueOrNull(entry.substringAfter("--"))) {
                Argument.ConfigPath -> {
                    val configPath = Path(args.getOrNull(++index) ?: ".")
                    val parentDir = configPath.parent
                    if (!SystemFileSystem.exists(configPath) && parentDir != null) {
                        logger.debug { "Config path parent $parentDir does not exists. Creating one." }
                        runCatching {
                            SystemFileSystem.createDirectories(
                                path = parentDir,
                                mustCreate = true
                            )
                        }.getOrElse { 
                            logger.error(it) { "Failed to create config path parent $parentDir." }
                        }
                    }
                    
                    put(Argument.ConfigPath, configPath.toString())
                }

                Argument.WorkDir -> {
                    val workDirPath = Path(args.getOrNull(++index) ?: ".")
                    if (!SystemFileSystem.exists(workDirPath)) {
                        logger.warn { "Working directory $workDirPath does not exists. Creating one." }
                        runCatching {
                            SystemFileSystem.createDirectories(
                                path = workDirPath,
                                mustCreate = true
                            )
                        }.getOrElse {
                            logger.error(it) { "Failed to create working directory $workDirPath." }
                        }
                    }

                    put(Argument.WorkDir, workDirPath.toString())
                }

                Argument.ConfigValue -> {
                    val keyValue = (args.getOrNull(++index) ?: continue)
                    put(Argument.ConfigValue, keyValue)
                }

                Argument.NoVersionCheck -> {
                    put(Argument.NoVersionCheck, "")
                }

                null -> {
                    logger.debug { "Finished arguments parse" }
                    return@buildMap
                }
            }
        }
    }
}
