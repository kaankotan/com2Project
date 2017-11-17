package org.kaan.morsecodetranslator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        assert bluetoothAdapter != null;

        /* Checks whether Bluetooth is enabled or not. */
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(!bluetoothAdapter.isEnabled()) {
            builder.setTitle("Your Bluetooth is not enabled.");
            builder.setMessage("Please open your Bluetooth.");
            builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
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
        }
        builder.show();

    }
}
