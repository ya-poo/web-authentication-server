package me.yapoo.fido2

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
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
import me.yapoo.fido2.di.appModule
import me.yapoo.fido2.domain.session.LoginSessionRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.handler.authentication.AuthenticationHandler
import me.yapoo.fido2.handler.authentication.AuthenticationRequest
import me.yapoo.fido2.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.fido2.handler.preauthentication.PreAuthenticationRequest
import me.yapoo.fido2.handler.preregistration.PreregistrationHandler
import me.yapoo.fido2.handler.preregistration.PreregistrationRequest
import me.yapoo.fido2.handler.registration.RegistrationHandler
import me.yapoo.fido2.handler.registration.RegistrationRequest
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
            registerModule(JavaTimeModule())
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
        allowMethod(HttpMethod.Options)
        anyHost()
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
        get("/") {
            call.respondText("Hello World!")
        }
        post("/preregistration") {
            val request = call.receive<PreregistrationRequest>()
            val response = preregistrationHandler.handle(request)
            call.respond(response)
        }
        post("/registration") {
            val request = call.receive<RegistrationRequest>()
            registrationHandler.handle(request)
            call.respond(Unit)
        }
        post("/pre-authentication") {
            val request = call.receive<PreAuthenticationRequest>()
            val response = preAuthenticationHandler.handle(request)
            call.respond(response)
        }
        post("/authentication") {
            val request = call.receive<AuthenticationRequest>()
            val session = authenticationHandler.handle(request)
            call.response.cookies.append(
                Cookie(
                    name = "login-session",
                    value = session.id
                )
            )
            call.respond(Unit)
        }
        get("/session") {
            val sessionCookie = call.request.cookies["login-session"]
                ?: throw Exception()
            val session = loginSessionRepository.find(sessionCookie)
                ?: throw Exception()
            val user = userRepository.findById(session.userId)
                ?: throw Exception()

            call.respond(mapOf("username" to user.username))
        }
    }
}
