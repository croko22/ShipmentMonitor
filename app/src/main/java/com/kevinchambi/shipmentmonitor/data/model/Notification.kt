package com.kevinchambi.shipmentmonitor.data.model

data class Notification(
    val id: Int,
    val invoice_number: String,
    val status: String,
    val created_at: String
)
