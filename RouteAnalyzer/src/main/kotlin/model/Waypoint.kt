package org.routeanalyzer.model

import kotlinx.serialization.Serializable

@Serializable
data class Waypoint(val timestamp: Double, val latitude: Double, val longitude: Double)
