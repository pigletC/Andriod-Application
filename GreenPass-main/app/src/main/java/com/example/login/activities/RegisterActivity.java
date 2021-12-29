package com.example.login.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.login.AESUtils;
import com.example.login.R;
import com.example.login.User;
import com.example.login.manager.DBEngine;

import java.util.List;
/**
 * This activity can help user to register an account and store in database
 * The entire class is fully developed by Chun Jiang and Yang Zhan
 */
public class RegisterActivity extends AppCompatActivity {
    //declaration of element from UI
    public static final int RESULT_CODE_REGISTER = 0;
    private Button register;
    private EditText rgAccount, rgPassword, rgConfirmPassword, rgAnswer;
    private DBEngine dbEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");


        // get all the element from the UI

        rgAccount = findViewById(R.id.rgAccount);
        rgPassword = findViewById(R.id.rgPassword);
        rgConfirmPassword = findViewById(R.id.rgConfirmPassword);
        rgAnswer = findViewById(R.id.rgAnswer);
        register = findViewById(R.id.signUp);
        // get the user table from database
        dbEngine =new DBEngine(this);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean flag =false;
                List<User> users;
                // get info from input
                String account = rgAccount.getText().toString();
                String password = rgPassword.getText().toString();
                String confirmPassword = rgConfirmPassword.getText().toString();
                String answer = rgAnswer.getText().toString();


                users=   dbEngine.getAllUser();
                //make user input data follow the rule
                if (account.isEmpty()){
                    flag=true;
                    Toast.makeText(RegisterActivity.this, "Account name can not be empty", Toast.LENGTH_LONG).show();
                }
                else if(password.isEmpty()||confirmPassword.isEmpty()){
                    flag=true;
                    Toast.makeText(RegisterActivity.this, "Password can not be empty", Toast.LENGTH_LONG).show();
                }else if(answer.isEmpty()){
                    flag=true;
                    Toast.makeText(RegisterActivity.this, "Security answer can not be empty", Toast.LENGTH_LONG).show();
                }

                else {
                    //loop to find check if the account is already exist
                    for (User user : users) {
                        if (user.getAccount().equals(account)) {
                            Toast.makeText(RegisterActivity.this, "Account name already exist", Toast.LENGTH_LONG).show();
                            flag = true;
                            break;
                        }
                    }
                }
                // if the input account name is unique
                if (!flag) {
                   // check to password input is same or not
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(RegisterActivity.this, "Password and confirm password is not same", Toast.LENGTH_LONG).show();
                    }
                    else{
                        User user = null;
                        try {
                            //encrypt
                            user = new User(account, AESUtils.encrypt(password), AESUtils.encrypt(answer));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //put data into the database
                        dbEngine.insertUser(user);
                        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
                        Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_LONG).show();
                        startActivity(it);
                    }
                }

            }
        });

    }

}