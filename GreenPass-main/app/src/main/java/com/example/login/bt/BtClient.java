package com.example.login.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.login.util.FilesUtils;
/**
 *    The client class used in a socket connections with the server
 *    Originally developed by lioilwin
 *    Below is the information of the original source code
 *    Title: <Bluetooth>
 *    Author: <lioilwin>
 *    Date: <11 Jan 2019>
 *    Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class BtClient extends BtBase {
    BtClient(Listener listener) {
        super(listener);
    }

    public void connect(BluetoothDevice dev) {
        close();
        try {
            final BluetoothSocket socket = dev.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            FilesUtils.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    loopRead(socket);
                }
            });
        } catch (Throwable e) {
            close();
        }
    }
}