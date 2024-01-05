package me.yapoo.fido2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.yapoo.fido2.di.appModule
import me.yapoo.fido2.domain.session.LoginSessionRepository
import me.yapoo.fido2.domain.user.UserRepository
import me.yapoo.fido2.handler.authentication.AuthenticationHandler
import me.yapoo.fido2.handler.authentication.AuthenticationRequest
import me.yapoo.fido2.handler.preauthentication.PreAuthenticationHandler
import me.yapoo.fido2.handler.preregistration.PreregistrationHandler
import me.yapoo.fido2.handler.preregistration.PreregistrationRequest
import me.yapoo.fido2.handler.registration.RegistrationHandler
import me.yapoo.fido2.handler.registration.RegistrationRequest
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.UUID

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
            val (responseBody, sessionId) = preAuthenticationHandler.handle()
            call.response.cookies.append(
                Cookie(
                    name = "authentication-session",
                    value = sessionId
                )
            )
            call.respond(responseBody)
        }
        post("/authentication") {
            val sessionId = call.request.cookies["authentication-session"]
                ?.let(UUID::fromString)
                ?: throw Exception("authentication-session is null")
            val request = call.receive<AuthenticationRequest>()
            val loginSession = authenticationHandler.handle(request, sessionId)
            call.response.cookies.append(
                Cookie(
                    name = "login-session",
                    value = loginSession.id
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
