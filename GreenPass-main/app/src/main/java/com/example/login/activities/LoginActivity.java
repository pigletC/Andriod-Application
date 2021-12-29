package com.example.login.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.AESUtils;
import com.example.login.Login;
import com.example.login.R;
import com.example.login.User;
import com.example.login.manager.DBEngine;
import com.example.login.manager.DBEngineLogin;

import java.util.List;

/**
 * This is tha activity that asking user to login for using further functions
 * The entire class is fully developed by Chun Jiang and Yang Zhan
 */
public class LoginActivity extends AppCompatActivity {
    //declaration of element from UI
    public static final int REQUEST_CODE_REGISTER = 1;
    private EditText lgAccount, lgPassword;
    private Button signIn;
    private DBEngine dbEngine;
    private TextView retrieve,create;
    private DBEngineLogin dbEngineLogin;
    private String userName = "";
    private String password = "";
    private String securityAnswer = "";
    private static int primary_key_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get database object
        dbEngineLogin = new DBEngineLogin(this);
        // if login database have no data create the default data into it to avoid null pointer error
        if (dbEngineLogin.getLogin()==null) {
            dbEngineLogin.insert(new Login(1, 0, 1));
        }

        // check the database of login if is 1 is means already login or go to the main page
        if(dbEngineLogin.getLogin().getLogin()==1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

            setContentView(R.layout.activity_login);
            getSupportActionBar().setTitle("Login");

            //get all the element from the UI
            lgAccount = findViewById(R.id.lgAccount);
            lgPassword = findViewById(R.id.lgPassword);
            signIn = findViewById(R.id.signIn);
            retrieve= findViewById(R.id.retrieve);
            create=findViewById(R.id.register);
            dbEngine = new DBEngine(this);

            //set listener for signIn button
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean flag = false;
                    //get from the user input
                    String account = lgAccount.getText().toString();
                    String pass = lgPassword.getText().toString();
                    //get allUser from User database
                    List<User> users = dbEngine.getAllUser();
                    //check for incorrect input
                    if (account.isEmpty()) {
                        flag = true;
                        Toast.makeText(LoginActivity.this, "Email or phone can not be empty", Toast.LENGTH_LONG).show();
                    } else if (pass.isEmpty()) {
                        flag = true;
                        Toast.makeText(LoginActivity.this, "Password can bot be empty", Toast.LENGTH_LONG).show();
                    }
                    if (!flag) {
                        boolean exist=false;
                        for (User user : users) { //loop all the user to find the macted account
                            if (user.getAccount().equals(account)) {  // find the account
                                exist=true;
                                try {
                                    //check the password is matched or not
                                    if (!user.getPassword().equals(AESUtils.encrypt(pass))) {
                                        Toast.makeText(LoginActivity.this, "incorrect password or account", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("login", "success");
                                        primary_key_user_id = user.getId();
                                        // store data in to the database so no need to login in again if not logout
                                        dbEngineLogin.updateLogin(new Login(1, 1, primary_key_user_id));
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        if(!exist) {
                            Toast.makeText(LoginActivity.this, "Account is not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                }


            });
        // jump to RetrieveActivity Activity
            retrieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(LoginActivity.this,RetrieveActivity.class);
                    startActivity(intent);
                }
            });
            // jump to RegisterActivity Activity
            create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        }
}