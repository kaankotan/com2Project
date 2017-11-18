package org.kaan.morsecodetranslator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/// Splash screen activity for welcome.

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private Typeface mTypeFace;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Removes title bar and changes color of the status bar.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.GREEN);

        setContentView(R.layout.activity_main);

        /// Custom font Segoe UI.
        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/segoeuil.ttf");
        mTextView = (TextView) findViewById(R.id.splash_text);
        mTextView.setTypeface(mTypeFace);

        /// External library used for animations.
        YoYo.with(Techniques.Tada)
                .duration(1300)
                .repeat(5)
                .playOn(findViewById(R.id.splash_text));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This project needs location service.");
                builder.setMessage("Please grant location access, so app can detect Bluetooth Morse device.");
                builder.setPositiveButton("GOT IT !", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_FINE_LOCATION);
                    }
                });
                builder.show();
            }
            else {
                /// Redirect to main activity after 6 seconds.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                        startActivity(mainIntent);
                        MainActivity.this.finish();
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }, 6000);
            }
        }
        /* If the device SDK is lower than API 23 or Marshmallow. */
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You need to update your phone.");
            builder.setMessage("Therefore, app was engineered in that way, you can use it also.");
            builder.setPositiveButton("OK :(", null);
            builder.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(mainIntent);
                    MainActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }, 6000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "coarse location permission granted");

                    /// Redirect to main activity after 6 seconds.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                            startActivity(mainIntent);
                            MainActivity.this.finish();
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }, 6000);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
