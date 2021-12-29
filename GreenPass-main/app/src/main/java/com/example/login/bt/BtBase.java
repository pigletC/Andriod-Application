package com.example.login.bt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.example.login.APP;
import com.example.login.util.FilesUtils;
import com.example.login.util.FilesUtils;
/**
 *    The base class of client and server, which is used to manage socket connections
 *    Modified by Ming Jiang based on the original class from a GitHub repo, which is developed by
 *    the author lioilwin
 *    Below is the information of the original source code
 *    Title: <Bluetooth>
 *    Author: <lioilwin>
 *    Date: <11 Jan 2019>
 *    Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class BtBase extends Activity {
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/";
    public static final int FLAG_MSG = 0;
    private static final int FLAG_FILE = 1;
    private static final int FLAG_ENCPUBKEY = 4;
    private static final int FLAG_PARAMETER = 3;

    private BluetoothSocket mSocket;
    private DataOutputStream mOut;
    private Listener mListener;
    private boolean isRead;
    private boolean isSending;

    BtBase(Listener listener) {
        mListener = listener;
    }

    //Loop to read the data from another device(if there is no data, block and wait)
    //Modified by Ming Jiang
    //Originally developed by lioilwin
    //socket - the connected socket between the sender and the receiver
    void loopRead(BluetoothSocket socket) {
        mSocket = socket;
        try {
            if (!mSocket.isConnected())
                mSocket.connect();
            notifyUI(Listener.CONNECTED, mSocket.getRemoteDevice());
            mOut = new DataOutputStream(mSocket.getOutputStream());
            DataInputStream in = new DataInputStream(mSocket.getInputStream());
            isRead = true;
            while (isRead) {
                switch (in.readInt()) {
                    case FLAG_MSG:
                        String msg = in.readUTF();
                        notifyUI(Listener.MSG, msg);
                        break;
                    case FLAG_ENCPUBKEY:
                        try {
                            String publicKeyEnc = in.readUTF();
                            notifyUI(Listener.ENCPUBKEY, publicKeyEnc);
                        } catch (Exception e) {
                            Log.e("ERROR", "Failed Receiving PUBKEY");
                            close();
                        }
                        break;
                    case FLAG_PARAMETER:
                        try {
                            String parameter = in.readUTF();
                            notifyUI(Listener.PARAMETER, parameter);
                        } catch (Exception e) {
                            Log.e("ERROR", "Failed Receiving PARAMETER");
                            close();
                        }
                        break;

                        //Reconstruct a encrypted file from the input data stream
                    case FLAG_FILE:
                        long start = System.currentTimeMillis();
                        try {
                            FilesUtils.mkdirs(FILE_PATH);
                            String fileName = in.readUTF();
                            long fileLen = in.readLong();
                            long len = 0;
                            int r;
                            byte[] b = new byte[4 * 1024];
                            FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);
                            notifyUI(Listener.MSG, "Receiving file(" + fileName + "), please wait...");
                            while ((r = in.read(b)) != -1) {
                                out.write(b, 0, r);
                                len += r;
                                if (len >= fileLen)
                                    break;
                            }
                            mOut.flush();
                            notifyUI(Listener.MSG, "File received(location: " + FILE_PATH + ")");
                        } catch (Exception e) {
                            Log.e("ERROR", "Failed Receiving File");
                            close();
                        }
                        long end = System.currentTimeMillis();
                        Log.e("Send time", end - start + " ms");
                        break;
                }
            }
        } catch (Throwable e) {
            close();
        }
    }

    //The file receiver decrypt the received ciphertext using AES in CBC format
    //Fully developed by Ming Jiang
    //filePath - the location of the ciphertext
    //bobAesKey - the AES key generated from the receiver
    //SencodedParams - the parameter that the sender used to encrypt file
    public void decryptFile(final String filePath, final SecretKeySpec bobAesKey, final String SencodedParams) {
        FilesUtils.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    int r;
                    byte[] b = new byte[1024];
                    //Decrypting
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();

                    //Convert the received data stream that contains ciphertext into byte array
                    while ((r = in.read(b)) != -1) {
                        bao.write(b, 0, r);
                    }
                    byte[] ciphertext = bao.toByteArray();
                    Log.e("ciphertext", Arrays.toString(ciphertext));

                    // Instantiate AlgorithmParameters object from parameter encoding
                    // obtained from the sender
                    AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
                    aesParams.init(SencodedParams.getBytes(StandardCharsets.ISO_8859_1));
                    Log.e("encodedParams", SencodedParams);
                    Cipher bobCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    bobCipher.init(Cipher.DECRYPT_MODE, bobAesKey, aesParams);

                    // Decrypt the byte array of ciphertext back to the byte array of cleartext
                    byte[] recovered = bobCipher.doFinal(ciphertext);
                    Log.e("recovered", Arrays.toString(recovered));
                    ByteArrayInputStream bin = new ByteArrayInputStream(recovered);
                    FileOutputStream out = new FileOutputStream(filePath);
                    notifyUI(Listener.MSG, "Decrypting: (" + filePath + ") Please wait...");

                    //Convert the byte array of cleartext to a file stream
                    while ((r = bin.read(b)) != -1) {
                        out.write(b, 0, r);
                    }
                    notifyUI(Listener.MSG, "Finished, location:(" + FILE_PATH + ")");
                } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IOException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
                    Log.e("ERROR", "Failed Decrypting File");
                }
            }
        });
    }

    //The file sender encrypt the given file into ciphertext using AES in CBC mode
    // and send the ciphertext to the receiver
    //Modified from the original sendFile() function
    //Partial developed by Ming Jiang
    //filePath - the location of the ciphertext
    //AesKey - the AES key generated from the sender
    public void sendFile(final String filePath, final SecretKeySpec AesKey) {
        long start = System.currentTimeMillis();
        if (checkSend()) return;
        isSending = true;
        FilesUtils.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(filePath);
                } catch (SecurityException e) {
                    Log.e("ERROR", "Access denied");
                } catch (FileNotFoundException e) {
                    Log.e("ERROR", "File DNE");
                }
                try {
                    File file = new File(filePath);
                    int r;
                    byte[] b = new byte[1024];
                    //Encrypting
                    Cipher aliceCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    aliceCipher.init(Cipher.ENCRYPT_MODE, AesKey);

                    // Retrieve the parameter that was used, and transfer it to the receiver in
                    // encoded format
                    byte[] encodedParams = aliceCipher.getParameters().getEncoded();
                    SystemClock.sleep(2000);
                    sendParameter(new String(encodedParams, StandardCharsets.ISO_8859_1));
                    mOut.writeInt(FLAG_FILE);
                    mOut.writeUTF(file.getName());
                    mOut.writeLong(file.length());
                    BufferedInputStream bis = new BufferedInputStream(in);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();

                    //Convert the file stream into byte array
                    while ((r = bis.read()) != -1) {
                        bao.write(r);
                    }

                    //Encrypt the file by encrypting its byte array content into ciphertext
                    byte[] cleartext = bao.toByteArray();
                    Log.e("cleartext", Arrays.toString(cleartext));
                    byte[] ciphertext = aliceCipher.doFinal(cleartext);
                    Log.e("ciphertext", Arrays.toString(ciphertext));
                    ByteArrayInputStream bin = new ByteArrayInputStream(ciphertext);
                    notifyUI(Listener.MSG, "Sending file(" + filePath + "), please wait...\n");

                    //Convert the byte array of the ciphertext back into an data stream
                    while ((r = bin.read(b)) != -1) {
                        mOut.write(b, 0, r);
                    }
                    mOut.flush();
                    notifyUI(Listener.MSG, "File sent\n");
                } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | IOException | IllegalBlockSizeException e) {
                    Log.e("ERROR", "Failed Sending File");
                    close();
                }
            }
        });
        isSending = false;
        long end = System.currentTimeMillis();
        Log.e("Send time", end - start + " ms");
    }

    //Either the file sender or the receiver send their encrypted public key
    //Modified from the original sendMsg() function
    //Fully developed by Ming Jiang
    //msg - the encrypted public key in String format
    public void sendPubKeyEnc(String msg) {
        if (checkSend()) return;
        isSending = true;
        try {
            mOut.writeInt(FLAG_ENCPUBKEY);
            mOut.writeUTF(msg);
            mOut.flush();
        } catch (Throwable e) {
            close();
        }
        isSending = false;
    }

    //The file sender send the parameter used in file encryption
    //Modified from the original sendMsg() function
    //Fully developed by Ming Jiang
    //msg - the parameter that the sender used in file encryption, in String format
    public void sendParameter(String msg) {
        try {
            mOut.writeInt(FLAG_PARAMETER);
            mOut.writeUTF(msg);
            mOut.flush();
            notifyUI(Listener.PARAMETER, "Sent Parameter: " + msg);
        } catch (IOException e) {
            Log.e("Failed Sending Param", "Error");
            close();
        }
    }

    //Release listening references to avoid memory leaks
    //Fully developed by lioilwin
    public void unListener() {
        mListener = null;
    }

    //Close socket connection
    //Fully developed by lioilwin
    public void close() {
        try {
            isRead = false;
            mSocket.close();
            notifyUI(Listener.DISCONNECTED, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //Check if the current device is connected to the target device
    //Fully developed by lioilwin
    //dev - the target device to be checked
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (mSocket != null && mSocket.isConnected());
        if (dev == null)
            return connected;
        return connected && mSocket.getRemoteDevice().equals(dev);
    }

    //Notify the user if there is an existing transition
    //Fully developed by lioilwin
    private boolean checkSend() {
        if (isSending) {
            APP.toast("Please wait for current transaction...", 0);
            return true;
        }
        return false;
    }

    //Notify the user interface
    //Fully developed by lioilwin
    //state - the header of the floating log
    //obj - the content of the floating log
    private void notifyUI(final int state, final Object obj) {
        APP.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mListener != null)
                        mListener.socketNotify(state, obj);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public interface Listener {
        int DISCONNECTED = 0;
        int CONNECTED = 1;
        int MSG = 2;
        int PARAMETER = 3;
        int ENCPUBKEY = 4;
        void socketNotify(int state, Object obj);
    }
}
