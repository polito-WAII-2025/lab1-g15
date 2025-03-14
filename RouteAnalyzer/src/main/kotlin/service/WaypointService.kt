package org.routeanalyzer.service

import org.routeanalyzer.config.Config
import org.routeanalyzer.model.MaxDistanceFromStart
import org.routeanalyzer.model.Waypoint
import org.routeanalyzer.model.WaypointsOutsideGeofence
import java.io.File
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2
import kotlin.math.pow

object WaypointService {
    private var waypoints: List<Waypoint> = emptyList()

    fun loadWaypoints(path: String) {
        if (waypoints.isNotEmpty()) {
            return
        }
        val file = File(path)
        val loadedWaypoints = file.readLines().map { line ->
            val parts = line.split(";")
            Waypoint(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
        }
        waypoints = loadedWaypoints
    }

    fun maxDistanceFromStart(): MaxDistanceFromStart {
        val start: Waypoint = waypoints.first()
        var maxDistance: Double = 0.0
        var resultWaypoint: Waypoint = start
        waypoints.forEach { waypoint ->
            val distance = haversineDistance(
                start.lat, start.long,
                waypoint.lat, waypoint.long,
                Config.earthRadiusKm!!
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
        r: Double
    ): List<Waypoint> {
        val resultWaypoints = mutableListOf<Waypoint>()
        for (waypoint in waypoints) {
            val distance = haversineDistance(latitude, longitude, waypoint.lat, waypoint.long, Config.earthRadiusKm!!)
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

    private fun timeSpentWithinRegion(latitude: Double, longitude: Double, r: Double): Double {
        var minTime: Double = Double.MAX_VALUE
        var maxTime: Double = Double.MIN_VALUE
        val waypointsWithinRegion: List<Waypoint> = waypointsWithinRegion(latitude, longitude, r)
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

    fun waypointsOutsideGeofence(): WaypointsOutsideGeofence {
        val geofenceCenter = Waypoint(0.0, Config.geofenceCenterLatitude, Config.geofenceCenterLongitude)
        val outsideWaypoints =  waypoints.filter { waypoint ->
            haversineDistance(geofenceCenter.lat, geofenceCenter.long, waypoint.lat, waypoint.long, Config.earthRadiusKm!!) > Config.geofenceRadiusKm
        }
        return WaypointsOutsideGeofence(geofenceCenter, Config.geofenceRadiusKm, outsideWaypoints.size, outsideWaypoints)
    }
}