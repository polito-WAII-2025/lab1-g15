package config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.routeanalyzer.config.Config
import org.yaml.snakeyaml.error.YAMLException
import kotlin.test.assertNull

class ConfigTest {
    private val filePathCorrect =
        javaClass.getResource("/custom-parameters.yml")?.path?.let { java.net.URLDecoder.decode(it, "UTF-8") }
            ?: throw IllegalArgumentException("Test file 'custom-parameters.yml' not found")

    private val filePathNoOptional = javaClass.getResource(
        "/custom-parameters-no-optional.yml"
    )?.path?.let { java.net.URLDecoder.decode(it, "UTF-8") }
        ?: throw IllegalArgumentException(
            "Test file 'custom-parameters-no-optional.yml' not found"
        )

    private val filePathNoRequired =
        javaClass.getResource("/custom-parameters-no-required.yml")?.path?.let {
            java.net.URLDecoder.decode(
                it,
                "UTF-8"
            )
        }
            ?: throw IllegalArgumentException("Test file 'custom-parameters-custom-parameters-invalid-format.yml' not found")

    private val filePathInvalidFormat =
        javaClass.getResource("/custom-parameters-invalid-format.yml")?.path?.let {
            java.net.URLDecoder.decode(
                it,
                "UTF-8"
            )
        }
            ?: throw IllegalArgumentException("Test file 'custom-parameters-invalid-format.yml' not found")

    private val filePathInvalidDataType =
        javaClass.getResource("/custom-parameters-invalid-type.yml")?.path?.let {
            java.net.URLDecoder.decode(
                it,
                "UTF-8"
            )
        }
            ?: throw IllegalArgumentException("Test file 'custom-parameters-invalid-type.yml' not found")


    @Nested
    inner class LoadParamsTests {
        @Test
        @DisplayName("It should correctly load all the parameters")
        fun loadParamsSuccess() {
            Config.loadParams(filePathCorrect)

            assertEquals(6371.0, Config.earthRadiusKm)
            assertEquals(0.0, Config.geofenceCenterLatitude)
            assertEquals(0.0, Config.geofenceCenterLongitude)
            assertEquals(10.0, Config.geofenceRadiusKm)
            assertEquals(5.0, Config.mostFrequentedAreaRadiusKm)
        }

        @Test
        @DisplayName("It should load the parameters only once")
        fun loadParamsOnlyOnce() {
            Config.loadParams(filePathCorrect)

            // Change values after first load
            Config.earthRadiusKm = 5000.0

            // Call loadParams again
            Config.loadParams(filePathCorrect)

            assertEquals(5000.0, Config.earthRadiusKm)
        }

        @Test
        @DisplayName("It should correctly handle missing of optional fields")
        fun loadParamsMissingOptionals() {
            Config.loadParams(filePathNoOptional)

            assertEquals(6371.0, Config.earthRadiusKm)
            assertEquals(0.0, Config.geofenceCenterLatitude)
            assertEquals(0.0, Config.geofenceCenterLongitude)
            assertEquals(10.0, Config.geofenceRadiusKm)
            assertNull(Config.mostFrequentedAreaRadiusKm) // Should remain null
        }

        @Test
        @DisplayName("It should throw an exception when required fields are missing")
        fun loadParamsMissingRequired() {
            assertThrows<NullPointerException> {
                Config.loadParams(filePathNoRequired)
            }
        }

        @Test
        @DisplayName("It should throw an exception if the YAML file is invalid")
        fun loadParamsInvalidFile() {
            assertThrows<YAMLException> {
                Config.loadParams(filePathInvalidFormat)
            }
        }

        @Test
        @DisplayName("It should throw an exception if data type is invalid")
        fun loadParamsInvalidDataType() {
            assertThrows<ClassCastException> {
                Config.loadParams(filePathInvalidDataType)
            }
        }
    }
}