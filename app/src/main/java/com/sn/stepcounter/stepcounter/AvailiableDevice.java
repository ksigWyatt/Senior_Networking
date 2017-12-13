package com.sn.stepcounter.stepcounter;

import android.Manifest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;


public class AvailiableDevice extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    private ListView mLvDevices;
    private HashSet<String> mDeviceList= new HashSet<String>();
    final String MacAddress = "98:4F:EE:0F:97:23";
    ArrayAdapter adapter;
    ProgressBar mProgressBar;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availiable_device);

        mLvDevices = (ListView) findViewById(R.id.lvDevices);
        mProgressBar = findViewById(R.id.mProgressBar);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBtReceiver, filter);

        // Getting the Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter != null) {
            int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            mBtAdapter.startDiscovery();

            Toast.makeText(this, "Starting discovery...", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "Bluetooth disabled or not available.",
                    Toast.LENGTH_SHORT).show();
        }
        mLvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Object entry =  adapterView.getItemAtPosition(i);
               String macAddress = entry.toString().substring(0,17);
               if(macAddress.equals(MacAddress)) {
                   final BluetoothDevice device = mBtAdapter
                           .getRemoteDevice(macAddress);
                   pairDevice(device);
                   Toast.makeText(getApplicationContext(), "Paired and Ready to Connect",
                           Toast.LENGTH_SHORT).show();
                   finish();
               }else{
                   Toast.makeText(getApplicationContext(),"Selected Device is not StepCounter",
                           Toast.LENGTH_SHORT).show();
               }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(mBtReceiver);
    }

    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    //discovery starts, we can show progress dialog or perform other tasks
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //discovery finishes, dismis progress dialog
                  mProgressBar.setVisibility(View.GONE);
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDeviceList.add(device.getAddress() + ", " + device.getName());
                    // get mac address

                   ArrayList<String> list = new ArrayList<String>(mDeviceList);
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, list);
                    mLvDevices.setAdapter(adapter);

                }








        }



    };

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
