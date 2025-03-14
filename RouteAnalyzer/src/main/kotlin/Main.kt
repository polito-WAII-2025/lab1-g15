package org.routeanalyzer

import org.routeanalyzer.config.Config
import org.routeanalyzer.model.Waypoint

import org.routeanalyzer.service.WaypointService

fun main() {
    Config.loadParams("RouteAnalyzer/src/main/resources/custom-parameters.yml")
    WaypointService.loadWaypoints("RouteAnalyzer/src/main/resources/waypoints.csv")
}