package com.wesleyhome.aws.appconfig

interface FeatureFlagConfigurationHelper<T : FeatureFlagConfiguration> : FreeFormConfigurationHelper<T> {
    val flags: List<String>
}
