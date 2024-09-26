package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class ConfigurationActivity extends AppCompatActivity {

    private String TAG = "ConfigurationActivity";

    private Button save_button;
    private MaterialAutoCompleteTextView address, port, topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        address = findViewById(R.id.addr);
        port = findViewById(R.id.port);
        topic = findViewById(R.id.topic);
        save_button = findViewById(R.id.save_button);

        save_button.setOnClickListener(view -> {
            JSONObject object = new JSONObject();
            try {
                object.put("addr", address.getText().toString());
                object.put("port", port.getText().toString());
                object.put("topic", topic.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                File file = new File(getFilesDir().getAbsolutePath() + "/application.conf");//文件路径
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(object.toString());
                fileWriter.flush();//刷新数据，不刷新写入不进去
                fileWriter.close();//关闭流
                if (!file.exists()) {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}