package com.example.login.util;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
/**
 *    This class is a customized BroadcastReceiver that
 *    monitors various statuses of Bluetooth broadcast
 *    Below is the information of the original source code
 *    Title: <Bluetooth>
 *    Author: <lioilwin>
 *    Date: <11 Jan 2019>
 *    Availability: <https://github.com/lioilwin/Bluetooth>
 *    Originally developed by lioilwin
 */
public class BtReceiver extends BroadcastReceiver {
    private static final String TAG = BtReceiver.class.getSimpleName();
    private final Listener mListener;
    public BtReceiver(Context cxt, Listener listener) {
        mListener = listener;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Bluetooth open/off status
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Start discover devices
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //Finish discover devices
        filter.addAction(BluetoothDevice.ACTION_FOUND); //Found devices that has not been bonded
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST); //Confirm paring
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //Device bond/unbond status
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //Connection established
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //Disconnected
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED); //BluetoothAdapter status changed
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //BluetoothHeadset status changed
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED); //BluetoothA2dp status changed
        cxt.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        Log.i(TAG, "===" + action);
        BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (dev != null)
            Log.i(TAG, "BluetoothDevice: " + dev.getName() + ", " + dev.getAddress());
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.i(TAG, "STATE: " + state);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
            case BluetoothDevice.ACTION_ACL_CONNECTED:
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                break;
            case BluetoothDevice.ACTION_FOUND:
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MAX_VALUE);
                Log.i(TAG, "EXTRA_RSSI:" + rssi);
                mListener.foundDev(dev);
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                Log.i(TAG, "BOND_STATE: " + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0));
                break;
            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                break;
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                Log.i(TAG, "CONN_STATE: " + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                break;
        }
    }

    public interface Listener {
        void foundDev(BluetoothDevice dev);
    }
}