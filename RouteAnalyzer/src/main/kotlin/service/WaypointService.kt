package org.routeanalyzer.service

import org.routeanalyzer.model.Waypoint
import java.io.File

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


}