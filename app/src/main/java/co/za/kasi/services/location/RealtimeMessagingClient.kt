package co.za.kasi.services.location

import co.za.kasi.model.superApp.a.location.CreateDriverLocation

interface RealtimeMessagingClient {
    suspend fun sendDriverLocation(location: CreateDriverLocation)
    suspend fun close()
}