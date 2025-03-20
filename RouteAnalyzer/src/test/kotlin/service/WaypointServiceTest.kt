package service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.routeanalyzer.model.Waypoint
import org.routeanalyzer.service.WaypointService
import java.io.FileNotFoundException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class WaypointServiceTest {
    @Suppress("UNCHECKED_CAST")
    val waypoints = waypointsField.get(null) as MutableList<Waypoint>
    val EARTH_RADIUS = 6371.0

    // Private methods
    val haversineDistanceMethod = Companion.haversineDistanceMethod
    val waypointsWithinRegionMethod = Companion.waypointsWithinRegionMethod
    val timeSpentWithinRegionMethod = Companion.timeSpentWithinRegionMethod

    @DisplayName("Modify private fields and methods visibility to access them inside tests")
    companion object {
        lateinit var waypointsField: Field
        lateinit var haversineDistanceMethod: Method
        lateinit var waypointsWithinRegionMethod: Method
        lateinit var timeSpentWithinRegionMethod: Method

        @BeforeAll
        @JvmStatic
        fun modifyMethodsVisibility() {
            // Use reflection to access private fields or methods
            val waypoints = WaypointService::class.java.getDeclaredField("waypoints")
            val haversineDistance = WaypointService::class.java.declaredMethods.find { it.name == "haversineDistance" }
            val waypointsWithinRegion =
                WaypointService::class.java.declaredMethods.find { it.name == "waypointsWithinRegion" }
            val timeSpentWithinRegion =
                WaypointService::class.java.declaredMethods.find { it.name == "timeSpentWithinRegion" }

            // Test for null references
            requireNotNull(waypoints) { "Field 'waypoints' not found" }
            requireNotNull(haversineDistance) { "Method 'haversineDistance' not found" }
            requireNotNull(waypointsWithinRegion) { "Method 'waypointsWithinRegion' not found" }
            requireNotNull(timeSpentWithinRegion) { "Method 'timeSpentWithinRegion' not found" }

            // Make them accessible
            waypoints.isAccessible = true
            haversineDistance.isAccessible = true
            waypointsWithinRegion.isAccessible = true
            timeSpentWithinRegion.isAccessible = true

            // Assign the reference to a global variable
            waypointsField = waypoints
            haversineDistanceMethod = haversineDistance
            waypointsWithinRegionMethod = waypointsWithinRegion
            timeSpentWithinRegionMethod = timeSpentWithinRegion
        }
    }

    @Nested
    inner class HaversineDistanceTests {

        @Test
        @DisplayName("It should calculate correct distance when given two points")
        fun harvesineDistance() {
            val params = arrayOf(45.0, 90.0, 46.0, 91.0, 6371.0)
            val distance = haversineDistanceMethod.invoke(WaypointService, *params) as Double
            assertEquals(135.78, distance, 0.01)
        }

        @Test
        @DisplayName("It should return zero when the two points are the same")
        fun harvesineDistanceSamePoint() {
            val params = arrayOf(45.0, 90.0, 45.0, 90.0, 6371.0)
            val distance = haversineDistanceMethod.invoke(WaypointService, *params) as Double
            assertEquals(0.0, distance)
        }

        @Test
        @DisplayName("It should throw an exception when positive latitude value is invalid")
        fun harvesineDistanceInvalidPositiveLatitude() {
            // Possible latitude values: [-90, +90]
            val invalidPositiveLatitude = 100.0
            val params = arrayOf(invalidPositiveLatitude, 90.0, 45.0, 90.0, 6371.0)
            val params2 = arrayOf(45.0, 90.0, invalidPositiveLatitude, 90.0, 6371.0)

            // Extract exception cause
            val exception = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params) as Double
            }.cause
            val exception2 = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params2) as Double
            }.cause

            // Assert exception
            assert(exception is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception?.javaClass}" }
            assertEquals("Invalid lat1: $invalidPositiveLatitude. Must be between -90 and 90.", exception?.message)
            assert(exception2 is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception2?.javaClass}" }
            assertEquals("Invalid lat2: $invalidPositiveLatitude. Must be between -90 and 90.", exception2?.message)
        }

        @Test
        @DisplayName("It should throw an exception when negative latitude value is invalid")
        fun harvesineDistanceInvalidNegativeLatitude() {
            // Possible latitude values: [-90, +90]
            val invalidNegativeLatitude = -100.0
            val params = arrayOf(invalidNegativeLatitude, 90.0, 45.0, 90.0, 6371.0)
            val params2 = arrayOf(45.0, 90.0, invalidNegativeLatitude, 90.0, 6371.0)

            // Extract exception cause
            val exception = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params) as Double
            }.cause
            val exception2 = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params2) as Double
            }.cause

            // Assert exception
            assert(exception is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception?.javaClass}" }
            assertEquals("Invalid lat1: $invalidNegativeLatitude. Must be between -90 and 90.", exception?.message)
            assert(exception2 is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception2?.javaClass}" }
            assertEquals("Invalid lat2: $invalidNegativeLatitude. Must be between -90 and 90.", exception2?.message)
        }

        @Test
        @DisplayName("It should throw an exception when positive longitude value is invalid")
        fun harvesineDistanceInvalidPositiveLongitude() {
            // Possible latitude values: [-180, +180]
            val invalidPositiveLongitude = 190.0
            val params = arrayOf(45.0, invalidPositiveLongitude, 45.0, 90.0, 6371.0)
            val params2 = arrayOf(45.0, 90.0, 45.0, invalidPositiveLongitude, 6371.0)

            // Extract exception cause
            val exception = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params) as Double
            }.cause
            val exception2 = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params2) as Double
            }.cause

            // Assert exception
            assert(exception is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception?.javaClass}" }
            assertEquals("Invalid lon1: $invalidPositiveLongitude. Must be between -180 and 180.", exception?.message)
            assert(exception2 is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception2?.javaClass}" }
            assertEquals("Invalid lon2: $invalidPositiveLongitude. Must be between -180 and 180.", exception2?.message)
        }

        @Test
        @DisplayName("It should throw an exception when negative longitude value is invalid")
        fun harvesineDistanceInvalidNegativeLongitude() {
            // Possible latitude values: [-180, +180]
            val invalidPositiveLongitude = -190.0
            val params = arrayOf(45.0, invalidPositiveLongitude, 45.0, 90.0, 6371.0)
            val params2 = arrayOf(45.0, 90.0, 45.0, invalidPositiveLongitude, 6371.0)

            // Extract exception cause
            val exception = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params) as Double
            }.cause
            val exception2 = assertThrows<InvocationTargetException> {
                haversineDistanceMethod.invoke(WaypointService, *params2) as Double
            }.cause

            // Assert exception
            assert(exception is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception?.javaClass}" }
            assertEquals("Invalid lon1: $invalidPositiveLongitude. Must be between -180 and 180.", exception?.message)
            assert(exception2 is IllegalArgumentException) { "Expected IllegalArgumentException but got ${exception2?.javaClass}" }
            assertEquals("Invalid lon2: $invalidPositiveLongitude. Must be between -180 and 180.", exception2?.message)
        }
    }

    @Nested
    inner class LoadWaypointsTests {
        private val filePath = javaClass.getResource("/waypoints.csv")?.path
            ?: throw IllegalArgumentException("Test file 'waypoints.csv' not found")
        private val filePath2 = javaClass.getResource("/waypoints2.csv")?.path
            ?: throw IllegalArgumentException("Test file 'waypoints2.csv' not found")
        private val emptyFilePath = javaClass.getResource("/waypoints_empty.csv")?.path
            ?: throw IllegalArgumentException("Test file 'waypoints_empty.csv' not found")

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should correctly load the waypoints list")
        fun loadWaypointsFile() {
            val expectedWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0, 45.06271, 7.67905),
                Waypoint(1741358198686.5452, 45.06312, 7.67791),
                Waypoint(1741358198700.0903, 45.06353, 7.67678)
            )
            // Read and load waypoints
            WaypointService.loadWaypoints(filePath)

            assertEquals(expectedWaypoints.size, waypoints.size)
            assertEquals(expectedWaypoints, waypoints)
        }

        @Test
        @DisplayName("It should not reload the waypoints list if it's not empty")
        fun loadWaypointsShouldNotReload() {
            // First call to load waypoints
            WaypointService.loadWaypoints(filePath)
            // Save the current size of waypoints list
            val initialSize = waypoints.size
            // Call again, should not reload the data
            WaypointService.loadWaypoints(filePath2)

            // Verify that the size of the list is still the same
            assertEquals(initialSize, waypoints.size)
        }

        @Test
        @DisplayName("It should correctly handle the loading from an empty file")
        fun loadWaypointsWithEmptyFile() {
            // Load waypoints from an empty file
            WaypointService.loadWaypoints(emptyFilePath)

            // Verify that the list of waypoints is empty
            assertTrue(waypoints.isEmpty())
        }

        @Test
        @DisplayName("It should throw an exception if the file path is invalid")
        fun loadWaypointsWithNonExistentFile() {
            val nonExistentFilePath = "nonexistent/file"

            // Try loading from a non-existent file, it should throw an exception
            assertThrows<FileNotFoundException> {
                WaypointService.loadWaypoints(nonExistentFilePath)
            }
        }
    }

    @Nested
    inner class MaxDistanceFromStartTests {
        private val expectedWaypoints: List<Waypoint> = listOf(
            Waypoint(1741358198673.0000, 45.06898, 7.67420),
            Waypoint(1741358198686.5452, 45.07309, 7.64890),
            Waypoint(1741358198700.0903, 45.06493, 7.58877),
            Waypoint(1741358198686.5452, 45.07236, 7.51505)
        )
        private val expectedWaypointsEmpty: List<Waypoint> = emptyList()

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should calculate the correct distance from the first waypoint")
        fun maxDistanceFromStart() {
            // Last waypoint is the farthest
            val startWaypoint = expectedWaypoints.first()
            val endWaypoint = expectedWaypoints.last()
            val params = arrayOf(
                startWaypoint.latitude,
                startWaypoint.longitude,
                endWaypoint.latitude,
                endWaypoint.longitude,
                EARTH_RADIUS
            )
            val expectedDistance = haversineDistanceMethod.invoke(WaypointService, *params) as Double

            // Add waypoints
            waypoints.addAll(expectedWaypoints)
            val maxDistanceObj = WaypointService.maxDistanceFromStart(EARTH_RADIUS)

            assertEquals(expectedDistance, maxDistanceObj.distanceKm)
            assertEquals(endWaypoint, maxDistanceObj.waypoint)
        }

        @Test
        @DisplayName("It should throw an exception if the waypoints list is empty")
        fun maxDistanceFromStartEmptyList() {
            assertEquals(expectedWaypointsEmpty, waypoints)
            assertThrows<NoSuchElementException> {
                WaypointService.maxDistanceFromStart(EARTH_RADIUS)
            }
        }

        @Test
        @DisplayName("It should return zero distance if there is only one waypoint")
        fun maxDistanceFromStartSingleWaypoint() {
            // Add single waypoint
            val waypoint = Waypoint(1741358198686.5452, 45.07236, 7.51505)
            waypoints.add(waypoint)

            val maxDistanceObj = WaypointService.maxDistanceFromStart(EARTH_RADIUS)

            assertEquals(0.0, maxDistanceObj.distanceKm)
            assertEquals(waypoint, maxDistanceObj.waypoint)

        }

        @Test
        @DisplayName("It should return zero distance if the waypoints are the same")
        fun maxDistanceFromStartSameWaypoints() {
            // Add multiple equal waypoint
            val waypoint = Waypoint(1741358198686.5452, 45.07236, 7.51505)
            waypoints.addAll(listOf(waypoint, waypoint, waypoint))

            val maxDistanceObj = WaypointService.maxDistanceFromStart(EARTH_RADIUS)

            assertEquals(0.0, maxDistanceObj.distanceKm)
            assertEquals(waypoint, maxDistanceObj.waypoint)

        }

    }

    @Nested
    inner class WaypointsWithinRegionTests {

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should return all the waypoints")
        fun waypointsWithinRegion() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 15.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)

            // Add waypoints
            waypoints.addAll(testWaypoints)

            @Suppress("UNCHECKED_CAST")
            val result = waypointsWithinRegionMethod.invoke(WaypointService, *params) as List<Waypoint>

            assertEquals(4, result.size)
            assertEquals(result, testWaypoints)
        }

        @Test
        @DisplayName("It should return an empty list of waypoints")
        fun waypointsWithinRegionEmpty() {
            val radius = 9.5 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)

            @Suppress("UNCHECKED_CAST")
            val result = waypointsWithinRegionMethod.invoke(WaypointService, *params) as List<Waypoint>

            assertEquals(0, result.size)
            assertTrue(result.isEmpty())
        }

        @Test
        @DisplayName("It should return all the waypoints except the last one")
        fun waypointsWithinRegionExceptLast() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 9.5 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)

            // Add waypoints
            waypoints.addAll(testWaypoints)

            @Suppress("UNCHECKED_CAST")
            val result = waypointsWithinRegionMethod.invoke(WaypointService, *params) as List<Waypoint>

            assertEquals(3, result.size)
            assertTrue(result.contains(testWaypoints[0]))
            assertTrue(result.contains(testWaypoints[1]))
            assertTrue(result.contains(testWaypoints[2]))
            assertFalse(result.contains(testWaypoints[3]))
        }

        @Test
        @DisplayName("It should include the waypoints on the border")
        fun waypointsWithinRegionOnBorder() {
            val centerLat = 45.0685
            val centerLon = 7.6843
            val radius = 0.5
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)
            val testWaypoints = listOf(
                Waypoint(0.0, 45.0670, 7.6783),  // Exactly at the edge
                Waypoint(1.0, 45.0669, 7.6783)   // Slightly outside
            )

            waypoints.addAll(testWaypoints)

            @Suppress("UNCHECKED_CAST")
            val result = waypointsWithinRegionMethod.invoke(WaypointService, *params) as List<Waypoint>

            assertEquals(1, result.size)
            assertTrue(result.contains(testWaypoints[0]))  // Ensure the edge waypoint is included
        }

        @Test
        @DisplayName("It should return an empty list of waypoints")
        fun waypointsWithinRegionAllOutside() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 0.1 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)

            // Add waypoints
            waypoints.addAll(testWaypoints)

            @Suppress("UNCHECKED_CAST")
            val result = waypointsWithinRegionMethod.invoke(WaypointService, *params) as List<Waypoint>

            assertEquals(0, result.size)
            assertTrue(result.isEmpty())
        }
    }

    @Nested
    inner class TimeSpentWithinRegionTests {

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should return the correct time considering all the waypoints")
        fun timeSpentWithinRegionAllWaypoints() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1.0, 45.06898, 7.67420),
                Waypoint(2.0, 45.07309, 7.64890),
                Waypoint(3.0, 45.06493, 7.58877),
                Waypoint(4.0, 45.07236, 7.51505)
            )
            val radius = 20.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)
            val expectedResult = testWaypoints.last().timestamp - testWaypoints.first().timestamp

            // Add waypoints
            waypoints.addAll(testWaypoints)
            val result = timeSpentWithinRegionMethod.invoke(WaypointService, *params) as Double

            assertEquals(expectedResult, result)
        }

        @Test
        @DisplayName("It should return the correct time considering only some waypoint")
        fun timeSpentWithinRegionSomeWaypoints() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1.0, 45.06898, 7.67420),
                Waypoint(2.0, 45.07309, 7.64890),
                Waypoint(3.0, 45.06493, 7.58877),
                Waypoint(4.0, 45.07236, 7.51505)
            )
            val radius = 11.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)
            val expectedResult = 2.0 // Last waypoint should be excluded

            // Add waypoints
            waypoints.addAll(testWaypoints)
            val result = timeSpentWithinRegionMethod.invoke(WaypointService, *params) as Double

            assertEquals(expectedResult, result)
        }

        @Test
        @DisplayName("It should return the correct time considering a single waypoint")
        fun timeSpentWithinRegionSingleWaypoint() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1.0, 45.06898, 7.67420)
            )
            val radius = 11.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)
            val expectedResult = 0.0

            // Add waypoints
            waypoints.addAll(testWaypoints)
            val result = timeSpentWithinRegionMethod.invoke(WaypointService, *params) as Double

            assertEquals(expectedResult, result)
        }

        @Test
        @DisplayName("It should return the correct time when no waypoints are provided")
        fun timeSpentWithinRegionNoWaypoints() {
            val radius = 11.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812
            val params = arrayOf(centerLat, centerLon, radius, EARTH_RADIUS)
            val expectedResult = 0.0

            // Add waypoints
            val result = timeSpentWithinRegionMethod.invoke(WaypointService, *params) as Double

            assertEquals(expectedResult, result)
        }
    }

    @Nested
    inner class WaypointsOutsideGeofenceTests {

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should return all the waypoints outside the geofence")
        fun waypointsOutsideGeofence() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 0.5 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.waypointsOutsideGeofence(radius, centerLat, centerLon, EARTH_RADIUS)

            assertEquals(4, result.count)
            assertEquals(testWaypoints, result.waypoints)
        }

        @Test
        @DisplayName("It should return an empty list of waypoints")
        fun waypointsOutsideGeofenceEmpty() {
            val radius = 0.5 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812

            val result = WaypointService.waypointsOutsideGeofence(radius, centerLat, centerLon, EARTH_RADIUS)

            assertEquals(0, result.count)
            assertTrue(result.waypoints.isEmpty())
        }

        @Test
        @DisplayName("It should return only the last waypoint")
        fun waypointsOutsideGeofenceOnlyLastOutside() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 9.5 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.waypointsOutsideGeofence(radius, centerLat, centerLon, EARTH_RADIUS)

            assertEquals(1, result.count)
            assertTrue(result.waypoints.contains(testWaypoints.last()))
        }

        @Test
        @DisplayName("It should exclude the waypoints on the border")
        fun waypointsOutsideGeofenceOnBorder() {
            val centerLat = 45.0685
            val centerLon = 7.6843
            val radius = 0.5
            val testWaypoints = listOf(
                Waypoint(0.0, 45.0670, 7.6783),  // Exactly at the edge
                Waypoint(1.0, 45.0669, 7.6783)   // Slightly outside
            )

            waypoints.addAll(testWaypoints)

            val result = WaypointService.waypointsOutsideGeofence(radius, centerLat, centerLon, EARTH_RADIUS)

            assertEquals(1, result.count)
            assertTrue(result.waypoints.contains(testWaypoints.last()))
        }

        @Test
        @DisplayName("It should return an empty list of waypoints")
        fun waypointsWithinRegionAllOutside() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198686.5452, 45.07236, 7.51505)
            )
            val radius = 20.0 // Km
            val centerLat = 45.0721
            val centerLon = 7.6812

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.waypointsOutsideGeofence(radius, centerLat, centerLon, EARTH_RADIUS)

            assertEquals(0, result.count)
            assertTrue(result.waypoints.isEmpty())
        }
    }

    @Nested
    inner class MostFrequentedAreaTests {

        @BeforeEach
        @DisplayName("Set the waypoints list to an empty list")
        fun resetWaypoints() {
            waypoints.clear()
        }

        @Test
        @DisplayName("It should include all the waypoints")
        fun mostFrequentedArea() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
            )
            val radius = 5.0 // Km

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.mostFrequentedArea(radius, EARTH_RADIUS)

            assertEquals(3, result.entriesCount)
            assertEquals(result.centralWaypoint, testWaypoints[1])
        }

        @Test
        @DisplayName("It should consider the only waypoint as the centre of the area")
        fun mostFrequentedAreaSingleWaypoint() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
            )
            val radius = 5.0 // Km

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.mostFrequentedArea(radius, EARTH_RADIUS)

            assertEquals(1, result.entriesCount)
            assertEquals(result.centralWaypoint, testWaypoints.first())
        }

        @Test
        @DisplayName("It should throw an exception when the waypoints list is empty")
        fun mostFrequentedAreaEmpty() {
            val radius = 9.5 // Km

            assertThrows<NoSuchElementException> {
                WaypointService.mostFrequentedArea(radius, EARTH_RADIUS)
            }
        }

        @Test
        @DisplayName("It should include all the waypoints except the last one")
        fun mostFrequentedAreaExceptLast() {
            val testWaypoints: List<Waypoint> = listOf(
                Waypoint(1741358198673.0000, 45.06898, 7.67420),
                Waypoint(1741358198686.5452, 45.07309, 7.64890),
                Waypoint(1741358198700.0903, 45.06493, 7.58877),
                Waypoint(1741358198700.0903, 45.01493, 7.18877),
            )
            val radius = 5.0 // Km

            // Add waypoints
            waypoints.addAll(testWaypoints)

            val result = WaypointService.mostFrequentedArea(radius, EARTH_RADIUS)

            assertEquals(3, result.entriesCount)
            assertEquals(result.centralWaypoint, testWaypoints[1])
        }

    }
}