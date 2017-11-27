package org.kaan.morsecodetranslator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by orhan on 27.11.2017.
 */

public class ServerConnection extends Thread {

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;

    private InputStream inputStream;
    private byte[] mBuffer;

    private static final String UUID_HC = "00001101-0000-1000-8000-00805F9B34FB";

    public ServerConnection(BluetoothDevice device, BluetoothAdapter adapter) {
        BluetoothSocket tempSocket = null;

        this.bluetoothAdapter = adapter;
        this.bluetoothDevice = device;

        try {
            tempSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC));
        } catch (IOException e) {
            Log.i("SOCKET_CONN", "Connection error");
        }

        bluetoothSocket = tempSocket;
    }

    @Override
    public void run() {
        mBuffer = new byte[1024];

        bluetoothAdapter.cancelDiscovery();
        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            Log.i("SOCKET_CONN", "Connect error");
            try {
                bluetoothSocket.close();
            } catch (IOException e1) {
                Log.i("SOCKET_CONN", "Close error");
            }
            return;
        }

        try {
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            Log.i("STREAM_ERROR", "Stream error");
        }

        while(true) {
            int numBytes;

            try {
                numBytes = inputStream.read(mBuffer);
                String s = new String(mBuffer, 0, numBytes);

                Log.i("INCOMING_MSG", s);

            } catch (IOException e) {
                Log.i("READ_ERR", "Read error");
            }
        }
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.i("SOCKET_CONN", "Close error");
        }
    }
}
