package org.routeanalyzer.service

import org.routeanalyzer.model.MaxDistanceFromStart
import org.routeanalyzer.model.MostFrequentedArea
import org.routeanalyzer.model.Waypoint
import org.routeanalyzer.model.WaypointsOutsideGeofence
import java.io.File
import kotlin.math.*

object WaypointService {
    private val waypoints: MutableList<Waypoint> = mutableListOf()

    fun loadWaypoints(path: String) {
        if (waypoints.isNotEmpty()) return
        // Read file using sequence and populate the list in place
        File(path).useLines { lines ->
            waypoints.addAll(
                lines.map { line ->
                    // Destructure the line and convert to Double
                    val (timestamp, lat, lon) = line.split(";").map { it.toDouble() }
                    Waypoint(timestamp, lat, lon)
                }
            )
        }
    }

    fun maxDistanceFromStart(earthRadiusKm: Double): MaxDistanceFromStart {
        val start: Waypoint = waypoints.first()
        var maxDistance = 0.0
        var resultWaypoint: Waypoint = start
        waypoints.forEach { waypoint ->
            val distance = haversineDistance(
                start.latitude, start.longitude,
                waypoint.latitude, waypoint.longitude,
                earthRadiusKm
            )
            if (distance > maxDistance) {
                maxDistance = distance
                resultWaypoint = waypoint
            }
        }
        return MaxDistanceFromStart(resultWaypoint, maxDistance)
    }

    private fun waypointsWithinRegion(
        latitude: Double,
        longitude: Double,
        r: Double,
        earthRadiusKm: Double
    ): List<Waypoint> {
        val resultWaypoints = mutableListOf<Waypoint>()
        for (waypoint in waypoints) {
            val distance =
                haversineDistance(latitude, longitude, waypoint.latitude, waypoint.longitude, earthRadiusKm)
            if (distance <= r) {
                resultWaypoints.add(waypoint)
            }
        }
        return resultWaypoints
    }

    private fun haversineDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        earthRadiusKm: Double
    ): Double {
        // Validate latitude (-90 to 90) and longitude (-180 to 180)
        require(lat1 in -90.0..90.0) { "Invalid lat1: $lat1. Must be between -90 and 90." }
        require(lat2 in -90.0..90.0) { "Invalid lat2: $lat2. Must be between -90 and 90." }
        require(lon1 in -180.0..180.0) { "Invalid lon1: $lon1. Must be between -180 and 180." }
        require(lon2 in -180.0..180.0) { "Invalid lon2: $lon2. Must be between -180 and 180." }

        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c  // Distance in km
    }

    private fun timeSpentWithinRegion(latitude: Double, longitude: Double, r: Double, earthRadiusKm: Double): Double {
        var minTime: Double = Double.MAX_VALUE
        var maxTime: Double = Double.MIN_VALUE
        val waypointsWithinRegion: List<Waypoint> = waypointsWithinRegion(latitude, longitude, r, earthRadiusKm)
        for (waypoint in waypointsWithinRegion) {
            if (waypoint.timestamp < minTime) {
                minTime = waypoint.timestamp
            } else if (waypoint.timestamp > maxTime) {
                maxTime = waypoint.timestamp
            }
        }
        return if (minTime != Double.MAX_VALUE && maxTime != Double.MIN_VALUE) {
            ((maxTime - minTime) * 10000).toInt().toDouble() / 10000
        } else {
            0.0
        }
    }

    fun waypointsOutsideGeofence(
        geofenceRadiusKm: Double,
        geofenceCenterLatitude: Double,
        geofenceCenterLongitude: Double,
        earthRadiusKm: Double
    ): WaypointsOutsideGeofence {
        val geofenceCenter = Waypoint(
            0.0,
            geofenceCenterLatitude,
            geofenceCenterLongitude
        )
        val outsideWaypoints = waypoints.filter { waypoint ->
            haversineDistance(
                geofenceCenter.latitude, geofenceCenter.longitude,
                waypoint.latitude, waypoint.longitude,
                earthRadiusKm
            ) > geofenceRadiusKm
        }
        return WaypointsOutsideGeofence(
            geofenceCenter,
            geofenceRadiusKm,
            outsideWaypoints.size,
            outsideWaypoints
        )
    }

    fun mostFrequentedArea(mostFrequentedAreaRadiusKm: Double, earthRadiusKm: Double): MostFrequentedArea {
        // Creating a map where the key is the waypoint itself and the value is a list of doubles
        var resultWaypoints = mutableMapOf<Waypoint, MutableList<Double>>()

        // Iterating over the waypoints to add the number of waypoints within a certain radius as first element of the value's list
        for (waypoint in waypoints) {
            resultWaypoints[waypoint] = mutableListOf(
                waypointsWithinRegion(
                    waypoint.latitude,
                    waypoint.longitude,
                    mostFrequentedAreaRadiusKm,
                    earthRadiusKm
                ).size.toDouble()
            )
        }

        // Getting the highest number of waypoints within a certain radius and filtering the map to only keep waypoints with that number
        val maxWaypointFrequency = (resultWaypoints.values.maxOfOrNull { it[0] } ?: 0).toDouble()
        resultWaypoints = resultWaypoints.filterValues { it[0] == maxWaypointFrequency }.toMutableMap()

        // Returning early if there are no ties after first filtering
        if (resultWaypoints.size == 1) {
            return MostFrequentedArea(
                resultWaypoints.keys.first(),
                mostFrequentedAreaRadiusKm,
                resultWaypoints.values.first()[0].toInt()
            )
        }

        // Iterating over the remaining waypoints to add the time spent within a certain radius as second element of the value's list
        for ((waypointKey, waypointValue) in resultWaypoints) {
            waypointValue.add(
                timeSpentWithinRegion(
                    waypointKey.latitude,
                    waypointKey.longitude,
                    mostFrequentedAreaRadiusKm,
                    earthRadiusKm
                )
            )
        }

        // Getting the highest time spent within a certain radius and filtering the map to only keep waypoints with that time
        val maxTimeSpent = (resultWaypoints.values.maxOfOrNull { it[1] } ?: 0).toDouble()
        resultWaypoints = resultWaypoints.filterValues { it[1] == maxTimeSpent }.toMutableMap()

        // Returning the first remaining waypoint with the highest frequency and time
        return MostFrequentedArea(
            resultWaypoints.keys.first(),
            mostFrequentedAreaRadiusKm,
            resultWaypoints.values.first()[0].toInt()
        )
    }
}