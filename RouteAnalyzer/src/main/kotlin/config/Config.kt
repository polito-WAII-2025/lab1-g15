package org.routeanalyzer.config


import org.yaml.snakeyaml.Yaml
import java.io.File


object Config {
    var earthRadiusKm: Double? = null
    var geofenceCenterLatitude: Double = 0.0
    var geofenceCenterLongitude: Double = 0.0
    var geofenceRadiusKm: Double = 0.0
    var mostFrequentedAreaRadiusKm: Double? = null // Optional

    fun loadParams(path: String) {
        if (earthRadiusKm != null) {
            return
        }
        try {
            val yaml = Yaml()
            val inputStream = File(path).inputStream()
            val data: Map<String, Any> = yaml.load(inputStream)

            try {
                earthRadiusKm = (data["earthRadiusKm"] as Number).toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value for 'earthRadiusKm'. Expected a number, but got: ${data["earthRadiusKm"]}")
            }

            try {
                geofenceCenterLatitude = (data["geofenceCenterLatitude"] as Number).toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value for 'geofenceCenterLatitude'. Expected a number, but got: ${data["geofenceCenterLatitude"]}")
            }

            try {
                geofenceCenterLongitude = (data["geofenceCenterLongitude"] as Number).toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value for 'geofenceCenterLongitude'. Expected a number, but got: ${data["geofenceCenterLongitude"]}")
            }

            try {
                geofenceRadiusKm = (data["geofenceRadiusKm"] as Number).toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value for 'geofenceRadiusKm'. Expected a number, but got: ${data["geofenceRadiusKm"]}")
            }

            try {
                mostFrequentedAreaRadiusKm = (data["mostFrequentedAreaRadiusKm"] as? Number)?.toDouble()
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid value for 'mostFrequentedAreaRadiusKm'. Expected a number or null, but got: ${data["mostFrequentedAreaRadiusKm"]}")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Error loading configuration from '$path': ${e.message}")
        }
    }
}