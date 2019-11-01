package com.mcmo.z.librarydemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.mcmo.z.library.utils.TheOnlyCodeUtil;

import java.net.SocketException;
import java.util.Arrays;
import java.util.UUID;

public class TheOnlyCodeActivity extends Activity {
    TextView tv;
String[] permission = new String[]{Manifest.permission.READ_PHONE_STATE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theonlycode);
        tv = findViewById(R.id.tv);
        if(!checkPermission(permission)){
            ActivityCompat.requestPermissions(this,permission,1);
        }
    }
    private boolean checkPermission(String[] permission){
        for (String p:permission) {
            if(ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void onIdClick(View view) {
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if(!wifiManager.isWifiEnabled()){
//            wifiManager.setWifiEnabled(true);
//            wifiManager.setWifiEnabled(false);
//        }
        String uuid = UUID.randomUUID().toString();
        tv.append(uuid);
        tv.append("\n");
        String ssaid = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        tv.append(ssaid);
        tv.append("\n");
        String mac = TheOnlyCodeUtil.getMacAddress(this);
        tv.append(mac);
        tv.append("\n");
        String macAddress = TheOnlyCodeUtil.getMacAddress(this);
        tv.append(macAddress);
        tv.append("\n");

        String macLinux = TheOnlyCodeUtil.getMacAddressLinux();
        tv.append(macLinux);
        tv.append("\n");

        String[] imei = TheOnlyCodeUtil.getIMEIAndIMSI(this);
        tv.append(Arrays.toString(imei));
        tv.append("\n");
        tv.append(UUID.nameUUIDFromBytes(ssaid.getBytes()).toString());
        tv.append("\n");
        try {
            TheOnlyCodeUtil.printMacAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
