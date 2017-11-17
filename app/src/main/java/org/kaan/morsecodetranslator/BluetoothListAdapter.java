package org.kaan.morsecodetranslator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by orhan on 18.11.2017.
 */

public class BluetoothListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BluetoothDevice> bluetoothDeviceList;
    private Typeface mTypeFace;
    private Context mContext;

    public BluetoothListAdapter(Activity activity, List<BluetoothDevice> list) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bluetoothDeviceList = list;
        mContext = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return bluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        mTypeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/segoeuil.ttf");

        rowView = mInflater.inflate(R.layout.row_layout, null);
        TextView nameText = (TextView) rowView.findViewById(R.id.nameText);
        nameText.setTypeface(mTypeFace);
        TextView addressText = (TextView) rowView.findViewById(R.id.hardwareAddressText);
        addressText.setTypeface(mTypeFace);

        BluetoothDevice bluetoothDevice = bluetoothDeviceList.get(position);
        nameText.setText(bluetoothDevice.getName());
        addressText.setText(bluetoothDevice.getAddress());

        return rowView;
    }
}
