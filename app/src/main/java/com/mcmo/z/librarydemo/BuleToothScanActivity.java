package com.mcmo.z.librarydemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by weizhang210142 on 2016/4/29.
 */
public class BuleToothScanActivity extends Activity {
    private static final String TAG = "BuleToothScanActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 101;
    private BroadcastReceiver mBlueToothStateReceiver;
    private Button btn_scan,btn_discovery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluescan);
        btn_scan= (Button) findViewById(R.id.btn_blueScan);
        btn_discovery= (Button) findViewById(R.id.btn_disable);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            init();
        }
        mBlueToothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                    int currState=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF);
                    int prevState=intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE,BluetoothAdapter.STATE_OFF);
                    //the state include  STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF, and STATE_OFF
                    //11,12,13,10
                    Log.e(TAG, "onReceive current state"+currState+" previous "+prevState);
                    if(currState==BluetoothAdapter.STATE_ON){
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                                // If there are paired devices
                        if (pairedDevices.size() > 0) {
                            // Loop through paired devices
                            for (BluetoothDevice device : pairedDevices) {
                                // Add the name and address to an array adapter to show in a ListView
                                Log.e(TAG, "onReceive paired: "+device.getName()+" "+device.getAddress());
                            }
                        }
                    }
                }else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                    btn_scan.setText("S:"+new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis())));
                }else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                    btn_scan.setText("E"+new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis())));
                }else if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    Log.e(TAG, "onReceive found: "+device.getName()+" "+device.getAddress());
                }else if(intent.getAction().equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                    int scan_mode=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                    int prev_scan_mode=intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE,0);
//                    SCAN_MODE_CONNECTABLE, SCAN_MODE_CONNECTABLE_DISCOVERABLE SCAN_MODE_NONE
//                    21                         23                                 20
                    Log.e(TAG, "onReceive scan mode : "+scan_mode+" prev: "+prev_scan_mode);
                }

            }
        };
        if(mBluetoothAdapter!=null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBlueToothStateReceiver, new IntentFilter(intentFilter));
        }
    }

    private void init() {
        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });
        btn_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//默认120s，最大3600s
                startActivity(discoverableIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(this,"ok",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"no ok",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBlueToothStateReceiver != null)
            unregisterReceiver(mBlueToothStateReceiver);
    }
}
