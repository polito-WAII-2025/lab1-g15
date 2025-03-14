package org.routeanalyzer.model

import kotlinx.serialization.Serializable


@Serializable
data class MaxDistanceFromStart(
    val waypoint: Waypoint,
    val distanceKm: Double
)