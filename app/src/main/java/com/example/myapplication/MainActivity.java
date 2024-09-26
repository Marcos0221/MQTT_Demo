package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity:";
    private ToggleButton toggleButton1, toggleButton2;

    private MqttClient client;
    private MqttConnectOptions options;

    private String server_addr = "192.168.0.26";
    private String servet_port = "1883";
    private String topic = "dev/pet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Config config = new Config(this);
        toggleButton1 = findViewById(R.id.water);
        toggleButton2 = findViewById(R.id.food);

        Button btn = findViewById(R.id.configuration);
        btn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,
                    ConfigurationActivity.class));
        });

        if (config.exists()) {
            server_addr = config.getServer_addr();
            servet_port = config.getServer_port();
            topic = config.getTopic();
            Init_MQTT();
            ConnMQTTBroken(true);

            toggleButton1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (!isChecked) {
                    // 开启
                    publish(topic, "WATER_OPEN");
                } else {
                    // 关闭
                    publish(topic, "WATER_CLOSE");
                }
            });

            toggleButton2.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (!isChecked) {
                    publish(topic, "FOOD_OPEN");
                } else {
                    publish(topic, "FOOD_CLOSE");
                }
            });
        } else {
            Toast.makeText(this, "还没有生成配置文件，请点击设置配置",
                    Toast.LENGTH_LONG).show();
        }


    }

    /**
     * 初始化MQTT配置
     */
    public void Init_MQTT() {
        try {
            client = new MqttClient("tcp://" + server_addr + ":" +servet_port,
                    "Android_Terminal",
                    new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        options = new MqttConnectOptions();
        options.setCleanSession(true);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "主题" + topic + "\t消息" + message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    /**
     * 发布订阅
     *
     * @param topic
     * @param msg
     */
    public void publish(String topic, String msg) {
        try {
            client.publish(topic, new MqttMessage(msg.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接 & 断开 MQTT 连接
     *
     * @param isConnected
     */
    public void ConnMQTTBroken(boolean isConnected) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    try {
                        if (!client.isConnected()) {
                            client.connect(options);
                        }
                        Log.i(TAG, "连接成功");
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.i(TAG, "连接失败");
                    }
                } else {
                    try {
                        if (client.isConnected()) {
                            client.disconnect();
                        }
                        Log.i(TAG, "断开成功");
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.i(TAG, "断开失败");
                    }
                }
            }
        }).start();
    }
}