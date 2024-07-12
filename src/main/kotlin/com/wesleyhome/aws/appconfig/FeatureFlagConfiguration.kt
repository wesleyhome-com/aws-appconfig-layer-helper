package com.wesleyhome.aws.appconfig

interface FeatureFlagConfiguration : FreeFormConfiguration {
    val enabled: Boolean
}
