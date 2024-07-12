package com.wesleyhome.aws.appconfig

data class Configuration(
    val application: String,
    val environment: String,
    val profile: String
)
