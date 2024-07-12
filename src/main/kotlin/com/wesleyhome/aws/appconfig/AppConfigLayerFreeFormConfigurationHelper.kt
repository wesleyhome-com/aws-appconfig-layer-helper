package com.wesleyhome.aws.appconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wesleyhome.aws.appconfig.api.ConfigurationApi
import org.openapitools.client.infrastructure.ApiClient
import retrofit2.Call
import kotlin.reflect.KClass
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.jvm.jvmErasure

abstract class AppConfigLayerFreeFormConfigurationHelper<T : FreeFormConfiguration>(
    override val configuration: Configuration,
    protected val apiClient: ConfigurationApi = ApiClient().createService(ConfigurationApi::class.java)
) : FreeFormConfigurationHelper<T> {
    private val configurationClass = javaClass.kotlin.allSupertypes.first()
            .arguments.first().type?.jvmErasure as KClass<T>

    private val mapper: ObjectMapper = jacksonObjectMapper()

    override fun readConfig(): T {
        val response = retrieveConfiguration().execute()
        return response.body().let {
            mapper.readerFor(configurationClass.java).readValue(it)
        }
    }

    open fun retrieveConfiguration(): Call<String> {

        return apiClient.getConfiguration(
            configuration = configuration.profile,
            application = configuration.application,
            environment = configuration.environment
        )
    }
}
