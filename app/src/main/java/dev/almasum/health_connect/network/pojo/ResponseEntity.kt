package dev.almasum.health_connect.network.pojo

data class ResponseEntity(
    var responseCode: String = "",
    var responseMessage: String = "",
    var responseDescriptions: String = "",
    var responseObjects: List<Any> = listOf()
)