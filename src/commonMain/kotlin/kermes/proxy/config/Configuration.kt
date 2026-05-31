package kermes.proxy.config

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parseSource
import com.fleeksoft.ksoup.parser.Parser
import io.github.oshai.kotlinlogging.KotlinLogging
import kermes.proxy.utils.resPathByNameOrNull
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put

private val logger = KotlinLogging.logger {}

@Serializable
data class Configuration(
    val clientSeed: String = "179D3DC3235629D07113A9B3867F97A7",
    val clientBuild: ClientVersionBuild = ClientVersionBuild.V2_5_2_40892,
    val serverBuild: ClientVersionBuild = clientBuild.toBestLegacyVersion(),
    val serverAddress: String = "127.0.0.1",
    val serverPort: Int = 3724,
    val reportedOS: OsFamily = OsFamily.WINDOWS,
    val reportedPlatform: CpuArchitecture = CpuArchitecture.X86,
    val externalAddress: String = "127.0.0.1",
    val restPort: Int = 8081,
    val bNetPort: Int = 1119,
    val realmPort: Int = 8084,
    val instancePort: Int = 8086,
    val debugOutput: Boolean = false,
    val packetsLog: Boolean = true,
    val serverSpellDelay: Int = 0,
    val clientSpellDelay: Int = 0
)

private val json = Json {
    ignoreUnknownKeys = true
}

fun loadAndVerifyConfig(
    configPath: Path
): Configuration = runCatching {
    val configPath: Path = if (SystemFileSystem.exists(configPath)) {
        configPath
    } else {
        resPathByNameOrNull("files/HermesProxy.config") ?: run { 
            val message = "No config was found in resources for files/HermesProxy.config"
            logger.error { message }
            error(message)
        }
    }
    
    SystemFileSystem.source(configPath).use { source ->
        val parsedXml = Ksoup.parseSource(
            source = source,
            parser = Parser.xmlParser()
        )
        json.decodeFromJsonElement<Configuration>(
            buildJsonObject {
                var clientBuild = Configuration().clientBuild
                parsedXml.select("appSettings > add").forEach { element ->
                    val key = element.attr("key")
                    val value = element.attr("value")

                    when (key) {
                        "DebugOutput",
                        "PacketsLog" -> put(key, value.toBoolean())

                        "ServerSpellDelay",
                        "ClientSpellDelay" -> value.toIntOrNull()?.let {
                            put(key, it)
                        }
                        
                        "ClientBuild" -> {
                            value.toIntOrNull()?.let { buildInt ->
                                ClientVersionBuild.fromBuildOrNull(buildInt)?.let { build ->
                                    clientBuild = build
                                    put(key, build.toString())
                                }
                            }
                        }
                        "ServerBuild" -> {
                            value.toIntOrNull()?.let { buildInt ->
                                ClientVersionBuild.fromBuildOrNull(buildInt)?.let { build ->
                                    put(key, build.toString())
                                }
                            } ?: run {
                                if (value.lowercase() == "auto") {
                                    put(key, clientBuild.toBestLegacyVersion().toString())
                                }
                            }
                        }

                        else -> put(key, value)
                    }
                }
            }

        )
    }
}.getOrElse {
    logger.error(it) { "Can't load the config from $configPath" }
    throw it
}
