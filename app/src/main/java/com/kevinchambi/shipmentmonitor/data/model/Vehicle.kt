package com.kevinchambi.shipmentmonitor.data.model

data class Vehicle(
    val id: Int,
    val plate: String,
    val speed: String,
    val latitude: Double,
    val longitude: Double,
    val angle: Int,
    val status: String
)
