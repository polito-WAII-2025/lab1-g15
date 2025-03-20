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
    private val filePath = javaClass.getResource("/custom-parameters.yml")?.path
        ?: throw IllegalArgumentException("Test file 'custom-parameters.yml' not found")
    private val filePathNoOptional = javaClass.getResource(
        "/custom-parameters-no-optional.yml"
    )?.path
        ?: throw IllegalArgumentException(
            "Test file 'custom-parameters-no-optional.yml' not found"
        )
    private val filePathInvalid = javaClass.getResource("/custom-parameters-invalid.yml")?.path
        ?: throw IllegalArgumentException("Test file 'custom-parameters-invalid.yml' not found")
    private val invalidYmlFilePath = javaClass.getResource("/invalid.yml")?.path
        ?: throw IllegalArgumentException("Test file 'invalid.yml' not found")


    @Nested
    inner class LoadParamsTests {
        @Test
        @DisplayName("It should correctly load all the parameters")
        fun loadParamsSuccess() {
            Config.loadParams(filePath)

            assertEquals(6371.0, Config.earthRadiusKm)
            assertEquals(0.0, Config.geofenceCenterLatitude)
            assertEquals(0.0, Config.geofenceCenterLongitude)
            assertEquals(10.0, Config.geofenceRadiusKm)
            assertEquals(5.0, Config.mostFrequentedAreaRadiusKm)
        }

        @Test
        @DisplayName("It should load the parameters only once")
        fun loadParamsOnlyOnce() {
            Config.loadParams(filePath)

            // Change values after first load
            Config.earthRadiusKm = 5000.0

            // Call loadParams again
            Config.loadParams(filePath)

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
                Config.loadParams(filePathInvalid)
            }
        }

        @Test
        @DisplayName("It should throw an exception if the YAML file is invalid")
        fun loadParamsInvalidFile() {
            assertThrows<YAMLException> {
                Config.loadParams(invalidYmlFilePath)
            }
        }
    }
}