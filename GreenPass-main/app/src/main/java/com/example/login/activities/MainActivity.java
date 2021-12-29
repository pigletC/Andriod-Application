package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.login.FileSharingActivity;
import com.example.login.Login;
import com.example.login.R;
import com.example.login.manager.DBEngineLogin;
/**
 * This is the activity to guide user to do what they want.
 * This activity do not have a real function except log out
 * The entire class is fully developed by Chun Jiang and Yang Zhan
 */
public class MainActivity extends AppCompatActivity {
    //declaration of element from UI
    private Button exit,passwordManager,fileSharing,generatePassword;
    private DBEngineLogin dbEngineLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get object from loin table from database
        dbEngineLogin = new DBEngineLogin(this);


        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Main");
        //get all the element from the UI
        passwordManager=findViewById(R.id.passwordManage);
        fileSharing = findViewById(R.id.fileSharing);
        generatePassword=findViewById(R.id.generatePassword);
        int account_id= dbEngineLogin.getLogin().getAccount_id();

        exit = findViewById(R.id.exit);

        //set a listener for logout the account
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change the database so that user should login again
                dbEngineLogin.updateLogin(new Login(1,0,account_id));

                finish();
            }
        });
        //set the listener to passwordManager button
        passwordManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to next view
                Intent intent = new Intent(MainActivity.this,PasswordManagementActivity.class);
                startActivity(intent);
            }
        });
        //set the listener to fileSharing button
        fileSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileSharingActivity.class);
                startActivity(intent);
            }
        });
        //set the listener to generatePassword button
        generatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GeneratePasswordActivity.class);
                startActivity(intent);
            }
        });

    }

}