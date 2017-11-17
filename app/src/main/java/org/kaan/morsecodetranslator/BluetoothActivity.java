package org.kaan.morsecodetranslator;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/* On Android, it is general to use "m" for private class member. Short for -> member */
public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private IntentFilter intentFilter;
    private TextView welcomeTextView;
    private ListView listView;
    private Typeface mTypeFace;

    private final List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();

    private BluetoothListAdapter adapter;

    /* Main event. May be hard to understand first. Read the manual. */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                /* Bluetooth device discovery started. */
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                welcomeTextView.setText("Bluetooth scan finished.");
                welcomeTextView.setVisibility(View.VISIBLE);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                /* Bluetooth device found. */
                welcomeTextView.setText("Bluetooth device found!");
                welcomeTextView.setVisibility(View.VISIBLE);

                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("DeviceFound", bluetoothDevice.getName());
                bluetoothDeviceList.add(bluetoothDevice);
                adapter = new BluetoothListAdapter(BluetoothActivity.this, bluetoothDeviceList);
                listView.setAdapter(adapter);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        listView = (ListView) findViewById(R.id.listView);

        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/segoeuil.ttf");
        welcomeTextView = (TextView) findViewById(R.id.textView);
        welcomeTextView.setTypeface(mTypeFace);
        welcomeTextView.setVisibility(View.INVISIBLE);

        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, intentFilter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /* Gives error on emulator.
         * Emulators do not have built-in Bluetooth core adapters.
         * @TODO: Add some more informative message on simulator. */
        assert bluetoothAdapter != null;

        /* Checks whether Bluetooth is enabled or not. */
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(!bluetoothAdapter.isEnabled()) {
            builder.setTitle("Your Bluetooth is not enabled.");
            builder.setMessage("Please open your Bluetooth.");
            builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
        }
        else {
            builder.setTitle("Your Bluetooth is enabled.");
            builder.setMessage("You are ready to go!");
            builder.setPositiveButton("GOT IT!", null);

            bluetoothAdapter.startDiscovery();
        }
        builder.show();

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
