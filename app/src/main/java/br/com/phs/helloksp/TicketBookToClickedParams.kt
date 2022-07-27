package br.com.phs.helloksp

import br.com.phs.annotations.MainEvent
import java.io.Serializable

@MainEvent
data class TicketBookToClickedParams(
    val eventName: String,
    val screenName: String,
    val ticketNumber: Int,
    val ticketAmount: String
): Serializable
