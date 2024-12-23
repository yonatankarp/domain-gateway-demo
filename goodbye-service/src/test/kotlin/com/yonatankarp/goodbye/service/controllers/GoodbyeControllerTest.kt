package com.yonatankarp.goodbye.service.controllers

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
class GoodbyeControllerTest(
    private val webTestClient: WebTestClient,
) {
    @ParameterizedTest(name = "test goodbye endpoint for {1}")
    @MethodSource("testCases")
    fun `test goodbye endpoint`(
        name: String,
        expectedOutput: String,
    ) {
        webTestClient
            .get()
            .uri("/goodbye/$name")
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
                arrayOf("John", "Goodbye John!"),
                arrayOf("JoHnNa", "Goodbye JoHnNa!"),
                arrayOf("james", "Goodbye james!"),
            )
    }
}
