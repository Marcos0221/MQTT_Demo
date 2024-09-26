package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    private String TAG = "Config";

    private String Server_addr;
    private String Server_port;
    private String topic;

    private String config_path;

    public Config(Context context) {
        try {
            config_path = context.getFilesDir().getAbsolutePath() + "/application.conf";
            Path path = Paths.get(config_path);
            if (!Files.exists(path)) {
                return;
            }
            byte[] bytes = Files.readAllBytes(path);
            String config = new String(bytes);
            JSONObject jsonObject = new JSONObject(config);
            Server_addr = jsonObject.get("addr").toString();
            Server_port = jsonObject.getString("port").toString();
            topic = jsonObject.getString("topic").toString();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getServer_addr() {
        return Server_addr;
    }

    public String getServer_port() {
        return Server_port;
    }

    public String getTopic() {
        return topic;
    }

    public boolean exists() {
        return Files.exists(Paths.get(config_path));
    }
}
