package dev.almasum.health_connect.network.pojo

data class ClientResponse(
    val clientId: Int,
    val clientFirstName: String,
    val clientLastName: String,
    val phoneNumber: String,
    val email: String,
    val phoneType: String,
    val createdDatetimeStr: String,
    val providerId: Int,
    val clientStepsRecordList: List<Any>?,
    val clientOxygenSaturationRecordList: List<Any>?,
    val clientBodyTemperatureList: List<Any>?,
    val clientRespiratoryList: List<Any>?,
    val clientHeartRateList: List<Any>?,
    val clientDistanceRecordList: List<Any>?,
    val clientBloodPressureRecordList: List<Any>?,
    val updatedDatetimeStr: String
)
