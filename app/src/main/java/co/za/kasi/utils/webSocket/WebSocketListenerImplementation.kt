package co.za.kasi.utils.webSocket

import android.util.Log
import co.za.kasi.services.LocalStorage
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListenerImplementation : WebSocketListener() {


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

//        webSocket.send("Hello World")
        Log.e("","8888888888888 = Connected")

      //  val driverId = "8612085494080"
        val driverId = LocalStorage.getSkynetDriverAccount().driver.identityNo

        val subscribeMessage = "{\"action\": \"subscribe\", \"topic\": \"topic/waybill/driver/$driverId\"}"
        webSocket.send(subscribeMessage)

        Log.d("WebSocket", "2222222222 Sent subscription message: $subscribeMessage")

    }


    override fun onMessage(webSocket: WebSocket, text: String) {
        outPut(
            text
        )

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)

        webSocket.close(NORMAL_CLOSURE_STATUS,null)
        outPut("Closing   : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        outPut(" onfailure Error : ${t.message}")
        outPut(" onfailure Error : ${t.cause?.message}")
        outPut(" onfailure Error  response message: ${response?.message}")
        outPut(" onfailure Error code  : ${response?.code}")
       // outPut(" onfailure Error : ${response.message}")
    }

    fun outPut(text: String){
        Log.e("","!!!OUT PUT DATA ++++  = $text")
    }

    companion object{
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}