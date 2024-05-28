package com.example.jailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Add_Device extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        SharedPreferences myPrefs = this.getSharedPreferences("data", MODE_PRIVATE);

        Log.e("test", "Add");
        //addDeviceCancel setup
        Button cancelBtn = (Button)findViewById(R.id.addDeviceCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //addDeviceConfirm setup
        Button confirmBtn = (Button)findViewById(R.id.addDeviceConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceName = ((EditText)findViewById(R.id.deviceID)).getText().toString();
                String deviceIP = ((EditText)findViewById(R.id.deviceIP)).getText().toString();
                String deviceDescription = ((EditText)findViewById(R.id.deviceDescription)).getText().toString();

                List<Device> devices = new ArrayList<>();
                SharedPreferences.Editor prefsEditor = myPrefs.edit();
                Gson gson = new Gson();
                try {
                    String savedJson = myPrefs.getString("Devices", "");
                    Device tempDevices[] = gson.fromJson(savedJson, Device[].class);
                    devices = new ArrayList<>(Arrays.asList(tempDevices));
                }
                catch (Exception e){
                    Log.e("test", "Unable to find devices");
                }

                Device newDevice = new Device(deviceName, deviceIP, deviceDescription);
                devices.add(newDevice);
                String newJson = gson.toJson(devices.toArray());

                prefsEditor.putString("Devices", newJson);

                prefsEditor.commit();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}