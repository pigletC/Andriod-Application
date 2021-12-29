package com.example.login.bt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.login.APP;
import com.example.login.R;
import com.example.login.util.FilesUtils;

/**
 * This is the file receiver activity
 * Modified by Ming Jiang based on the original class from a GitHub repo, which is developed by
 * the author lioilwin
 * Below is the information of the original source code
 * Title: <Bluetooth>
 * Author: <lioilwin>
 * Date: <11 Jan 2019>
 * Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class BtServerActivity extends Activity implements BtBase.Listener {

    private TextView mTips;
    private EditText mInputMsg;
    private EditText mInputFile;
    private TextView mLogs;
    private BtServer mServer;
    private Button mSendMsg2;
    private Button mGenerateSecret;
    private Button mDecrypt;
    private String alicePubKeyEnc;
    private KeyAgreement bobKeyAgree;
    private String SbobPubKeyEnc;
    private PublicKey alicePubKey;
    private SecretKeySpec bobAesKey;
    private String SencodedParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alicePubKey = null;
        setContentView(R.layout.activity_btserver);
        mTips = findViewById(R.id.tv_tips);
        mInputMsg = findViewById(R.id.input_msg);
        mInputFile = findViewById(R.id.input_file);
        mLogs = findViewById(R.id.tv_log);
        mServer = new BtServer(this);
        mSendMsg2 = findViewById(R.id.sendPublicKey2);
        mSendMsg2.setClickable(false);
        mGenerateSecret = findViewById(R.id.generateSecret);
        mDecrypt = findViewById(R.id.decrypt);
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

    //Disconnect the existing connection
    //Originally developed by lioilwin
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServer.unListener();
        mServer.close();
    }

    //The file receiver send the selected file to the base class for the decryption processing
    //Fully developed by Ming Jiang
    public void decryptFile(View view) {
        if (mServer.isConnected(null)) {
            String filePath = mInputFile.getText().toString();
            if (TextUtils.isEmpty(filePath) || !new File(filePath).isFile())
                APP.toast("Cannot open the file", 0);
            else if (bobAesKey != null) {
                mServer.decryptFile(filePath, bobAesKey, SencodedParams);
            } else {
                APP.toast("Please generate shared secret first", 0);
            }
        } else
            APP.toast("No connected device", 0);
    }

    //The file receiver sends his encoded public key back to the sender
    //Originally developed by lioilwin
    //Modified by Ming Jiang
    public void sendMsg(View view) {
        if (mServer.isConnected(null)) {
            try {
                //The file receiver instantiates a DH public key from the encoded key material.
                KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc.getBytes(StandardCharsets.ISO_8859_1));
                Log.e("alicePubKeyEnc", alicePubKeyEnc);
                alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
                Log.e("alicePubKey", "Done");

                //The file receiver gets the DH parameters associated with sender's public key.
                //He must use the same parameters when he generates his own key pair.
                DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey) alicePubKey).getParams();
                Log.e("DHParameterSpec", "Done");

                // The file receiver creates his own DH key pair
                KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
                Log.e("KeyPairGenerator", "Done");

                // The file receiver creates and initializes his DH KeyAgreement object
                bobKpairGen.initialize(dhParamFromAlicePubKey);
                KeyPair bobKpair = bobKpairGen.generateKeyPair();
                bobKeyAgree = KeyAgreement.getInstance("DH");
                bobKeyAgree.init(bobKpair.getPrivate());

                // The file receiver encodes his public key
                byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();
                SbobPubKeyEnc = new String(bobPubKeyEnc, StandardCharsets.ISO_8859_1);
                mLogs.append("Server Sent Encoded Public Key!\n");
                mGenerateSecret.setEnabled(true);
                mSendMsg2.setEnabled(false);
                mSendMsg2.setText("Public Key Sent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (alicePubKeyEnc == null)
                APP.toast("Please wait client to send encoded public key", 0);
            else
                mServer.sendPubKeyEnc(SbobPubKeyEnc);
            Log.e("mServer.sendPubKeyEnc", SbobPubKeyEnc);
        } else
            APP.toast("No connected device", 0);
    }

    //The file receiver generate his shared secret and the corresponding AES key
    //Fully developed by Ming Jiang
    public void serverGenerateSecret(View view) {
        try {
            // The file receiver uses the sender's public key for the first (and only) phase
            // of his version of the DH protocol.
            bobKeyAgree.doPhase(alicePubKey, true);
            Log.e("bobKeyAgree", "Done");
            byte[] bobSharedSecret = bobKeyAgree.generateSecret();
            Log.e("bobSharedSecret", "Done");

            // The file receiver creates a SecretKey object using the shared secret
            bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");
            Log.e("bobAesKey", "Done");
            mLogs.append("Server Generated Shared Secret!\n");
            APP.toast("Generated Shared Secret!", 0);
            Log.e("Shared Secret: ", Arrays.toString(bobSharedSecret));
            mGenerateSecret.setEnabled(false);
            mGenerateSecret.setText("Shared Secret Generated");
            mDecrypt.setEnabled(true);
        } catch (Exception e) {
            Log.e("Shared Secret: ", "Cannot generate shared secret");
        }
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
                break;
            case BtBase.Listener.DISCONNECTED:
                mServer.listen();
                msg = "Disconnect, now start listening another request...";
                mTips.setText(msg);
                mSendMsg2.setEnabled(false);
                mGenerateSecret.setEnabled(false);
                mDecrypt.setEnabled(false);
                break;
            case BtBase.Listener.MSG:
                msg = String.format("\n%s", obj);
                mLogs.append(msg);
                break;

            //The file receiver has received sender's public key in encoded format.
            case BtBase.Listener.ENCPUBKEY:
                msg = String.format("%s", obj);
                alicePubKeyEnc = msg;
                mInputMsg.setText("Encoded Public Key Received.");
                Log.e("msg", msg);
                Log.e("alicePubKeyEnc", alicePubKeyEnc);
                mLogs.append("Server Received Encoded Public Key!\n");
                mSendMsg2.setEnabled(true);
                mSendMsg2.setClickable(true);
                break;

            //The file receiver has received parameter that the sender used
            // in encryption, in encoded format.
            case BtBase.Listener.PARAMETER:
                msg = String.format("%s", obj);
                SencodedParams = msg;
                Log.e("msg", msg);
                Log.e("SencodedParams", SencodedParams);
                mLogs.append("Server Received Encoded Parameter!\n");
                msg = "Public Key Sent";
                break;
        }
        APP.toast(msg, 0);
    }

    //Return the uri address of the selected file
    //Originally developed by lioilwin
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            mInputFile.setText(FilesUtils.getFilePathByUri(this, uri));
        }
    }
}