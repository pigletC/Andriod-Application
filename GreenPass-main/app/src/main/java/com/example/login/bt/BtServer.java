package com.example.login.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.example.login.util.FilesUtils;
/**
 *    The server class used in a socket connections with the client
 *    Originally developed by lioilwin
 *    Below is the information of the original source code
 *    Title: <Bluetooth>
 *    Author: <lioilwin>
 *    Date: <11 Jan 2019>
 *    Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class BtServer extends BtBase {
    private static final String TAG = BtServer.class.getSimpleName();
    private BluetoothServerSocket mSSocket;

    BtServer(Listener listener) {
        super(listener);
        listen();
    }

    public void listen() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            mSSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(TAG, SPP_UUID);
            FilesUtils.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BluetoothSocket socket = mSSocket.accept();
                        mSSocket.close();
                        loopRead(socket);
                    } catch (Throwable e) {
                        close();
                    }
                }
            });
        } catch (Throwable e) {
            close();
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            mSSocket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}