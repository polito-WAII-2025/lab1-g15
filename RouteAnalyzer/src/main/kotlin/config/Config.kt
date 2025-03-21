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
        println("Loading config parameters")
        if (earthRadiusKm != null) {
            return
        }
        val yaml = Yaml()
        println("after yaml")
        val inputStream = File(path).inputStream()
        println("after input stream")
        val data: Map<String, Any> = yaml.load(inputStream)
        println("After data")
        earthRadiusKm = (data["earthRadiusKm"] as Number).toDouble()
        geofenceCenterLatitude = (data["geofenceCenterLatitude"] as Number).toDouble()
        geofenceCenterLongitude = (data["geofenceCenterLongitude"] as Number).toDouble()
        geofenceRadiusKm = (data["geofenceRadiusKm"] as Number).toDouble()
        mostFrequentedAreaRadiusKm = (data["mostFrequentedAreaRadiusKm"] as? Number)?.toDouble()
    }
}