package com.example.login;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.login.activities.MainActivity;
import com.example.login.bt.BtClientActivity;
import com.example.login.bt.BtServerActivity;
/**
 * This is the activity that user can choose to send or receive files
 * by clicking relate button.
 * The entire class is fully developed by Ming Jiang, based on the original class
 * from a GitHub repo developed by the author lioilwin
 * Below is the information of the original source code
 * Title: <Bluetooth>
 * Author: <lioilwin>
 * Date: <11 Jan 2019>
 * Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class FileSharingActivity extends Activity {

    private Button mClient, mServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try to grant permissions dynamically if the device has Android version higher than 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
        setContentView(R.layout.activity_file_sharing);
        mClient = findViewById(R.id.btn_bt_client);
        mServer = findViewById(R.id.btn_bt_server);

        // Check if the bluetooth is enabled
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            APP.toast("This device does not support Bluetooth!", 0);
            finish();
            return;
        } else {
            if (!adapter.isEnabled()) {
                adapter.enable();
            }
        }

        // Try to grant permissions dynamically if the device has Android version higher than 6
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_COARSE_LOCATION
        };
        for (String str : permissions) {
            if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 111);
                break;
            }
        }

        mClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileSharingActivity.this, BtClientActivity.class);
                startActivity(intent);
            }
        });
        mServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileSharingActivity.this, BtServerActivity.class);
                startActivity(intent);
            }
        });

    }
}
