package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.login.AESUtils;
import com.example.login.Password;
import com.example.login.R;
import com.example.login.User;
import com.example.login.manager.DBEngine;
import com.example.login.manager.DBEngineLogin;
import com.example.login.manager.DBEnginePassword;

import java.util.List;

/**
 * This is the activity that user can add password into password table from database
 * by clicking relate button.
 * All password store in database is all encrypted
 * The entire class is fully developed by Chi Zhang
 */
public class AddPasswordActivity extends AppCompatActivity {
    //declaration of element from UI
    private TextView password_domain,password_account,password_password;
    private Button add;
    private DBEnginePassword dbEnginePassword;
    private DBEngine dbEngine;
    private DBEngineLogin dbEngineLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        //set the relate page title
        getSupportActionBar().setTitle("Add password");

        // get all the element from the UI
        dbEnginePassword = new DBEnginePassword(this);
        dbEngine = new DBEngine(this);
        password_domain = findViewById(R.id.input_domain);
        password_account = findViewById(R.id.input_account);
        password_password = findViewById(R.id.input_password);
        add = findViewById(R.id.button_add);

        //get the Database
        dbEngineLogin = new DBEngineLogin(this);

        //set listener for add button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an password object
                Password password = null;
                try {
                    //put correct info in to password oject
                    password = new Password(password_domain.getText().toString(),
                            password_account.getText().toString(),

                            // encrypt the password
                            AESUtils.encrypt(password_password.getText().toString()),dbEngineLogin.getLogin().getAccount_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // insert password object in to the database
                dbEnginePassword.insertPassword(password);

                // change the view to PasswordManagementActivity view
                Intent intent = new Intent(AddPasswordActivity.this,PasswordManagementActivity.class);
                startActivity(intent);
                List<Password> passwords=dbEnginePassword.queryAllPassword();
                for (Password password1 : passwords) {
                    Log.e("AddPasswordActivity",password1.toString());
                }
            }
        });
    }


}