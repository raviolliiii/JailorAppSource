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

public class Edit_Device extends AppCompatActivity {
    private List<Device> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        SharedPreferences myPrefs = this.getSharedPreferences("data", MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        int selectedDevice = bundle.getInt("device");

        Log.e("test", "Edit");

        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        Gson gson = new Gson();
        try {
            String savedJson = myPrefs.getString("Devices", "");
            Device tempDevices[] = gson.fromJson(savedJson, Device[].class);
            devices = new ArrayList<>(Arrays.asList(tempDevices));

            ((EditText)findViewById(R.id.deviceIP)).setText(devices.get(selectedDevice).getIP());
            ((EditText)findViewById(R.id.deviceID)).setText(devices.get(selectedDevice).getName());
            ((EditText)findViewById(R.id.deviceDescription)).setText(devices.get(selectedDevice).getDescription());
        }
        catch (Exception e){
            Log.e("test", "Unable to find devices");
        }

        //editDeviceCancel setup
        Button cancelBtn = (Button)findViewById(R.id.editDeviceCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //editDeviceConfirm setup
        Button confirmBtn = (Button)findViewById(R.id.editDeviceConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceName = ((EditText)findViewById(R.id.deviceID)).getText().toString();
                String deviceIP = ((EditText)findViewById(R.id.deviceIP)).getText().toString();
                String deviceDescription = ((EditText)findViewById(R.id.deviceDescription)).getText().toString();


                Device newDevice = new Device(deviceName, deviceIP, deviceDescription);
                devices.set(selectedDevice, newDevice);
                String newJson = gson.toJson(devices.toArray());

                prefsEditor.putString("Devices", newJson);
                prefsEditor.commit();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //editDeviceRemove setup
        Button removeBtn = (Button)findViewById(R.id.editDeviceRemove);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devices.remove(selectedDevice);
                String newJson = gson.toJson(devices.toArray());

                prefsEditor.putString("Devices", newJson);
                prefsEditor.commit();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}