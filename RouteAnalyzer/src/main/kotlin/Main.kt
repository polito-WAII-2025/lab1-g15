package org.routeanalyzer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.routeanalyzer.config.Config
import org.routeanalyzer.model.AnalysisResult
import org.routeanalyzer.model.MaxDistanceFromStart
import org.routeanalyzer.service.WaypointService
import java.io.File

fun main() {
    val resourcesFolder = "RouteAnalyzer/src/main/resources"
    val isContainer = System.getenv("CONTAINER")?.toBoolean() ?: false

    val configFile =
        if (isContainer) "/app/resources/" + System.getenv("CONFIG_FILE") else "$resourcesFolder/custom-parameters.yml"
    val waypointsFile =
        if (isContainer) "/app/resources/" + System.getenv("WAYPOINTS_FILE") else "$resourcesFolder/waypoints.csv"

    println("Loading config from $configFile")
    Config.loadParams(configFile)
    WaypointService.loadWaypoints(waypointsFile)

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
    val outputFile = if (isContainer) File("./resources/output.json") else File("./evaluation/output.json")
    outputFile.writeText(resultJsonString)
    println("JSON file written successfully with the following result: \n $resultJsonString")
}