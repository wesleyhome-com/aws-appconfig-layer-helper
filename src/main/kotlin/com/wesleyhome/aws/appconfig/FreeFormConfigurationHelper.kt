package com.wesleyhome.aws.appconfig

interface FreeFormConfigurationHelper<T : FreeFormConfiguration> {

    val configuration: Configuration

    fun readConfig(): T
}

