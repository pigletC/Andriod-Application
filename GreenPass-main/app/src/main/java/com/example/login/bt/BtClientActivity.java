package com.example.login.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;
import com.example.login.APP;
import com.example.login.R;
import com.example.login.util.BtReceiver;
import com.example.login.util.FilesUtils;
/**
 *    This is the file sender activity
 *    Modified by Ming Jiang based on the original class from a GitHub repo, which is developed by
 *    the author lioilwin
 *    Below is the information of the original source code
 *    Title: <Bluetooth>
 *    Author: <lioilwin>
 *    Date: <11 Jan 2019>
 *    Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class BtClientActivity extends Activity implements BtBase.Listener, BtReceiver.Listener, BtDevAdapter.Listener {
    private TextView mTips;
    private EditText mInputMsg;
    private EditText mInputFile;
    private TextView mLogs;
    private BtReceiver mBtReceiver;
    private Button mSendMsg;
    private Button mGenerateSecret;
    private Button mEncrypt;
    private String SalicePubKeyEnc;
    private String bobPubKeyEnc;
    private SecretKeySpec aliceAesKey;
    private KeyAgreement aliceKeyAgree;
    private final BtDevAdapter mBtDevAdapter = new BtDevAdapter(this);
    private final BtClient mClient = new BtClient(this);

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyPairGenerator aliceKpairGen = null;
        try {
            /*
             * The file sender creates his own DH key pair with 256-bit key size
             */
            aliceKpairGen = KeyPairGenerator.getInstance("DH");
            aliceKpairGen.initialize(256);
            KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

            // The file sender creates and initializes his DH KeyAgreement object
            aliceKeyAgree = KeyAgreement.getInstance("DH");
            aliceKeyAgree.init(aliceKpair.getPrivate());

            // The file sender encodes his public key
            byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
            SalicePubKeyEnc = new String(alicePubKeyEnc, StandardCharsets.ISO_8859_1);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            Log.e("Error: ", "Failed encoding alice public key");
        }
        setContentView(R.layout.activity_btclient);
        RecyclerView rv = findViewById(R.id.rv_bt);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mBtDevAdapter);
        mTips = findViewById(R.id.tv_tips);
        mInputMsg = findViewById(R.id.input_msg);
        mInputFile = findViewById(R.id.input_file);
        mLogs = findViewById(R.id.tv_log);
        mBtReceiver = new BtReceiver(this, this);
        mSendMsg = findViewById(R.id.sendPublicKey);
        mGenerateSecret = findViewById(R.id.generateSecret);
        mEncrypt = findViewById(R.id.encrypt);
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
        Button button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1100);
            }
        });
    }

    //Return the uri address of the selected file
    //Originally developed by lioilwin
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
            mInputFile.setText(FilesUtils.getFilePathByUri(this,uri));
        }
    }

    //Disconnect the existing connection
    //Originally developed by lioilwin
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBtReceiver);
        mClient.unListener();
        mClient.close();
    }

    //Connect to the target device if it is already bonded
    //Originally developed by lioilwin
    @Override
    public void onItemClick(BluetoothDevice dev) {
        if (mClient.isConnected(dev)) {
            APP.toast("Already connected", 0);
            return;
        }
        mClient.connect(dev);
        APP.toast("Connecting...", 0);
        mTips.setText("Connecting...");
    }

    //Discover the device nearby, if detected, add the device information to the list
    //Originally developed by lioilwin
    @Override
    public void foundDev(BluetoothDevice dev) {
        mBtDevAdapter.add(dev);
    }

    //Rescan the device nearby, refresh the list
    //Originally developed by lioilwin
    public void reScan(View view) {
        mBtDevAdapter.reScan();
    }

    // The file sender sends his encoded public key to the file receiver.
    //Originally developed by lioilwin
    //Modified by Ming Jiang
    public void sendMsg(View view) {
        if (mClient.isConnected(null)) {
            mInputMsg.setText(SalicePubKeyEnc);
            mClient.sendPubKeyEnc(SalicePubKeyEnc);
            mLogs.append("Client Sent Encoded Public Key!\n");
            mSendMsg.setEnabled(false);
            mSendMsg.setText("Public Key Sent");
        } else
            APP.toast("Please connect to a bounded device first", 0);
    }

    //The file sender generate his shared secret and the corresponding AES key
    //Fully developed by Ming Jiang
    public void clientGenerateSecret(View view){
        try {
            //The file sender uses receiver's public key for the first (and only) phase
            //of his version of the DH protocol.
            //Before the sender can do so, he has to instantiate a DH public key
            //from the receiver's encoded key material.
            KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc.getBytes(StandardCharsets.ISO_8859_1));
            PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
            aliceKeyAgree.doPhase(bobPubKey, true);
            byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();

            // The file sender creates a SecretKey object using the shared secret
            aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");
            mLogs.append("Client Generated Shared Secret!\n");
            APP.toast("Generated Shared Secret!", 0);
            Log.e("Shared Secret: ", Arrays.toString(aliceSharedSecret));
            mGenerateSecret.setEnabled(false);
            mGenerateSecret.setText("Shared Secret Generated");
            mEncrypt.setEnabled(true);

        } catch (Exception e) {
            Log.e("Shared Secret: ", "Cannot generate shared secret");
        }
    }

    //The file sender send the selected file to the base class for the encryption processing
    //Originally developed by lioilwin
    //Modified by Ming Jiang
    public void sendFile(View view) {
        if (mClient.isConnected(null)) {
            String filePath = mInputFile.getText().toString();
            if (TextUtils.isEmpty(filePath) || !new File(filePath).isFile())
                APP.toast("Cannot use this path, please try another one", 0);
            else
                if(aliceAesKey != null) {
                    mClient.sendFile(filePath, aliceAesKey);
                }
                else{
                    APP.toast("Please generate shared secret first", 0);
                }
        } else
            APP.toast("No connected device", 0);
    }

    //Handle different signals received
    //Originally developed by lioilwin
    //Modified by Ming Jiang
    @Override
    public void socketNotify(int state, final Object obj) {
        if (isDestroyed())
            return;
        String msg = null;
        switch (state) {
            case BtBase.Listener.CONNECTED:
                BluetoothDevice dev = (BluetoothDevice) obj;
                msg = String.format("Connected to %s(%s)", dev.getName(), dev.getAddress());
                mTips.setText(msg);
                mSendMsg.setEnabled(true);
                break;
            case BtBase.Listener.DISCONNECTED:
                msg = "Disconnected";
                mTips.setText(msg);
                mSendMsg.setEnabled(true);
                mGenerateSecret.setEnabled(false);
                mEncrypt.setEnabled(false);
                break;
            case BtBase.Listener.MSG:
                msg = String.format("%s", obj);
                mLogs.append(msg);
                break;
            case BtBase.Listener.ENCPUBKEY:
                msg = String.format("%s", obj);
                bobPubKeyEnc = msg;
                mInputMsg.setText("Encoded Public Key Received.");
                Log.e("msg", msg);
                Log.e("bobPubKeyEnc", bobPubKeyEnc);
                mLogs.append("Client Received Encoded Public Key\n");
                mGenerateSecret.setEnabled(true);
                msg = "Public Key Sent";
                break;
        }
        APP.toast(msg, 0);
    }
}