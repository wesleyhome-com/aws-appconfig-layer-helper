package com.wesleyhome.aws.appconfig

import assertk.assertThat
import assertk.assertions.isTrue
import com.wesleyhome.aws.appconfig.api.ConfigurationApi
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response

@ExtendWith(MockKExtension::class)
class AppConfigLayerFeatureFlagConfigurationHelperTest {

    @MockK
    private lateinit var apiClient: ConfigurationApi
    @InjectMockKs
    private lateinit var helper: TestFeatureFlagConfigurationHelper

    @Test
    fun testHelper() {
        every { apiClient.getConfiguration(
            helper.configuration.application,
            helper.configuration.environment,
            helper.configuration.profile,
            helper.flags
        ).execute() } returns Response.success("""
                        {
                          "enabled": true
                        }
                    """.trimIndent())
        val config: TestFeatureFlag = helper.readConfig()
        assertThat(config.enabled).isTrue()
    }


}

class TestFeatureFlagConfigurationHelper(apiClient: ConfigurationApi) : AppConfigLayerFeatureFlagConfigurationHelper<TestFeatureFlag>(
    Configuration("test-app", "test-env", "test-feature-flag"),
    emptyList(),
    apiClient = apiClient
)

data class TestFeatureFlag(
    override var enabled: Boolean = false
) : FeatureFlagConfiguration
