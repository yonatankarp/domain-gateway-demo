package com.yonatankarp.domain.gateway.config

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * A WebFilter responsible for logging incoming requests and outgoing responses
 * for HTTP requests. It logs request methods, URIs, and response statuses.
 *
 * @property log The logger instance used for logging.
 * @property excludedEndpoints Set of URIs to be excluded from logging.
 */
@Component
@Order(Int.MIN_VALUE)
class RestLoggingFilter : WebFilter {
    companion object {
        private val log = LoggerFactory.getLogger(RestLoggingFilter::class.java)

        private val excludedEndpoints =
            setOf(
                "/actuator/health",
                "/actuator/prometheus",
            )
    }

    /**
     * Filters incoming requests and logs them before passing them to the next
     * filter in the chain. Also, logs the response status after the request is
     * processed.
     *
     * @param exchange The ServerWebExchange containing the request and response.
     * @param chain The WebFilterChain to continue processing the request.
     * @return A Mono indicating completion of the request processing.
     */
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val request = exchange.request
        val response = exchange.response

        if (shouldExclude(request)) {
            return chain.filter(exchange)
        }

        logRequest(request)

        return chain.filter(exchange).doOnTerminate {
            logResponse(request, response)
        }
    }

    private fun shouldExclude(request: ServerHttpRequest) = excludedEndpoints.contains(request.uri.path)

    private fun logRequest(request: ServerHttpRequest) {
        log.info("[${request.method}] ${request.uri}")
    }

    private fun logResponse(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ) {
        val status = response.statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR
        log.info("[${request.method}] ${request.uri} <-- [${status.value()}]")
    }
}
