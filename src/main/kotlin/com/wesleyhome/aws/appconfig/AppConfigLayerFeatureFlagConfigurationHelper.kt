package com.wesleyhome.aws.appconfig

import com.wesleyhome.aws.appconfig.api.ConfigurationApi
import org.openapitools.client.infrastructure.ApiClient
import retrofit2.Call

abstract class AppConfigLayerFeatureFlagConfigurationHelper<T : FeatureFlagConfiguration>(
    configuration: Configuration,
    override val flags: List<String> = emptyList(),
    apiClient: ConfigurationApi = ApiClient().createService(ConfigurationApi::class.java)
) : AppConfigLayerFreeFormConfigurationHelper<T>(configuration, apiClient), FeatureFlagConfigurationHelper<T> {

    override fun retrieveConfiguration(): Call<String> {
        return apiClient.getConfiguration(
            configuration = configuration.profile,
            application = configuration.application,
            environment = configuration.environment,
            flag = flags
        )
    }
}
