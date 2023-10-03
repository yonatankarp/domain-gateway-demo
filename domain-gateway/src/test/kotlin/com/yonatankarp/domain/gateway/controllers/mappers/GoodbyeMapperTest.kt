package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.domain.gateway.controllers.mappers.GoodbyeMapper.toResponse
import com.yonatankarp.gateway.openapi.v1.models.GoodbyeResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import retrofit2.Response
import com.yonatankarp.goodbye.openapi.v1_current.models.GoodbyeResponse as GoodbyeServiceResponse

class GoodbyeMapperTest {
    @Test
    fun `test convert to response`() {
        val message = "Goodbye you!"

        // Given
        val serviceResponse =
            GoodbyeServiceResponse(value = message)
                .let { Response.success(it) }

        // When
        val actual = serviceResponse.toResponse()

        // Then
        assertEquals(ResponseEntity::class.java, actual::class.java)
        val expected = GoodbyeResponse(value = message)
        assertTrue(actual.body is GoodbyeResponse)
        assertEquals(expected, actual.body as GoodbyeResponse)
    }
}
