package me.yapoo.webauthn

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.yapoo.webauthn.di.appModule
import me.yapoo.webauthn.domain.session.LoginSessionRepository
import me.yapoo.webauthn.domain.user.UserRepository
import me.yapoo.webauthn.handler.authentication.AuthenticationHandler
import me.yapoo.webauthn.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.webauthn.handler.preregistration.PreregistrationHandler
import me.yapoo.webauthn.handler.registration.RegistrationHandler
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    install(CORS) {
        // ローカルでの動作時にフロントエンドとポートが異なるため、CORS を許可しておく
        allowHost(host = "localhost:3000", schemes = listOf("http"))
        allowMethod(HttpMethod.Options)
        allowHeader("Cookie")
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    val logger = LoggerFactory.getLogger(this::class.simpleName)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.info("Exception", cause)
            call.respondText(text = "Exception: ${cause.message}", status = HttpStatusCode.BadRequest)
        }
    }

    val preregistrationHandler by inject<PreregistrationHandler>()
    val registrationHandler by inject<RegistrationHandler>()
    val preAuthenticationHandler by inject<PreAuthenticationHandler>()
    val authenticationHandler by inject<AuthenticationHandler>()
    val loginSessionRepository by inject<LoginSessionRepository>()
    val userRepository by inject<UserRepository>()

    routing {
        post("/pre-registration") { preregistrationHandler.handle(call) }
        post("/registration") { registrationHandler.handle(call) }

        post("/pre-authentication") { preAuthenticationHandler.handle(call) }
        post("/authentication") { authenticationHandler.handle(call) }

        get("/session") {
            val sessionCookie = call.request.cookies["login-session"] ?: throw Exception("no login-session cookie")
            val session = loginSessionRepository.find(sessionCookie) ?: throw Exception("login-session not found")
            val user = userRepository.findById(session.userId) ?: throw Exception("user not found")

            call.respond(mapOf("username" to user.username))
        }
    }
}
