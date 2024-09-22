package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

        toggleButton1 = findViewById(R.id.water);
        toggleButton2 = findViewById(R.id.food);

        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // 开启
                    Log.i(TAG, "开启");
                } else {
                    // 关闭
                    Log.i(TAG, "关闭");
                }
            }
        });

        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // 开启
                } else {
                    // 关闭
                }
            }
        });

        Init_MQTT();

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnMQTTBroken(!client.isConnected());
            }
        });

    }

    /**
     * 初始化MQTT配置
     */
    public void Init_MQTT() {
        try {
            client = new MqttClient("tcp://192.168.3.217:1883",
                    "test_1234",
                    new MemoryPersistence());
            Log.i(TAG, "创建client对象");
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
     * 连接 & 断开 MQTT 连接
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