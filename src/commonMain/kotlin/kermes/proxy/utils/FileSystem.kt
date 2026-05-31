package kermes.proxy.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator

private val logger = KotlinLogging.logger {}

var workingDir: Path = Path(".")

operator fun Path.plus(other: Path): Path {
    if (SystemFileSystem.metadataOrNull(this)?.isRegularFile == true) {
        "Can only concatenate directories. Got: $this".let { message ->
            logger.error { message }
            error(message)
        }
    }
    
    return Path(
        toString().trimEnd(SystemPathSeparator) + 
                SystemPathSeparator +
                other.toString().trimStart(SystemPathSeparator)
    )
}

fun Path.toAbsolutePathString(): String = SystemFileSystem.resolve(this).toString()
