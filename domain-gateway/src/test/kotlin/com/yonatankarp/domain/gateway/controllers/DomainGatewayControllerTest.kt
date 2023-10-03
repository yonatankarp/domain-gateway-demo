package com.yonatankarp.domain.gateway.controllers

import com.ninjasquad.springmockk.MockkBean
import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.goodbye.openapi.v1_current.models.GoodbyeResponse
import com.yonatankarp.hello.openapi.v1_current.HelloApi
import com.yonatankarp.hello.openapi.v1_current.models.HelloResponse
import io.mockk.coEvery
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode
import org.springframework.test.web.reactive.server.WebTestClient
import retrofit2.Response

@SpringBootTest
@AutoConfigureMockMvc
@TestConstructor(autowireMode = AutowireMode.ALL)
class DomainGatewayControllerTest(private val webTestClient: WebTestClient) {

    @MockkBean
    private lateinit var helloApi: HelloApi

    @MockkBean
    private lateinit var goodbyeApi: GoodbyeApi

    @ParameterizedTest(name = "test hello endpoint for {1}")
    @MethodSource("helloTestCases")
    fun `test hello endpoint`(
        name: String,
        expectedOutput: String,
    ) {
        coEvery { helloApi.hello(eq(name)) } returns Response.success(
            HelloResponse("Hello, $name!")
        )

        webTestClient.get()
            .uri("/hello/$name")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.value").isEqualTo(expectedOutput)
    }

    @ParameterizedTest(name = "test goodbye endpoint for {1}")
    @MethodSource("goodbyeTestCases")
    fun `test goodbye endpoint`(
        name: String,
        expectedOutput: String,
    ) {
        coEvery { goodbyeApi.goodbye(eq(name)) } returns Response.success(
            GoodbyeResponse("Goodbye $name!")
        )

        webTestClient.get()
            .uri("/goodbye/$name")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.value").isEqualTo(expectedOutput)
    }

    companion object {
        @JvmStatic
        fun helloTestCases() =
            listOf(
                arrayOf("John", "Hello, John!"),
                arrayOf("JoHnNa", "Hello, JoHnNa!"),
                arrayOf("james", "Hello, james!"),
            )

        @JvmStatic
        fun goodbyeTestCases() =
            listOf(
                arrayOf("John", "Goodbye John!"),
                arrayOf("JoHnNa", "Goodbye JoHnNa!"),
                arrayOf("james", "Goodbye james!"),
            )
    }
}
