package org.routeanalyzer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.routeanalyzer.config.Config
import org.routeanalyzer.model.AnalysisResult
import org.routeanalyzer.model.MaxDistanceFromStart
import org.routeanalyzer.service.WaypointService
import java.io.File

fun main() {
    Config.loadParams("RouteAnalyzer/src/main/resources/custom-parameters.yml")
    WaypointService.loadWaypoints("RouteAnalyzer/src/main/resources/waypoints.csv")
    val maxDistanceFromStart: MaxDistanceFromStart = WaypointService.maxDistanceFromStart(Config.earthRadiusKm!!)
    if (Config.mostFrequentedAreaRadiusKm == null) {
        Config.mostFrequentedAreaRadiusKm = maxDistanceFromStart.distanceKm / 10
    }
    val json = Json { prettyPrint = true }
    val resultJsonString = json.encodeToString(
        AnalysisResult(
            WaypointService.maxDistanceFromStart(Config.earthRadiusKm!!),
            WaypointService.mostFrequentedArea(Config.mostFrequentedAreaRadiusKm!!, Config.earthRadiusKm!!),
            WaypointService.waypointsOutsideGeofence(
                Config.geofenceRadiusKm,
                Config.geofenceCenterLatitude,
                Config.geofenceCenterLongitude,
                Config.earthRadiusKm!!
            )
        )
    )
    val outputFile = File("output.json")
    outputFile.writeText(resultJsonString)
    println("JSON file written successfully with the following result: \n $resultJsonString")
}