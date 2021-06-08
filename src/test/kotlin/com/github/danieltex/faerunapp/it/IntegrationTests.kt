package com.github.danieltex.faerunapp.it

import com.github.danieltex.faerunapp.dtos.DebitListDTO
import com.github.danieltex.faerunapp.dtos.LoanRequestDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketBatchDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketDTO
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
        assertEquals(HttpStatus.OK, getResponse.statusCode)
    }

    @Test
    fun `Assert returns NOT FOUND status when get for non existing water pocket`() {
        val nonExistingId = 999

        val getResponse = restTemplate.getForEntity("/water-pockets/$nonExistingId", String::class.java)

        assertEquals(HttpStatus.NOT_FOUND, getResponse.statusCode)
        assertEquals("Water Pocket '999' not found", getResponse.body)
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
        assertEquals(HttpStatus.OK, getBatchResponse.statusCode)
    }

    @Test
    fun `Assert borrow invalid amount returns bad request status`() {
        val fromWP = WaterPocketDTO("From", "10.00".toBigDecimal())
        val toWP = WaterPocketDTO("To", "0.00".toBigDecimal())

        val fromResponse = restTemplate.postForEntity("/water-pockets", fromWP, WaterPocketDTO::class.java).body!!
        val toResponse = restTemplate.postForEntity("/water-pockets", toWP, WaterPocketDTO::class.java).body!!
        val borrowRequest = LoanRequestDTO(fromResponse.id!!, "20.00".toBigDecimal())
        val result = restTemplate.postForEntity("/water-pockets/${toResponse.id}/borrow", borrowRequest, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals("Insufficient storage on water pocket", result.body)
    }

    @Test
    fun `Assert when borrows valid amount returns updated storage`() {
        val fromWP = WaterPocketDTO("FromWp", "50.00".toBigDecimal())
        val toWP = WaterPocketDTO("ToWp", "10.00".toBigDecimal())

        val idFrom = restTemplate.postForEntity("/water-pockets", fromWP, WaterPocketDTO::class.java).body!!.id!!
        val idTo = restTemplate.postForEntity("/water-pockets", toWP, WaterPocketDTO::class.java).body!!.id!!
        val borrowRequest = LoanRequestDTO(idFrom, "30.00".toBigDecimal())
        val result = restTemplate.postForEntity("/water-pockets/${idTo}/borrow", borrowRequest, WaterPocketDTO::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(idTo, result.body!!.id!!)
        assertEquals(40.0, result.body!!.storage.toDouble())
    }

    @Test
    fun `Assert can retrieve debts contracted`() {
        // create 3 water pockets
        val alpha = WaterPocketDTO("Alpha", "100.00".toBigDecimal())
        val beta = WaterPocketDTO("Beta", "100.00".toBigDecimal())
        val gamma = WaterPocketDTO("Gamma", "100.00".toBigDecimal())
        val idAlpha = restTemplate.postForEntity("/water-pockets", alpha, WaterPocketDTO::class.java).body!!.id!!
        val idBeta = restTemplate.postForEntity("/water-pockets", beta, WaterPocketDTO::class.java).body!!.id!!
        val idGamma = restTemplate.postForEntity("/water-pockets", gamma, WaterPocketDTO::class.java).body!!.id!!

        // make gamma borrow from alpha and beta
        // make alpha borrow from gamma
        val borrowAlpha = LoanRequestDTO(idAlpha, "10.00".toBigDecimal())
        val borrowBeta = LoanRequestDTO(idBeta, "20.00".toBigDecimal())
        val borrowGamma = LoanRequestDTO(idGamma, "30.00".toBigDecimal())
        restTemplate.postForEntity("/water-pockets/${idGamma}/borrow", borrowAlpha, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${idGamma}/borrow", borrowBeta, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${idAlpha}/borrow", borrowGamma, WaterPocketDTO::class.java)
        val responseAlpha = restTemplate.getForEntity("/water-pockets/${idAlpha}/debt", DebitListDTO::class.java)
        val responseBeta = restTemplate.getForEntity("/water-pockets/${idBeta}/debt", DebitListDTO::class.java)
        val responseGamma = restTemplate.getForEntity("/water-pockets/${idGamma}/debt", DebitListDTO::class.java)

        // returns ok even if no debt
        assertEquals(HttpStatus.OK, responseAlpha.statusCode)
        assertEquals(HttpStatus.OK, responseBeta.statusCode)
        assertEquals(HttpStatus.OK, responseGamma.statusCode)

        // assert alpha has debt to gamma
        val debtsAlpha = responseAlpha.body!!.debts
        assertEquals(1, debtsAlpha.size)
        assertEquals(30.0, debtsAlpha.find { it.id == idGamma }!!.quantity.toDouble())

        // assert beta has no debts
        val debtsBeta = responseBeta.body!!.debts
        assertEquals(0, debtsBeta.size)

        // assert gamma has debts to alpha and gamma
        val debtsGamma = responseGamma.body!!.debts
        assertEquals(2, debtsGamma.size)
        assertEquals(10.0, debtsGamma.find { it.id == idAlpha }!!.quantity.toDouble())
        assertEquals(20.0, debtsGamma.find { it.id == idBeta }!!.quantity.toDouble())
    }
}
