package com.nahid.expensetracker.domain.model

data class User(
    val id: Long,
    val userId: String,
    val userName: String,
    val role: String,
    val distanceThreshold: Double,
    val locationUpdateInterval: Int,
    val exitTime: String,
    val isGeoLocationEnabled: Boolean,
    val companyName: String
)
