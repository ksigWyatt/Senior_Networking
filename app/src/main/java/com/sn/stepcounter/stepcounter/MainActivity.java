package com.sn.stepcounter.stepcounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.processbutton.iml.ActionProcessButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/* YOYO Animation Libary used : https://github.com/daimajia/AndroidViewAnimations*/
public class MainActivity extends AppCompatActivity {

    final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    ;
    private BluetoothDevice mBluetoothDevice;
    final String TAG = "Arduino 101";
    // the Mac Address and the UUID for the Arduino 101
    final String MacAddress = "98:4F:EE:0F:97:23";
    final String UUIDStr = "19b10012-e8f2-537e-4f6c-d104768a1214";
    final String serviceUUID = "19b10010-e8f2-537e-4f6c-d104768a1214";
    final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    final UUID mUUID = UUID.fromString(UUIDStr);
    final UUID mServiceUUID = UUID.fromString(serviceUUID);
    final UUID mButtonUUID = UUID.fromString("19b10011-e8f2-537e-4f6c-d104768a1214");

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mGattCallback;
    private ActionProcessButton btnConnect;
    private TextView stepsCount, date, miles;
    private long steps = 0;
    private FirebaseDatabase database;
    FloatingActionMenu mFloatingActionMenu;
    FloatingActionButton soundBtn;
    private FirebaseUser currentUser;
    private MediaPlayer mediaPlayer;
    private AlertDialog.Builder builder;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundBtn = findViewById(R.id.sound_btn);
        mediaPlayer = MediaPlayer.create(this, R.raw.smw_coin);


        builder = new AlertDialog.Builder(this);

        // create alert dialog
        mAlertDialog = builder.setTitle("Reminder")
                .setMessage("Your exercise is not finished, Are you sure to log out ? \n( " +
                        "The current steps data won't be uploaded to the cloud after log out)")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //user log out
                        Toast.makeText(getApplication(), "Logged Out", Toast.LENGTH_SHORT).
                                show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(login.class);
                        finish();
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).create();

        // get firebase database instance
        database = database.getInstance();
        // the current firebase logged - in user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // init connect btn
        btnConnect = findViewById(R.id.connect_btn);
        btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
        // the step count textview
        stepsCount = findViewById(R.id.steps_count);
        stepsCount.setText("0");
        // the data textview on top
        date = findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String datestr = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        date.setText(dayOfTheWeek + "  " + datestr);
        // the distance textview at bottom left
        miles = findViewById(R.id.mile);
        miles.setText("0 \t miles");
        mFloatingActionMenu = findViewById(R.id.menu);
        resetWeekly();


// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //bluetooth is connected so discover services
                    Log.d(TAG, "Connected");
                    mBluetoothGatt.discoverServices();

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //Bluetooth is disconnected
                    Log.d(TAG, "Disconnected");

                    unpairDevice(mBluetoothDevice);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            btnConnect.setProgress(0);
                            btnConnect.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this,
                                    "StepCounter Disconnected.", Toast.LENGTH_SHORT).show();
                            // play animation on the button
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
                    Log.d(TAG, "Discovered");
                    //enable notify on Arduino 101
                    BluetoothGattCharacteristic characteristic = gatt.getService(mServiceUUID).
                            getCharacteristic(mUUID);
                    gatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.
                            getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
                    descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                            new byte[]{0x00, 0x00});
                    gatt.writeDescriptor(descriptor);
                    // the current method is running asynchronously in the background,
                    // so call runOnUiThread to update the text
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            btnConnect.setProgress(0);
                            YoYo.with(Techniques.FlipInX)
                                    .duration(500)
                                    .repeat(0)
                                    .playOn(btnConnect);
                            btnConnect.setText("Start");
                            Toast.makeText(MainActivity.this, "StepCounter Connected",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });


                }


            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                Log.d(TAG, "Read");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, characteristic.getUuid().toString());

                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {

                long value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,
                        0) & 0x00000000ffffffffL;


                Log.d(TAG, "Steps Received " + value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        if (steps != value && value != 0) {
                            YoYo.with(Techniques.FlipInX)
                                    .duration(500)
                                    .repeat(0)
                                    .playOn(stepsCount);
                            stepsCount.setText(String.valueOf(value));
                            if (mediaPlayer != null) {
                                // play mario coin sound
                                mediaPlayer.start();
                            }
                            steps = value;
                            double distance = (double) value / 2112;
                            Log.d(TAG, "Distance " + distance);
                            NumberFormat formatter = new DecimalFormat("#0.00");
                            // only the first 5 digit
                            miles.setText(formatter.format(distance) + "\t miles");
                        }
                    }
                });

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.d(TAG, "Connected6");

            }
        };
        /******************click listener on the connect btn*//////////////

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check the Bluetooth is enabled
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //onActivityResult will be called for the call back

                } else if (btnConnect.getText().equals("Complete Exercise")) {
                    // turn stepcounter off
                    btnConnect.setProgress(0);
                    btnConnect.setText("Start");
                    updateDatabase(stepsCount.getText().toString());
                    String data = "1";
                    byte[] strBytes = data.getBytes();
                    BluetoothGattService mSVC = mBluetoothGatt.getService(mServiceUUID);
                    BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(mButtonUUID);
                    mCH.setValue(strBytes);
                    mBluetoothGatt.writeCharacteristic(mCH);
                    stepsCount.setText("0");
                    miles.setText(String.valueOf(0) + "\t miles");


                } else if (btnConnect.getText().equals("Start")) {
                    btnConnect.setProgress(100);
                    btnConnect.setText("Complete Exercise");
                    Log.d(TAG, "START Pressed");
                    // turn stepcounter on
                    String data = "0";
                    byte[] strBytes = data.getBytes();
                    BluetoothGattService mSVC = mBluetoothGatt.getService(mServiceUUID);
                    BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(mButtonUUID);
                    mCH.setValue(strBytes);
                    mBluetoothGatt.writeCharacteristic(mCH);

                } else {
                    //check step counter is already paired or not
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            btnConnect.setProgress(1);
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            if (deviceHardwareAddress.equals(MacAddress)) {
                                Log.d(TAG, deviceName + " " + deviceHardwareAddress);
                                mBluetoothDevice = device;
                                break;


                            }
                        }


                    }
                    // discover unparied bluetooth device
                    if (mBluetoothDevice == null) {
                        Intent i = new Intent(getApplicationContext(), AvailiableDevice.class);
                        startActivity(i);
                        btnConnect.setProgress(0);
                    } else {
                        connectGatt(mBluetoothDevice.getAddress());
                    }


                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Access Granted",
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Bluetooth Access Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean connectGatt(final String address) {
        if (mBluetoothAdapter == null || address == null) {

            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    btnConnect.setProgress(-1);
                    Toast.makeText(getBaseContext(), "BluetoothAdapter not initialized or " +
                            "unspecified address.", Toast.LENGTH_SHORT).show();


                }
            });
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui

                    btnConnect.setProgress(-1);
                    Toast.makeText(getBaseContext(), "Device not found.  Unable to connect.",
                            Toast.LENGTH_SHORT).show();


                }
            });


            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        return mBluetoothGatt.connect();
    }

    // unpair the step counter
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        //app will run in the background , the back button on the bottom nav bar pressed
        moveTaskToBack(true);

    }

 // app closed
    @Override
    protected void onDestroy() {
        super.onDestroy();
//
        if (mBluetoothDevice != null) {
            unpairDevice(mBluetoothDevice);
        }
        if (mediaPlayer != null) {
            //release mediaPlayer
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


    public void updateDatabase(final String steps) {
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM-dd-yyyy");
        // the day of the week abbreviated
        SimpleDateFormat simpleDateformat2 = new SimpleDateFormat("E");

        String date = simpleDateformat.format(now);
        String day = simpleDateformat2.format(now);

        Log.d(TAG, day + " " + date);
        DatabaseReference mref = database.getReference("UsersStepData/" + currentUser.getUid()
                + "/WeeklyStepData");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(day).child("date") == null || dataSnapshot.child(day).
                        child("date").getValue(String.class).equals(date)) {
                    float oldStepsCount = dataSnapshot.child(day).child("stepsCount").
                            getValue(float.class);
                    float newStepsCount = oldStepsCount + Float.valueOf(steps);
                    mref.child(day).child("stepsCount").setValue(newStepsCount);
                    mref.child(day).child("date").setValue(date);
                } else {
                    for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                        if (childSnap.getKey().equals(day)) {
                            float newStepsCount = Float.valueOf(steps);
                            mref.child(day).child("stepsCount").setValue(newStepsCount);
                            mref.child(day).child("date").setValue(date);
                        } else {
                            mref.child(childSnap.getKey()).child("stepsCount").setValue((float) 0);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void resetWeekly() {
        DatabaseReference mref = database.getReference("UsersStepData/" + currentUser.getUid() +
                "/WeeklyStepData");
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat simpleDateformat2 = new SimpleDateFormat("E");
        // the day of the week abbreviated
        String date = simpleDateformat.format(now);
        String day = simpleDateformat2.format(now);
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(day).child("date").getValue(String.class) != null) {
                    if (!dataSnapshot.child(day).child("date").getValue(String.class).equals(date)) {
                        /* if the date in database for today(ex.Tue,Mon...)
                        is different ,reset the weeklydata*/
                        for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                            mref.child(childSnap.getKey()).child("stepsCount").setValue((float) 0);
                            mref.child(childSnap.getKey()).child("date").setValue(null);
                        }
                    }
                }
                mref.child(day).child("date").setValue(date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void resultClicked(View view) {
        mFloatingActionMenu.close(true);
        startActivity(Result.class);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void startActivity(Class<?> clazz) {
        startActivity(new Intent(MainActivity.this, clazz));
    }

    public void logoutClicked(View view) {
        if (btnConnect.getText().equals("Complete Exercise")) {
            mAlertDialog.show();
        } else {
            Toast.makeText(getApplication(), "Logged Out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(login.class);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

    }

    // change sound button image
    public void soundClicked(View view) {
        if (soundBtn.getTag().equals("on")) {
            soundBtn.setImageResource(R.drawable.ic_sound_offf);
            soundBtn.setTag("off");
            mediaPlayer.release();
            mediaPlayer = null;
        } else {
            soundBtn.setImageResource(R.drawable.ic_sound_on);
            soundBtn.setTag("on");
            mediaPlayer = MediaPlayer.create(this, R.raw.smw_coin);
        }


    }
}




