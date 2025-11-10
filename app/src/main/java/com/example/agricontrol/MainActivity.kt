package com.example.agricontrol

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.*

/* ====== YOUR MQTT SETTINGS ====== */
private const val MQTT_BROKER = "ssl://sdbc1da0.ala.asia-southeast1.emqxsl.com:8883"
// If 8883 is blocked on mobile data, use WebSocket TLS:
// private const val MQTT_BROKER = "wss://sdbc1da0.ala.asia-southeast1.emqxsl.com:8084/mqtt"

private const val MQTT_USERNAME = "YOUR_USER"
private const val MQTT_PASSWORD = "YOUR_PASSWORD"
/* ================================ */

class MainActivity : AppCompatActivity() {

    private var client: MqttAsyncClient? = null
    private lateinit var statusText: TextView
    private lateinit var brokerText: TextView
    private lateinit var btnConnect: Button

    private val clientId: String by lazy { "android-${Build.BOARD}-${Build.ID}" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        brokerText = findViewById(R.id.brokerText)
        btnConnect = findViewById(R.id.btnConnect)

        brokerText.text = "Broker: $MQTT_BROKER"

        btnConnect.setOnClickListener {
            connect()
        }
    }

    private fun connect() {
        try {
            status("Connecting…")
            val opts = MqttConnectOptions().apply {
                isCleanSession = false
                keepAliveInterval = 45
                connectionTimeout = 20
                mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
                isAutomaticReconnect = true
                maxInflight = 20
                userName = MQTT_USERNAME
                password = MQTT_PASSWORD.toCharArray()
                willTopic = "apps/$clientId/state"
                willMessage = MqttMessage("""{"online":false}""".toByteArray()).apply { qos = 1 }
            }

            val c = MqttAsyncClient(MQTT_BROKER.trim(), clientId, null)
            client = c

            c.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    status("Connection lost: ${cause?.javaClass?.simpleName ?: ""} ${cause?.localizedMessage ?: ""}")
                }
                override fun messageArrived(topic: String?, message: MqttMessage?) { /* not used here */ }
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            c.connect(opts, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    status("Connected ✔")
                    c.setBufferOpts(DisconnectedBufferOptions().apply {
                        isBufferEnabled = true; bufferSize = 200; isPersistBuffer = false; isDeleteOldestMessages = true
                    })
                    try {
                        c.publish(
                            "apps/$clientId/state",
                            MqttMessage("""{"online":true}""".toByteArray()).apply { qos = 1 }
                        )
                    } catch (_: Exception) {}
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    val msg = when (exception) {
                        is javax.net.ssl.SSLHandshakeException -> "TLS error (hostname/CA). Try WSS URL."
                        is MqttSecurityException -> "Auth error (username/password or listener)."
                        else -> "Connect error: ${exception?.javaClass?.simpleName}: ${exception?.localizedMessage}"
                    }
                    status(msg)
                }
            })

        } catch (e: Exception) {
            status("Init error: ${e.javaClass.simpleName}: ${e.localizedMessage}")
        }
    }

    private fun status(s: String) {
        statusText.text = "Status: $s"
    }
}