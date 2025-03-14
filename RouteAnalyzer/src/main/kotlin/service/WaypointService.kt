package org.routeanalyzer.service

import org.routeanalyzer.model.Waypoint
import java.io.File
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2
import kotlin.math.pow

object WaypointService {
    private var waypoints: List<Waypoint>? = null;

    fun loadWaypoints(path: String) {
        if (waypoints != null) {
            return
        }
        val file = File(path)
        val loadedWaypoints = file.readLines().map { line ->
            val parts = line.split(";")
            Waypoint(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
        }
        waypoints = loadedWaypoints
    }

    fun waypointsWithinRegion(latitude : Double, longitude : Double, r: Double, earthRadiusKm: Double, lines : List<String>) : List<String> {
        val waypoints = mutableListOf<String>()
        for (line in lines) {
            val parts = line.split(";")
            if (parts.size < 3) {
                continue
            }
            val lat = parts[1].toDouble()
            val lon = parts[2].toDouble()
            val distance = haversineDistance(latitude, longitude, lat, lon, earthRadiusKm)

            if (distance <= r) {
                waypoints.add(line)
            }
        }
        return waypoints
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, earthRadiusKm: Double): Double {
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

    fun timeSpentWithinRegion(latitude: Double, longitude: Double, r: Double, lines: List<String>) : Double {
        var minTime: Double = Double.MAX_VALUE
        var maxTime: Double = Double.MIN_VALUE
        val waypointsWithinRegion =  waypointsWithinRegion(latitude, longitude, r,6371.0, lines)
        for (waypoint in waypointsWithinRegion) {
            val time = waypoint.split(";")[0].toDouble()
            if (time < minTime){
                minTime = time
            }
            else if (time > maxTime) {
                maxTime = time
            }
        }
        return if ( minTime != Double.MAX_VALUE && maxTime != Double.MIN_VALUE) {
            ((maxTime - minTime)*10000).toInt().toDouble()/10000
        }
        else {
            0.0
        }
    }
}