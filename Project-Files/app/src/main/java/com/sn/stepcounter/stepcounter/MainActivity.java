package com.sn.stepcounter.stepcounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.processbutton.iml.ActionProcessButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;


public class MainActivity extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1;
    private  BluetoothAdapter mBluetoothAdapter;;
    final String TAG = "Name123";
    final String MacAddress = "98:4F:EE:0F:96:90";
    final String UUIDStr ="19b10012-e8f2-537e-4f6c-d104768a1214";
    final String serviceUUID ="19b10010-e8f2-537e-4f6c-d104768a1214";
     final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    final UUID mUUID = UUID.fromString(UUIDStr);
    final UUID mServiceUUID = UUID.fromString(serviceUUID);
    private BluetoothGatt mBluetoothGatt;
    private  BluetoothGattCallback mGattCallback;
    private ActionProcessButton btnConnect;
    private TextView stepsCount,date,miles;
    private long steps =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);








        btnConnect =  findViewById(R.id.connect_btn);
        btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);

        stepsCount = findViewById(R.id.steps_count);
        stepsCount.setText("0");

        date = findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String datestr = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        date.setText(dayOfTheWeek+ ", "+datestr);

        miles = findViewById(R.id.mile);
        miles.setText("0 \t miles");



// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //bluetooth is connected so discover services
                    Log.d(TAG, "Connected1");
                    mBluetoothGatt.discoverServices();

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //Bluetooth is disconnected
                    Log.d(TAG, "Connected2");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            btnConnect.setProgress(0);
                            btnConnect.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this,"StepCounter Disconnected.",Toast.LENGTH_SHORT).show();
                            YoYo.with(Techniques.FadeIn)
                                    .duration(1000)
                                    .repeat(0)
                                    .playOn(btnConnect);
                            stepsCount.setText("0");
                            miles.setText(String.valueOf(0) + "\t miles");

                        }
                    });


                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // services are discoverd
                    Log.d(TAG, "Connected3");
                    BluetoothGattCharacteristic characteristic= gatt.getService(mServiceUUID).getCharacteristic(mUUID);
                    gatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                    descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[] { 0x00, 0x00 });
                    gatt.writeDescriptor(descriptor);

                    Log.d(TAG, "Connected6");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            btnConnect.setProgress(100);
                            Toast.makeText(MainActivity.this,"StepCounter Connected.",Toast.LENGTH_SHORT).show();
                            YoYo.with(Techniques.FadeOut)
                                    .duration(1500)
                                    .repeat(0)
                                    .playOn(btnConnect);

                        }
                    });



                }


            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                Log.d(TAG, "Connected5");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, characteristic.getUuid().toString());

                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {

                long value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0) & 0x00000000ffffffffL;


                Log.d(TAG, "Connected4 "+value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        if(steps!=value && value!=0 ){
                            YoYo.with(Techniques.FlipInX)
                                    .duration(500)
                                    .repeat(0)
                                    .playOn(stepsCount);
                            stepsCount.setText(String.valueOf(value));
                            steps=value;
                            double distance = (double)value/5000;
                            Log.d(TAG, "Connected4 "+distance);
                            miles.setText(String.valueOf(distance) + "\t miles");

                        }




                    }
                });

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG, "Connected6");

            }
        };

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    btnConnect.setProgress(1);
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            if (deviceHardwareAddress.equals(MacAddress)) {
                                Log.d(TAG, deviceName + " " + deviceHardwareAddress);
                                Toast.makeText(getBaseContext(),"StepCounter Found.",Toast.LENGTH_SHORT).show();
                                connectGatt(MacAddress);
                                break;


                            }
                        }


                    }


                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Bluetooth Access Granted",Toast.LENGTH_SHORT).show();
                //start scanning



            }else{

                Toast.makeText(this, "Bluetooth Access Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean connectGatt(final String address) {
        if (mBluetoothAdapter == null || address == null) {

            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            btnConnect.setProgress(-1);
            Toast.makeText(this,"BluetoothAdapter not initialized or unspecified address.",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            btnConnect.setProgress(-1);
            Toast.makeText(this,"Device not found.  Unable to connect.",Toast.LENGTH_SHORT).show();
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        return mBluetoothGatt.connect();
    }


    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}




