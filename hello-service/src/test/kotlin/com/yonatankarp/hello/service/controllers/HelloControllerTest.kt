package com.yonatankarp.hello.service.controllers

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = AutowireMode.ALL)
class HelloControllerTest(
    private val webTestClient: WebTestClient,
) {
    @ParameterizedTest(name = "test hello endpoint for {1}")
    @MethodSource("testCases")
    fun `test hello endpoint`(
        name: String,
        expectedOutput: String,
    ) {
        webTestClient
            .get()
            .uri("/hello/$name")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.value")
            .isEqualTo(expectedOutput)
    }

    companion object {
        @JvmStatic
        fun testCases() =
            listOf(
                arrayOf("John", "Hello, John!"),
                arrayOf("JoHnNa", "Hello, JoHnNa!"),
                arrayOf("james", "Hello, james!"),
            )
    }
}
