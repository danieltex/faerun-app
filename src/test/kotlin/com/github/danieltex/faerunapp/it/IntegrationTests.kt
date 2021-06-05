package com.github.danieltex.faerunapp.it

import com.github.danieltex.faerunapp.entities.WaterPocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired private val restTemplate: TestRestTemplate) {
    @Test
    fun `Assert create water pocket returns valid object with id`() {
        val waterPocket = WaterPocket(null, "Callagan", "700.20".toBigDecimal())

        val response = restTemplate.postForEntity("/water-pockets", waterPocket, WaterPocket::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body?.id, "WaterPocket ID was null")
        assertEquals(waterPocket.name, response.body?.name)
        assertEquals(waterPocket.storage, response.body?.storage)
    }

    @Test
    fun `Assert create water pocket wont accept empty or null fields`() {
        val noNameWatterPocket = """
            {
                "storage": 700.8
            }
        """.trimIndent()
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val request = HttpEntity(noNameWatterPocket, headers)

        val response = restTemplate.postForEntity("/water-pockets", request, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

}
