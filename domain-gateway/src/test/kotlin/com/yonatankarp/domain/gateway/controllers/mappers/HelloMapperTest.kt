package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.domain.gateway.controllers.mappers.HelloMapper.toResponse
import com.yonatankarp.gateway.openapi.v1.models.HelloResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import retrofit2.Response
import com.yonatankarp.hello.openapi.v1_current.models.HelloResponse as HelloServiceResponse

class HelloMapperTest {
    @Test
    fun `test convert to response`() {
        val message = "Hello, you!"

        // Given
        val serviceResponse =
            HelloServiceResponse(value = message)
                .let { Response.success(it) }

        // When
        val actual = serviceResponse.toResponse()

        // Then
        assertEquals(ResponseEntity::class.java, actual::class.java)
        val expected = HelloResponse(value = message)
        assertTrue(actual.body is HelloResponse)
        assertEquals(expected, actual.body as HelloResponse)
    }
}
