package com.github.danieltex.faerunapp.it

import com.github.danieltex.faerunapp.dto.WaterPocketBatchDTO
import com.github.danieltex.faerunapp.dto.WaterPocketDTO
import org.assertj.core.api.Assertions.assertThat
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
        val waterPocket = WaterPocketDTO("Callagan", "700.20".toBigDecimal())

        val response = restTemplate.postForEntity("/water-pockets", waterPocket, WaterPocketDTO::class.java)

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

    @Test
    fun `Assert can create and retrieve a water pocket by id`() {
        val waterPocket = WaterPocketDTO("Rei", "1000.1".toBigDecimal())

        val postResponse = restTemplate.postForEntity("/water-pockets", waterPocket, WaterPocketDTO::class.java)
        val getResponse = restTemplate.getForEntity("/water-pockets/${postResponse.body!!.id}", WaterPocketDTO::class.java)

        assertEquals(postResponse.body!!.id, getResponse.body!!.id)
        assertEquals(waterPocket.name, getResponse.body!!.name)
        assertEquals(waterPocket.storage.toDouble(), getResponse.body!!.storage.toDouble())
    }

    @Test
    fun `Assert returns NOT FOUND status when get for non existing water pocket`() {
        val nonExistingId = 999

        val getResponse = restTemplate.getForEntity("/water-pockets/$nonExistingId", WaterPocketDTO::class.java)

        assertEquals(HttpStatus.NOT_FOUND, getResponse.statusCode)
    }

    @Test
    fun `Assert can retrieve created water pockets`() {
        val wpRequests = listOf(
            WaterPocketDTO("Alpha", "1000.1".toBigDecimal()),
            WaterPocketDTO("Beta", "1000.1".toBigDecimal()),
            WaterPocketDTO("Gamma", "1000.1".toBigDecimal())
        )

        val wpResponses = wpRequests
            .map {
                restTemplate.postForEntity("/water-pockets", it, WaterPocketDTO::class.java)
            }
            .map { it.body!! }
        val getBatchResponse = restTemplate.getForEntity("/water-pockets", WaterPocketBatchDTO::class.java)

        assertThat(getBatchResponse.body!!.waterPockets)
            .usingElementComparatorIgnoringFields("storage")
            .containsAll(wpResponses)
    }
}
