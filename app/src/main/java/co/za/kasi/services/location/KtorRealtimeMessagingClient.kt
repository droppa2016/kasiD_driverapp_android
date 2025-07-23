package co.za.kasi.services.location

import android.util.Log
import co.za.kasi.model.superApp.a.location.CreateDriverLocation
import io.ktor.client.*
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.*
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient


class KtorRealtimeMessagingClient(
    private val client: HttpClient
): RealtimeMessagingClient {

    private var session: WebSocketSession? = null
    private var isConnected = false
    private lateinit var stompClient: StompClient

    private suspend fun ensureConnected() {
        if (session == null || session?.isActive != true) {
            try {
             //   Log.d("WebSocket", "Attempting to connect to ws://88.99.94.84:8185/drivers-websocket")

                session = client.webSocketSession {
                    url("ws://88.99.94.84:8185/drivers-websocket")
                }

                isConnected = session?.isActive == true

                if (isConnected) {
                //    Log.d("WebSocket", "Connected successfully ")
                } else {
                    Log.e("WebSocket", "Connection failed ")
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error during connection: ${e.message}", e)
                isConnected = false
            }
        } else {
         //   Log.d("WebSocket", "Already connected")
        }
    }

    fun isWebSocketConnected(): Boolean {
        return isConnected && session?.isActive == true
    }

    private fun connectStomp() {
        val url = "ws://88.99.94.84:8185/drivers-websocket"
        stompClient = Stomp.over(Stomp.ConnectionProvider.JWS, url)
        stompClient.connect()
       // Log.d("WebSocket", " " + stompClient.isConnected)
    }


    override suspend fun sendDriverLocation(location: CreateDriverLocation) {

      //  ensureConnected()
        connectStomp()


        val json = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("driverId", location.driverId)
            put("batteryPercentage", location.batteryPercentage)
            put("dateTime", location.dateTime.toString())
            put("deviceId", location.deviceId)
        }

        stompClient.send("/app/location/update", json.toString())
            .subscribe({
             //   Log.d("WebSocket", " Location update sent successfully")
            }, { error ->
               // Log.e("WebSocket", " Failed to send location update", error)
            })

        if (!isWebSocketConnected()) {
            Log.e("WebSocket", "Not connected. Skipping send.")
            return
        }

        val payload = Json.encodeToString(location)
        session?.outgoing?.send(Frame.Text("driver_location#$payload"))

//        val json = Json.encodeToString(location)
//        session?.send(Frame.Text(json))

      //  Log.d("WebSocket", "Sent message: $json")

    }

    override suspend fun close() {
        session?.close()
        session = null
    }


}