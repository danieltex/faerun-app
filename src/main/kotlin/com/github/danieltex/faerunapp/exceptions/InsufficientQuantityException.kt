package com.github.danieltex.faerunapp.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Quantity insufficient to realize operation")
class InsufficientQuantityException(message: String) : RuntimeException(message) {
    companion object {
        const val INSUFFICIENT_STORAGE_MESSAGE = "Insufficient storage on water pocket"
        const val OVERPAYMENT_MESSAGE = "Payment is bigger than debt"
    }
}
