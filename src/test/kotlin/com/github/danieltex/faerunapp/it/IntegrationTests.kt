package com.github.danieltex.faerunapp.it

import com.github.danieltex.faerunapp.dtos.BalanceDTO
import com.github.danieltex.faerunapp.dtos.DebitListDTO
import com.github.danieltex.faerunapp.dtos.LoanRequestDTO
import com.github.danieltex.faerunapp.dtos.OperationDetails
import com.github.danieltex.faerunapp.dtos.OperationType
import com.github.danieltex.faerunapp.dtos.PaymentRequestDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketBatchDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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
    fun `Assert result bad request on invalid json`() {
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
    fun `Assert when borrows invalid amount returns bad request status`() {
        val creditor = WaterPocketDTO("From", "10.00".toBigDecimal())
        val debtor = WaterPocketDTO("To", "0.00".toBigDecimal())

        val creditorResponse = restTemplate.postForEntity("/water-pockets", creditor, WaterPocketDTO::class.java).body!!
        val debtorResponse = restTemplate.postForEntity("/water-pockets", debtor, WaterPocketDTO::class.java).body!!
        val borrowRequest = LoanRequestDTO(creditorResponse.id!!, "20.00".toBigDecimal())
        val result = restTemplate.postForEntity("/water-pockets/${debtorResponse.id}/borrow", borrowRequest, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertEquals("Insufficient storage on water pocket", result.body)
    }

    @Test
    fun `Assert when borrows valid amount returns updated storage`() {
        val creditor = WaterPocketDTO("FromWp", "50.00".toBigDecimal())
        val debtor = WaterPocketDTO("ToWp", "10.00".toBigDecimal())

        val creditorId = restTemplate.postForEntity("/water-pockets", creditor, WaterPocketDTO::class.java).body!!.id!!
        val debtorId = restTemplate.postForEntity("/water-pockets", debtor, WaterPocketDTO::class.java).body!!.id!!
        val loanRequest = LoanRequestDTO(creditorId, "30.00".toBigDecimal())
        val result = restTemplate.postForEntity("/water-pockets/${debtorId}/borrow", loanRequest, WaterPocketDTO::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(debtorId, result.body!!.id!!)
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
        val loanAlpha = LoanRequestDTO(idAlpha, "10.00".toBigDecimal())
        val loanBeta = LoanRequestDTO(idBeta, "20.00".toBigDecimal())
        val loanGamma = LoanRequestDTO(idGamma, "30.00".toBigDecimal())
        restTemplate.postForEntity("/water-pockets/${idGamma}/borrow", loanAlpha, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${idGamma}/borrow", loanBeta, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${idAlpha}/borrow", loanGamma, WaterPocketDTO::class.java)
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

    @Test
    fun `Assert when pays valid amount returns updated storage`() {
        val debtor = WaterPocketDTO("Debtor", "10.00".toBigDecimal())
        val creditor = WaterPocketDTO("Creditor", "40.00".toBigDecimal())

        val debtorId = restTemplate.postForEntity("/water-pockets", debtor, WaterPocketDTO::class.java).body!!.id!!
        val creditorId = restTemplate.postForEntity("/water-pockets", creditor, WaterPocketDTO::class.java)
            .body!!.id!!

        val loanRequest = LoanRequestDTO(creditorId, "30.00".toBigDecimal())
        restTemplate.postForEntity("/water-pockets/${debtorId}/borrow", loanRequest, WaterPocketDTO::class.java)

        val paymentRequest = PaymentRequestDTO(creditorId, "30.00".toBigDecimal())
        val result = restTemplate.postForEntity("/water-pockets/${debtorId}/settle", paymentRequest, WaterPocketDTO::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(debtorId, result.body!!.id!!)
        assertEquals(10.0, result.body!!.storage.toDouble())
    }

    @Test
    fun `assert can generate valid balance`() {
        val alpha = WaterPocketDTO("Alpha", "100.00".toBigDecimal())
        val beta = WaterPocketDTO("Beta", "400.00".toBigDecimal())
        val gamma = WaterPocketDTO("Gamma", "1000.00".toBigDecimal())
        val delta = WaterPocketDTO("Delta", "0.00".toBigDecimal())

        val alphaId = restTemplate.postForEntity("/water-pockets", alpha, WaterPocketDTO::class.java).body!!.id!!
        val betaId = restTemplate.postForEntity("/water-pockets", beta, WaterPocketDTO::class.java).body!!.id!!
        val gammaId = restTemplate.postForEntity("/water-pockets", gamma, WaterPocketDTO::class.java).body!!.id!!
        val deltaId = restTemplate.postForEntity("/water-pockets", delta, WaterPocketDTO::class.java).body!!.id!!

        // borrows:
        // alpha -> beta  : 400
        // beta  -> gamma : 10
        // delta -> gamma : 900

        // alpha should pay 400
        // delta should pay 900
        // beta should receive 390
        // gamma should receive 910
        val loanAlphaBeta = LoanRequestDTO(betaId, "400.00".toBigDecimal())
        val loanBetaGamma = LoanRequestDTO(gammaId, "10.00".toBigDecimal())
        val loanDeltaGamma = LoanRequestDTO(gammaId, "900.00".toBigDecimal())
        restTemplate.postForEntity("/water-pockets/${alphaId}/borrow", loanAlphaBeta, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${betaId}/borrow", loanBetaGamma, WaterPocketDTO::class.java)
        restTemplate.postForEntity("/water-pockets/${deltaId}/borrow", loanDeltaGamma, WaterPocketDTO::class.java)

        val response = restTemplate.getForEntity("/water-pockets/balance", BalanceDTO::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val alphaBalance = response.body!!.balance[alphaId]!!
        val betaBalance = response.body!!.balance[betaId]!!
        val gammaBalance = response.body!!.balance[gammaId]!!
        val deltaBalance = response.body!!.balance[deltaId]!!

        // assert correct storage on balance response
        assertEquals(500.0, alphaBalance.storage.toDouble())
        assertEquals(10.0, betaBalance.storage.toDouble())
        assertEquals(90.0, gammaBalance.storage.toDouble())
        assertEquals(900.0, deltaBalance.storage.toDouble())

        val alphaOperations = alphaBalance.operations.groupBy { it.operation }
        val betaOperations = betaBalance.operations.groupBy { it.operation }
        val gammaOperations = gammaBalance.operations.groupBy { it.operation }
        val deltaOperations = deltaBalance.operations.groupBy { it.operation }

        // should either receive or pay (no transitive operations)
        assertNull(alphaOperations[OperationType.RECEIVE])
        assertNull(betaOperations[OperationType.PAY])
        assertNull(gammaOperations[OperationType.PAY])
        assertNull(deltaOperations[OperationType.RECEIVE])
        assertNotNull(alphaOperations[OperationType.PAY])
        assertNotNull(betaOperations[OperationType.RECEIVE])
        assertNotNull(gammaOperations[OperationType.RECEIVE])
        assertNotNull(deltaOperations[OperationType.PAY])

        val alphaPayTotal = alphaOperations[OperationType.PAY]!!.sumToDouble()
        val betaReceiveTotal = betaOperations[OperationType.RECEIVE]!!.sumToDouble()
        val gammaReceiveTotal = gammaOperations[OperationType.RECEIVE]!!.sumToDouble()
        val deltaPayTotal = deltaOperations[OperationType.PAY]!!.sumToDouble()
        assertEquals(400.0, alphaPayTotal)
        assertEquals(390.0, betaReceiveTotal)
        assertEquals(910.0, gammaReceiveTotal)
        assertEquals(900.0, deltaPayTotal)
    }
}

fun List<OperationDetails>.sumToDouble() = this.map { it.quantity }.reduce { a, b -> a + b }.toDouble()
