package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;

import java.util.Random;
/**
 * This is the activity that can help user create the better password.
 * random password is much harder to figure out.
 * There are many different combination of password you can choose
 * The entire class is fully developed by Chi Zhang
 */
public class GeneratePasswordActivity extends AppCompatActivity {
    //declaration of element from UI
    private int pwd_len;
    private int signal;
    private CheckBox lowerLetter, upperLetter , specialCharacter, number;
    private TextView password,seekInfo;
    private Button generate ,copy;
    private SeekBar length;
    private int intLength=8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Generate Password");
        setContentView(R.layout.activity_generate_password_actity);
        //get all the element from the UI
        lowerLetter = findViewById(R.id.generate_lowerLetter);
        upperLetter=findViewById(R.id.generate_upperLetter);
        specialCharacter = findViewById(R.id.generate_specialCharacter);
        number = findViewById(R.id.generate_number);
        password =findViewById(R.id.generate_password);
        generate=findViewById(R.id.generate_button);
        length =findViewById(R.id.generate_length);
        seekInfo = findViewById(R.id.generate_seekbar_info);
        copy=findViewById(R.id.generate_copy);

        // set the seekbar listener and only track when the seekbar change and show different
        // text in TextView
        length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekInfo.setText("Current password length: "+progress);
                intLength=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // set the listener for generate button
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowerLetter.setChecked(true);
                pwd_len = 0;
                //if input length is smaller than 8 it will not generate password. It will ask for larger input for secure object.
                if (intLength<8) {
                    Toast.makeText(GeneratePasswordActivity.this, "Enter Larger length to be safer", Toast.LENGTH_LONG).show();
                } else {
                    pwd_len = intLength;
                    if (lowerLetter.isChecked()) {
                        Log.e("GeneratePassword", "selected");
                    } else {
                        Log.e("GeneratePassword", "Unselected");
                    }

                // get info from UI to see the user in different requirement of password different signal will be generate
                    if ((!lowerLetter.isChecked() && !upperLetter.isChecked() && !number.isChecked() && !specialCharacter.isChecked()) || pwd_len == 0) {
                        //system not work
                        password.setText("At lest on checkbox be selected");
                        //only lowerLetter is selected
                    } else if (lowerLetter.isChecked() && !upperLetter.isChecked() && !number.isChecked() && !specialCharacter.isChecked()) {
                        signal = 0;
                        //lowerLetter and specialCharacter are selected
                    } else if (lowerLetter.isChecked() && !upperLetter.isChecked() && !number.isChecked() && specialCharacter.isChecked()) {
                        signal = 1;
                        //lowerLetter and number are selected
                    } else if (lowerLetter.isChecked() && !upperLetter.isChecked() && number.isChecked() && !specialCharacter.isChecked()) {
                        signal = 2;
                        //lowerLetter,number and specialCharacter are selected
                    } else if (lowerLetter.isChecked() && !upperLetter.isChecked() && number.isChecked() && specialCharacter.isChecked()) {
                        signal = 3;
                        //lowerLetter,upperLetter are selected
                    } else if (lowerLetter.isChecked() && upperLetter.isChecked() && !number.isChecked() && !specialCharacter.isChecked()) {
                        signal = 4;
                        //lowerLetter,upperLetter,specialCharacter are selected
                    } else if (lowerLetter.isChecked() && upperLetter.isChecked() && !number.isChecked() && specialCharacter.isChecked()) {
                        signal = 5;
                        //lowerLetter,upperLetter,number are selected
                    } else if (lowerLetter.isChecked() && upperLetter.isChecked() && number.isChecked() && !specialCharacter.isChecked()) {
                        signal = 6;
                        //lowerLetter,upperLetter,number and specialCharacter are selected
                    } else if (lowerLetter.isChecked() && upperLetter.isChecked() && number.isChecked() && specialCharacter.isChecked()) {
                        signal = 7;
                    }
                    // call getPassword method and show in TextView
                    password.setText(getPassword());
                }
            }


        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", password.getText().toString());
                cm.setPrimaryClip(mClipData);
                Toast.makeText(GeneratePasswordActivity.this, "Copy success", Toast.LENGTH_LONG).show();
            }
        });


    }
    public String getPassword() {
        int i;
        int count = 0;
        StringBuilder pwd = new StringBuilder("");
        Random random = new Random();
        String lowerLetter = "abcdefghijklmnopqrstuvwxyz";
        String upperLetter=  "ABCDEFGHIJKLMNOPQISTUVWXYZ";
        String specialCharacter="!@#$%^&*()_+-=";
        String number ="123456789";
        String combination=null;
        long start = System.currentTimeMillis();
        switch (signal) {

            case 0:  //only lowerLetter is selected
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(lowerLetter.length()));
                    if (i >= 0 && i < lowerLetter.length()) {
                            pwd.append(lowerLetter.charAt(i));
                            count++;
                        }
                }
                break;
            case 1: //lowerLetter and specialCharacter are selected
                combination = lowerLetter+specialCharacter;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    if (i >= 0 && i < combination.length()) {
                            pwd.append(combination.charAt(i));
                            count++;
                    }
                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,specialCharacter.charAt((int)Math.random()*specialCharacter.length()));
                break;
            case 2: //lowerLetter and number are selected
                combination=lowerLetter+number;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;

                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,Integer.toString(random.nextInt(10)).charAt(0));
                break;
            case 3: //lowerLetter,number and specialCharacter are selected
                combination=lowerLetter+number+specialCharacter;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;

                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,Integer.toString(random.nextInt(10)).charAt(0));
                pwd.setCharAt(2,specialCharacter.charAt((int)Math.random()*specialCharacter.length()));
                break;
            case 4:  //lowerLetter,upperLetter are selected
                combination=lowerLetter+upperLetter;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;

                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,upperLetter.charAt((int)(Math.random() * upperLetter.length())));
                break;
            case 5:
                //lowerLetter,upperLetter,specialCharacter are selected
                combination=lowerLetter+upperLetter+specialCharacter;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;

                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,upperLetter.charAt((int)(Math.random() * upperLetter.length())));
                pwd.setCharAt(2,specialCharacter.charAt((int)Math.random()*specialCharacter.length()));
                break;
            case 6: //lowerLetter,upperLetter,number are selected
                combination=lowerLetter+upperLetter+number;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;
                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,upperLetter.charAt((int)(Math.random() * upperLetter.length())));
                pwd.setCharAt(2,Integer.toString(random.nextInt(10)).charAt(0));
                break;
            case 7: //lowerLetter,upperLetter,number and specialCharacter are selected
                combination=lowerLetter+upperLetter+number+specialCharacter;
                while (count < pwd_len) {
                    i = Math.abs(random.nextInt(combination.length()));
                    pwd.append(combination.charAt(i));
                    count++;

                }
                // to make sure the requirement always meet
                pwd.setCharAt(0,lowerLetter.charAt((int)(Math.random() * lowerLetter.length())));
                pwd.setCharAt(1,upperLetter.charAt((int)(Math.random() * upperLetter.length())));
                pwd.setCharAt(2,Integer.toString(random.nextInt(10)).charAt(0));
                pwd.setCharAt(3,specialCharacter.charAt((int)Math.random()*specialCharacter.length()));
                break;
            default:
        }
        long end = System.currentTimeMillis();
        String cost = Long.toString(end-start);
        Log.e("generationpassword","cosetTime: "+Long.toString(end-start) +"ms");
        return pwd.toString();

    }


}