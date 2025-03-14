package org.routeanalyzer.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisResult(
    val maxDistanceFromStart: MaxDistanceFromStart,
    val mostFrequentedArea: MostFrequentedArea,
    val waypointsOutsideGeofence: WaypointsOutsideGeofence
)
