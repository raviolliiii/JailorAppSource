package com.example.jailor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private static final int SERVERPORT = 2005;
    public String deviceID = "", deviceIP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences myPrefs = this.getSharedPreferences("data", MODE_PRIVATE);

        List<Device> devices = new ArrayList<>();
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        Gson gson = new Gson();

        try{
            String savedJson = myPrefs.getString("Devices", "");
            Device tempDevices[] = gson.fromJson(savedJson, Device[].class);
            devices = Arrays.asList(tempDevices);
            Log.d("test", devices.get(0).getDescription());
        }
        catch (Exception e){
            Log.e("test", "Unable to find devices");
        }

        Spinner selectDevice = (Spinner)findViewById(R.id.selectDevice);
        ArrayAdapter<Device> deviceAdapter = new ArrayAdapter<Device>(this, android.R.layout.simple_spinner_dropdown_item, devices);
        selectDevice.setAdapter(deviceAdapter);

        selectDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deviceIP = ((Device)selectDevice.getSelectedItem()).getIP();
                deviceID = ((Device)selectDevice.getSelectedItem()).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deviceIP = "";
                deviceID = "";
            }
        });

        //addDeviceButton setup
        Button addBtn = (Button)findViewById(R.id.addDeviceButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Add_Device.class));
            }
        });

        //Action choice setup
        Spinner selectAction = findViewById(R.id.selectAction);
        ArrayAdapter<JB_Instruction> actionAdapter = new ArrayAdapter<JB_Instruction>(this, android.R.layout.simple_spinner_dropdown_item, JB_Instruction.values());
        selectAction.setAdapter(actionAdapter);
        selectAction.setSelection(JB_Instruction.DEV_MODE.getValue());

        FragmentManager fragmentManager = getSupportFragmentManager();

        selectAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JB_Instruction action = JB_Instruction.valueOf(selectAction.getSelectedItem().toString());
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (action) {
                    case JB_FUN:
                        fragmentTransaction.replace(R.id.fragmentSpace, new FUN());
                        break;
                    case JB_DEACTIVE:
                        fragmentTransaction.replace(R.id.fragmentSpace, new DEACTIVE());
                        break;
                    case JB_REMOVE:
                        fragmentTransaction.replace(R.id.fragmentSpace, new REMOVE());
                        break;
                    case JB_VOLUME:
                        fragmentTransaction.replace(R.id.fragmentSpace, new VOLUME());
                        break;
                    case JB_MUTE:
                        fragmentTransaction.replace(R.id.fragmentSpace, new MUTE());
                        break;
                    case JB_SZAMBO:
                        fragmentTransaction.replace(R.id.fragmentSpace, new SZAMBO());
                        break;
                    case JB_SETWALL:
                        fragmentTransaction.replace(R.id.fragmentSpace, new SETWALL());
                        break;
                    case JB_SAVEWALL:
                        fragmentTransaction.replace(R.id.fragmentSpace, new SAVEWALL());
                        break;
                    case JB_LOADWALL:
                        fragmentTransaction.replace(R.id.fragmentSpace, new LOADWALL());
                        break;
                    case JB_CREATELINKS:
                        fragmentTransaction.replace(R.id.fragmentSpace, new CREATELINKS());
                        break;
                    case JB_REMOVELINKS:
                        fragmentTransaction.replace(R.id.fragmentSpace, new REMOVELINKS());
                        break;
                    case JB_OPENWEB:
                        fragmentTransaction.replace(R.id.fragmentSpace, new OPENWEB());
                        break;
                    case JB_CDEJECT:
                        fragmentTransaction.replace(R.id.fragmentSpace, new CDEJECT());
                        break;
                    case JB_POPUPA:
                        fragmentTransaction.replace(R.id.fragmentSpace, new POPUPA());
                        break;
                    case JB_POPUPW:
                        fragmentTransaction.replace(R.id.fragmentSpace, new POPUPW());
                        break;
                    case JB_EXEC:
                        fragmentTransaction.replace(R.id.fragmentSpace, new EXEC());
                        break;
                    case DEV_MODE:
                        fragmentTransaction.replace(R.id.fragmentSpace, new DEV_MODE());
                        break;
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    public void onClick(View view) {
        //check if device is chosen
        if(deviceID == "" || deviceIP == "") return;
        //Get selected action
        JB_Instruction action = JB_Instruction.valueOf(((Spinner)findViewById(R.id.selectAction)).getSelectedItem().toString());

        ByteBuffer buffer = ByteBuffer.allocate(540);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 32; i++) {
            if(i < deviceID.length()) buffer.put((byte) deviceID.charAt(i));
            else buffer.put((byte)0);
        }

        if(action != JB_Instruction.DEV_MODE){
            buffer.putInt(action.getValue());
        }
        //for(int i = 0; i < buffer.array().length; i++) Log.d("test", String.valueOf(buffer.array()[i]));
        switch (action){
            case JB_FUN:
            case JB_REMOVE:
            case JB_DEACTIVE:
            case JB_VOLUME:
            case JB_MUTE:
            case JB_SAVEWALL:
            case JB_LOADWALL:
            case JB_REMOVELINKS:
            case JB_CDEJECT:
                break;
            case JB_SZAMBO:
                char[] szamboName = ((EditText)findViewById(R.id.szamboName)).getText().toString().toCharArray();
                for(int i = 0; i < szamboName.length; i++) {
                    buffer.put((byte) szamboName[i]);
                }
                break;
            case JB_SETWALL:
                char[] setwallName = ((EditText)findViewById(R.id.setwallName)).getText().toString().toCharArray();
                for(int i = 0; i < setwallName.length; i++) {
                    buffer.put((byte) setwallName[i]);
                }
                break;
            case JB_CREATELINKS:
                char[] createlinksAmount = ((EditText)findViewById(R.id.createlinksAmount)).getText().toString().toCharArray();
                for(int i = 0; i < createlinksAmount.length; i++) {
                    buffer.put((byte) createlinksAmount[i]);
                }
                break;
            case JB_OPENWEB:
                byte[] openwebLink = ((EditText)findViewById(R.id.openwebLink)).getText().toString().getBytes(StandardCharsets.UTF_8);
                for(int i = 0; i < openwebLink.length; i++) {
                    buffer.put(openwebLink[i]);
                }
                if(((Switch)findViewById(R.id.openwebSwitch)).isChecked()){
                    buffer.put((byte) 3);
                    buffer.put("1".getBytes());
                }
                break;
            case JB_POPUPW:
                byte[] contentW = ((EditText)findViewById(R.id.popupwContent)).getText().toString().getBytes(StandardCharsets.UTF_8);
                for(int i = 0; i < contentW.length; i++) {
                    buffer.put(contentW[i]);
                }

                buffer.put((byte) 3); //separate title from content

                byte[] titleW = ((EditText)findViewById(R.id.popupwTitle)).getText().toString().getBytes(StandardCharsets.UTF_8);
                for(int i = 0; i < titleW.length; i++) {
                    buffer.put(titleW[i]);
                }
                break;
            case JB_POPUPA:
                char[] contentA = ((EditText)findViewById(R.id.popupaContent)).getText().toString().toCharArray();
                for(int i = 0; i < contentA.length; i++) {
                    buffer.put((byte) contentA[i]);
                }

                buffer.put((byte) 3); //separate title from content

                char[] titleA = ((EditText)findViewById(R.id.popupaTitle)).getText().toString().toCharArray();
                for(int i = 0; i < titleA.length; i++) {
                    buffer.put((byte) titleA[i]);
                }
                break;
            case JB_EXEC:
                char[] execCommand = ((EditText)findViewById(R.id.execCommand)).getText().toString().toCharArray();
                for(int i = 0; i < execCommand.length; i++) {
                    buffer.put((byte) execCommand[i]);
                }
                break;
            case DEV_MODE:
                buffer.putInt(Integer.parseInt(((EditText)findViewById(R.id.dev_modeActionID)).getText().toString()));
                char[] dev_modeArgs = ((EditText)findViewById(R.id.dev_modeArgs)).getText().toString().toCharArray();
                for(int i = 0; i < dev_modeArgs.length; i++) {
                    buffer.put((byte) dev_modeArgs[i]);
                }
                break;
        }

        sendBroadcastWithIP(buffer.array());

        Toast.makeText(this, "Command sent", Toast.LENGTH_SHORT).show();
    }
    public void sendBroadcast(byte[] sendData) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), SERVERPORT);
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e("Error", "IOException: " + e.getMessage());
        }
    }

    public void sendBroadcastWithIP(byte[] sendData) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(deviceIP), SERVERPORT);
            socket.send(sendPacket);
            System.out.println(getClass().getName() + "Broadcast packet sent to: " + InetAddress.getByName(deviceIP));
        } catch (IOException e) {
            Log.e("Error", "IOException: " + e.getMessage());
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    public void ETXSign(View view){
        EditText text = findViewById(R.id.dev_modeArgs);
        char ETX = (char)(byte)3;
        text.setText(text.getText() + String.valueOf(ETX));
        text.setSelection(text.getText().length());
    }
}