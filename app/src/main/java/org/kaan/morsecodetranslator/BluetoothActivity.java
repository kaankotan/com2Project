package org.kaan.morsecodetranslator;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/* On Android, it is general to use "m" for private class member. Short for -> member */
public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private IntentFilter intentFilter;
    private TextView welcomeTextView, morseTopText;
    private ListView listView;
    private Typeface mTypeFace;
    private PullToRefreshView mPullToRefreshView;
    private FancyButton mConnectButton;
    private AVLoadingIndicatorView avi;
    private BluetoothDevice hcArduino;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private View secondView;
    private SeekBar seekBar;
    private TextView thresholdText;
    private TextView morseDisplayText;
    private static String receivedButtonTime = "";
    private static String displayText = "";
    private static String morseHash = "";

    private static int progressValue = 50;
    private static int threshold = 2000;

    private static final String MAC_ADDRESS_HC = "20:16:04:05:27:69";

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] readBuffer = (byte[]) msg.obj;
            String chunk = new String(readBuffer, 0, msg.arg1);
            if(chunk.contains(".")) {
                receivedButtonTime += chunk.substring(0, chunk.indexOf('.'));
                receivedButtonTime = receivedButtonTime.replace(System.getProperty("line.separator"), "");
                morseTopText.setText(receivedButtonTime);
                if(Integer.parseInt(receivedButtonTime.trim()) > threshold) {
                    morseHash += "l";
                }
                else {
                    morseHash += "s";
                }

                receivedButtonTime = "";
            }
            else {
                if(chunk.contains(",")) {
                    morseTopText.setText("Space received.");

                    if(MorseHashTable.morseDictionary.get(morseHash) != null) {
                        displayText += MorseHashTable.morseDictionary.get(morseHash);
                        displayText += " ";
                        morseDisplayText.setText(displayText);
                        morseHash = "";
                    }
                    else {
                        displayText += " ";
                    }
                }
                else {
                    receivedButtonTime += chunk;
                }
            }

        }
    };

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
        MorseHashTable.createHashTable();

        listView = (ListView) findViewById(R.id.listView);

        LayoutInflater layoutInflater = getLayoutInflater();
        secondView = layoutInflater.inflate(R.layout.morse_layout, null, false);
        secondView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out));

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mConnectButton = (FancyButton) findViewById(R.id.connect_button);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setVisibility(View.INVISIBLE);

        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/segoeuil.ttf");
        welcomeTextView = (TextView) findViewById(R.id.textView);
        welcomeTextView.setTypeface(mTypeFace);
        welcomeTextView.setVisibility(View.INVISIBLE);

        mConnectButton.setCustomTextFont("fonts/segoeuil.ttf");

        Intent intent = new Intent(this, BluetoothActivity.class);
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sun)
                        .setContentTitle("Data received!")
                        .setContentText("Bluetooth module just sent a data!");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

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

            mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
            mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(bluetoothDeviceList.size() > 0) {
                                bluetoothDeviceList.removeAll(bluetoothDeviceList);
                            }

                            bluetoothAdapter.startDiscovery();
                            mPullToRefreshView.setRefreshing(false);
                        }
                    }, 1000);
                }
            });

            mConnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ConnectButton", "Clicked.");
                    avi.setVisibility(View.VISIBLE);

                    hcArduino = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS_HC);

                    setContentView(secondView);

                    seekBar = (SeekBar) findViewById(R.id.progressBar);
                    thresholdText = (TextView) findViewById(R.id.thresholdText);
                    morseDisplayText = (TextView) findViewById(R.id.mainMorseText);
                    morseDisplayText.setTypeface(mTypeFace);
                    thresholdText.setTypeface(mTypeFace);

                    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MorseLayoutFragment fragment = MorseLayoutFragment.newInstance();
                            fragment.setParentFab(fab);
                            fragment.show(getSupportFragmentManager(), fragment.getTag());
                        }
                    });

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progressValue = progress;
                            threshold = (progress * 40);
                            thresholdText.setText("Threshold: " + threshold + " ms");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    morseTopText = (TextView) findViewById(R.id.morseTopText);
                    morseTopText.setTypeface(mTypeFace);

                    new Thread(new Runnable() {
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void run() {
                            Thread connectionThread = new ServerConnection(hcArduino, bluetoothAdapter, notificationManager, mBuilder, mHandler);
                            connectionThread.run();
                        }
                    }).start();
                }
            });
        }
        builder.show();

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
