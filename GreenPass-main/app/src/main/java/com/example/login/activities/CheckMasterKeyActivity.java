package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.AESUtils;
import com.example.login.R;
import com.example.login.manager.DBEngine;
import com.example.login.manager.DBEngineLogin;
/**
 * This is the activity that everytime user want use the password that store in database
 * it will ask user for checking maskerKey for further security
 * The entire class is fully developed by Chi Zhang
 */
public class CheckMasterKeyActivity extends AppCompatActivity {
    //declaration of element from UI
    Button button_checkPassword, button_cancel;
    TextView text_password;
    String password;
    private DBEngineLogin dbEngineLogin;
    private DBEngine dbEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Check the master key");
        setContentView(R.layout.activity_check_master_key);
        //get all the element from the UI
        button_checkPassword = findViewById(R.id.checkKey_button);
        button_cancel =findViewById(R.id.checkKey_cancel_button);
        text_password = findViewById(R.id.checkKey_password);
        password =text_password.getText().toString();

        //get database object
        dbEngine= new DBEngine(this);
        dbEngineLogin = new DBEngineLogin(this);

        //set the listener for checkPassword button
        button_checkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                for (int i = 0; i < dbEngine.getAllUser().size(); i++) {
                    Log.e("CheckMasterKey", "enter for loop id" + dbEngine.getAllUser().get(i).getId() + " " + dbEngineLogin.getLogin().getAccount_id());
                    //find matched User with input User by compare relative element Account_id
                    if (dbEngine.getAllUser().get(i).getId() == dbEngineLogin.getLogin().getAccount_id()) {
                        Log.e("CheckMasterKey", "editText " + text_password.getText().toString() + "Master key " + dbEngine.getAllUser().get(i).getPassword());
                        try {
                            // check if the masterKey is matched with input masterKey
                            if (dbEngine.getAllUser().get(i).getPassword().equals(AESUtils.encrypt(text_password.getText().toString()))) {
                                Log.e("CheckMasterKey", "editText " + text_password.getText().toString() + "Master key " + dbEngine.getAllUser().get(i).getPassword());
                                Intent intent = new Intent(CheckMasterKeyActivity.this, UpdatePassword.class);

                                //Take the data and change the View
                                intent.putExtra("passwordId", getIntent().getStringExtra("passwordId"));
                                intent.putExtra("domain", getIntent().getStringExtra("domain"));
                                intent.putExtra("account", getIntent().getStringExtra("account"));
                                intent.putExtra("password", getIntent().getStringExtra("password"));
                                flag=false;
                                startActivity(intent);
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (flag) {
                    Toast.makeText(CheckMasterKeyActivity.this, "Master key incorrect", Toast.LENGTH_LONG).show();
                }

            }
        });

        // set listening for cancel button which program will do nothing just change the view back
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckMasterKeyActivity.this, PasswordManagementActivity.class);
                intent.putExtra("passwordId",getIntent().getStringExtra("passwordId"));
                intent.putExtra("domain",getIntent().getStringExtra("domain"));
                intent.putExtra("account",getIntent().getStringExtra("account"));
                intent.putExtra("password",getIntent().getStringExtra("password"));
                startActivity(intent);
            }
        });

    }
}