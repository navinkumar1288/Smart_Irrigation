package com.example.agricontrol;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MainActivity extends AppCompatActivity {

    /* ===== YOUR MQTT SETTINGS ===== */
    private static final String MQTT_BROKER   = "ssl://sdbc1da0.ala.asia-southeast1.emqxsl.com:8883";
    // If mobile data blocks 8883, try WebSocket TLS:
    // private static final String MQTT_BROKER = "wss://sdbc1da0.ala.asia-southeast1.emqxsl.com:8084/mqtt";

    private static final String MQTT_USERNAME = "YOUR_USER";
    private static final String MQTT_PASSWORD = "YOUR_PASSWORD";
    /* ============================== */

    private TextView statusText;
    private TextView brokerText;
    private Button btnConnect;

    private MqttAsyncClient client;
    private final String clientId = "android-" + Build.BOARD + "-" + Build.ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        brokerText = findViewById(R.id.brokerText);
        btnConnect = findViewById(R.id.btnConnect);

        brokerText.setText("Broker: " + MQTT_BROKER);

        btnConnect.setOnClickListener(v -> connect());
    }

    private void connect() {
        try {
            setStatus("Connecting…");

            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(false);         // persistent session
            opts.setKeepAliveInterval(45);
            opts.setConnectionTimeout(20);
            opts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            opts.setAutomaticReconnect(true);
            opts.setMaxInflight(20);
            opts.setUserName(MQTT_USERNAME);
            opts.setPassword(MQTT_PASSWORD.toCharArray());
            opts.setWill("apps/" + clientId + "/state",
                    "{\"online\":false}".getBytes(),
                    1, false);

            client = new MqttAsyncClient(MQTT_BROKER.trim(), clientId, null);

            client.setCallback(new MqttCallback() {
                @Override public void connectionLost(Throwable cause) {
                    setStatus("Connection lost: " + (cause == null ? "" : cause.getClass().getSimpleName() + " " + cause.getMessage()));
                }
                @Override public void messageArrived(String topic, MqttMessage message) { /* not used */ }
                @Override public void deliveryComplete(IMqttDeliveryToken token) { }
            });

            client.connect(opts, null, new IMqttActionListener() {
                @Override public void onSuccess(IMqttToken asyncActionToken) {
                    setStatus("Connected ✔");
                    DisconnectedBufferOptions buf = new DisconnectedBufferOptions();
                    buf.setBufferEnabled(true);
                    buf.setBufferSize(200);
                    buf.setPersistBuffer(false);
                    buf.setDeleteOldestMessages(true);
                    client.setBufferOpts(buf);
                    try {
                        client.publish("apps/" + clientId + "/state",
                                new MqttMessage("{\"online\":true}".getBytes()));
                    } catch (Exception ignored) {}
                }

                @Override public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    String msg;
                    if (exception instanceof javax.net.ssl.SSLHandshakeException) {
                        msg = "TLS error (hostname/CA). Try WSS URL.";
                    } else if (exception instanceof MqttSecurityException) {
                        msg = "Auth error (username/password or listener).";
                    } else {
                        msg = "Connect error: " + (exception == null ? "unknown" :
                                exception.getClass().getSimpleName() + ": " + exception.getMessage());
                    }
                    setStatus(msg);
                }
            });

        } catch (Exception e) {
            setStatus("Init error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void setStatus(String s) {
        runOnUiThread(() -> statusText.setText("Status: " + s));
    }
}