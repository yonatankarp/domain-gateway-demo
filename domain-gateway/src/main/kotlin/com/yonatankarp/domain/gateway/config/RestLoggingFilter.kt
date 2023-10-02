package com.yonatankarp.domain.gateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.AsyncEvent
import jakarta.servlet.AsyncListener
import jakarta.servlet.FilterChain
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@WebFilter(urlPatterns = ["/*"])
@Order(Int.MIN_VALUE)
class RestLoggingFilter(objectMapper: ObjectMapper) : OncePerRequestFilter() {
    private val objectMapper = objectMapper.copy().disable(SerializationFeature.INDENT_OUTPUT)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestContent = request as? ContentCachingRequestWrapper ?: ContentCachingRequestWrapper(request)
        val responseContent = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)

        filterChain.doFilter(requestContent, responseContent)

        if (requestContent.isAsyncStarted) {
            requestContent.asyncContext.addListener(
                object : AsyncListener {
                    override fun onComplete(event: AsyncEvent?) {
                        logInfo(request, requestContent, responseContent)
                        responseContent.copyBodyToResponse()
                    }

                    override fun onTimeout(event: AsyncEvent?) = logError(request, requestContent, "Async request timed out")

                    override fun onError(event: AsyncEvent?) = logError(request, requestContent)

                    override fun onStartAsync(event: AsyncEvent?) = Unit
                },
            )
        } else {
            logInfo(request, requestContent, responseContent)
            responseContent.copyBodyToResponse()
        }
    }

    private fun logInfo(
        request: HttpServletRequest,
        requestContent: ContentCachingRequestWrapper,
        responseContent: ContentCachingResponseWrapper,
    ) {
        if (request.requestURI in skippedUris) {
            return
        }

        objectMapper.writeValueAsString(
            Log(
                method = requestContent.method,
                url = request.requestURI,
                status = responseContent.status,
                headers = extractHeaders(request),
                requestBody = String(requestContent.contentAsByteArray),
                responseBody = String(responseContent.contentAsByteArray),
            ),
        ).let { body ->
            when {
                responseContent.status < HttpStatus.BAD_REQUEST.value() -> log.info(body)
                else -> log.error(body)
            }
        }
    }

    private fun logError(
        request: HttpServletRequest,
        requestContent: ContentCachingRequestWrapper,
        message: String = "Error while processing the request",
    ) {
        log.error(
            objectMapper.writeValueAsString(
                Log(
                    method = requestContent.method,
                    url = request.requestURI,
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    headers = extractHeaders(request),
                    requestBody = String(requestContent.contentAsByteArray),
                    responseBody = message,
                ),
            ),
        )
    }

    data class Log(
        val method: String?,
        val url: String,
        val status: Int,
        val headers: Map<String, String>,
        val requestBody: Any?,
        val responseBody: Any?,
    )

    companion object {
        private val log = LoggerFactory.getLogger(RestLoggingFilter::class.java)
        private val skippedUris =
            listOf(
                "/actuator/health",
                "/actuator/prometheus",
            )

        private fun extractHeaders(request: HttpServletRequest): Map<String, String> =
            request.headerNames.toList().associateWith {
                request.getHeader(it)
            }
    }
}
