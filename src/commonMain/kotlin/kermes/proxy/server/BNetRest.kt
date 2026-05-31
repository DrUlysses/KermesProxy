package kermes.proxy.server

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kermes.proxy.server.models.loginForm
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

fun Application.bNetRest() {
    monitor.subscribe(ApplicationStarted) {
        logger.debug { "BNet REST is ready" }
    }
    monitor.subscribe(ApplicationStopping) {
        logger.debug { "BNet REST stopping" }
    }
    monitor.subscribe(ApplicationStopped) {
        logger.debug { "BNet REST stopped" }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    
    routing {
        route("bnetserver") {
            get("login/{...}") {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = loginForm
                )
            }
        }
    }
}
