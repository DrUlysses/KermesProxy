package kermes.proxy.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

fun Application.realmSocket() {
    monitor.subscribe(ApplicationStarted) {
        logger.debug { "Realm Socket is ready" }
    }
    monitor.subscribe(ApplicationStopping) {
        logger.debug { "Realm Socket stopping" }
    }
    monitor.subscribe(ApplicationStopped) {
        logger.debug { "Realm Socket stopped" }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}
