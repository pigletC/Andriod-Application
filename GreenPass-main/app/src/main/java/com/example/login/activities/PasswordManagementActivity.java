package com.example.login.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.Adapter.PasswordManagerAdapter;
import com.example.login.Password;
import com.example.login.R;
import com.example.login.manager.DBEngineLogin;
import com.example.login.manager.DBEnginePassword;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity is always inquire the password form the database whenever you open this activity or move to it
 * After you check one of the password you will move to other activity for further changes or functions
 * and click add symbol user will jump to add activity
 * The entire class is fully developed by Chi Zhang
 */
public class PasswordManagementActivity extends AppCompatActivity {

    //declaration of element from UI
    FloatingActionButton add;
    RecyclerView recyclerView;
    private DBEngineLogin dbEngineLogin;
    private DBEnginePassword dbEnginePassword;
    List<Password> passwords_database;
    private ArrayList<String> passwordId,domain, account,password;
    PasswordManagerAdapter passwordManagerAdapter;
    int account_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_management);
        getSupportActionBar().setTitle("Password Manager");

        // get all the element from the UI
        dbEnginePassword = new DBEnginePassword(this);
        dbEngineLogin = new DBEngineLogin(this);
        account_id=dbEngineLogin.getLogin().getAccount_id(); //get current login account_id
        add = findViewById(R.id.button_add);
        recyclerView=findViewById(R.id.recyclerView);

        //get the password table object from database
        passwords_database= dbEnginePassword.queryAllPassword();
        //create different list for further usage
        passwordId = new ArrayList<String>();
        domain = new ArrayList<String>();
        account = new ArrayList<String>();
        password = new ArrayList<String>();


        Log.e("PasswordManager","show database size"+passwords_database.size());
        for (int i = 0; i < passwords_database.size(); i++) {
            // select data from data which match the current login account_id
            if (passwords_database.get(i).getAccount_id()==account_id) {
                passwordId.add(Integer.toString(passwords_database.get(i).getPassword_id()));
                domain.add(passwords_database.get(i).getPassword_domain());
                account.add(passwords_database.get(i).getPassword_account());
                password.add(passwords_database.get(i).getPassword_password());
            }

        }
        // set listener to add function if it clicked it will go to add activity
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordManagementActivity.this,AddPasswordActivity.class);
                startActivity(intent);
            }
        });
        //get all the necessary data to the Adapter for recycler View.
        passwordManagerAdapter = new PasswordManagerAdapter(PasswordManagementActivity.this,passwordId,domain,account,password);
        recyclerView.setAdapter(passwordManagerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PasswordManagementActivity.this));

    }
}
