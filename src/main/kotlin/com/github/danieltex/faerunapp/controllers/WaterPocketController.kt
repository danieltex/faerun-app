package com.github.danieltex.faerunapp.controllers

import com.github.danieltex.faerunapp.dtos.BalanceDTO
import com.github.danieltex.faerunapp.dtos.DebitListDTO
import com.github.danieltex.faerunapp.dtos.EventDTO
import com.github.danieltex.faerunapp.dtos.LoanRequestDTO
import com.github.danieltex.faerunapp.dtos.NewWaterPocketDTO
import com.github.danieltex.faerunapp.dtos.PaymentRequestDTO
import com.github.danieltex.faerunapp.dtos.SettleOperationsDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketBatchDTO
import com.github.danieltex.faerunapp.dtos.WaterPocketDTO
import com.github.danieltex.faerunapp.dtos.toDTO
import com.github.danieltex.faerunapp.dtos.toEntity
import com.github.danieltex.faerunapp.entities.WaterPocketEntity
import com.github.danieltex.faerunapp.services.WaterPocketService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(
    path = ["/water-pockets"],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Tag(name = "Gerencia de bolsões de água")
class WaterPocketController(
    private val waterPocketService: WaterPocketService
) {

    @Operation(summary="Cadastra um bolsão e retorna as informações resultantes do cadastro")
    @ApiResponse(responseCode = "201", description = "Bolsão criado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createWaterPocket(@Valid @RequestBody waterPocket: NewWaterPocketDTO): WaterPocketDTO {
        val created = waterPocketService.save(waterPocket.toEntity())
        return created.toDTO()
    }

    @Operation(summary="Lista todos os bolsões cadastrados.")
    @ApiResponse(responseCode = "200", description = "Retorna uma lista com todos os bolsões")
    @GetMapping(consumes = [MediaType.ALL_VALUE])
    fun getWaterPocketBatch(): WaterPocketBatchDTO {
        val waterPockets = waterPocketService.findAll().map(WaterPocketEntity::toDTO)
        return WaterPocketBatchDTO(waterPockets)
    }

    @Operation(summary = "Informa os dados do bolsão solicitado.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Bolsão encontrado"),
            ApiResponse(responseCode = "404", description = "Bolsão não encontrado", content = [Content()])
        ]
    )
    @GetMapping("/{id}", consumes = [MediaType.ALL_VALUE])
    fun getWaterPocket(
        @Parameter(description = "ID do bolsão solicitado")
        @PathVariable("id") id: Int
    ): WaterPocketDTO {
        return waterPocketService.findById(id).toDTO()
    }

    @Operation(summary = "O bolsão especificado no path pega um empréstimo com bolsão especificado no corpo da requisição")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Empréstimo concedido"),
            ApiResponse(
                responseCode = "400",
                description = "O bolsão solicitado não tem capacidade suficiente para conceder o empréstimo",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "Um dos bolões não foi encontrado", content = [Content()])
        ]
    )
    @PostMapping("/{id}/borrow")
    fun borrow(
        @Parameter(description = "ID do bolsão solicitante")
        @Valid
        @PathVariable("id") debtorId: Int,
        @RequestBody loanRequest: LoanRequestDTO
    ): WaterPocketDTO {
        return waterPocketService.loan(
            debtorId = debtorId,
            creditorId = loanRequest.from,
            quantity = loanRequest.quantity
        ).toDTO()
    }

    @Operation(summary = "O bolsão devedor paga uma quantia para o bolsão pagador especificado no corpo da requisição.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pagamento realizado"),
            ApiResponse(
                responseCode = "400",
                description = "O bolsão devedor não tem capacidade suficiente para realizar o pagamento ou a quantia " +
                    "especificada é maior que o valor do empréstimo contraído",
                content = [Content()]
            ),
            ApiResponse(responseCode = "404", description = "Um dos bolões não foi encontrado", content = [Content()])
        ]
    )
    @PostMapping("/{id}/settle")
    fun settle(
        @Parameter(description = "ID do bolsão solicitante")
        @Valid
        @PathVariable("id") debtorId: Int,
        @RequestBody paymentRequest: PaymentRequestDTO
    ): WaterPocketDTO {
        return waterPocketService.settle(
            debtorId = debtorId,
            creditorId = paymentRequest.to,
            quantity = paymentRequest.quantity
        ).toDTO()
    }

    @Operation(summary = "Informa o status de dívida do bolsão solicitado.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de dívidas do bolsão"),
            ApiResponse(responseCode = "404", description = "Bolsão não encontrado", content = [Content()])
        ]
    )
    @GetMapping("/{id}/debt", consumes = [MediaType.ALL_VALUE])
    fun debt(
        @Parameter(description = "ID do bolsão solicitado")
        @PathVariable("id") id: Int
    ): DebitListDTO {
        return waterPocketService.findAllDebts(id).toDTO()
    }

    @Operation(summary = "Retorna um relatório de quais transferências são necessárias para quitar todos os empréstimos realizados no planeta.")
    @ApiResponse(responseCode = "200", description = "Lista otimizada de todas as operações")
    @GetMapping("/balance", consumes = [MediaType.ALL_VALUE])
    fun balance(): BalanceDTO {
        return waterPocketService.getOptimizedBalance()
    }

    @Operation(summary = "Realiza o pagamento de todos os empréstimos da melhor forma possível")
    @ApiResponse(responseCode = "200", description = "Lista otimizada de todas as operações")
    @PutMapping("/settle-all", consumes = [MediaType.ALL_VALUE])
    fun settleAll(): SettleOperationsDTO {
        return waterPocketService.settleAll()
    }


    @Operation(summary = "Retorna uma lista com todas as operações de um bolsão desde a sua criação, mais recentes primeiro.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operações do bolsão"),
            ApiResponse(responseCode = "404", description = "Bolsão não encontrado", content = [Content()])
        ]
    )
    @GetMapping("/{id}/events", consumes = [MediaType.ALL_VALUE])
    fun events(
        @Parameter(description = "ID do bolsão solicitado")
        @PathVariable("id") id: Int
    ): List<EventDTO> {
        return waterPocketService.findEvents(id)
    }
}
