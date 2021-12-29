package com.example.login.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.login.AESUtils;
import com.example.login.Password;
import com.example.login.R;
import com.example.login.manager.DBEngineLogin;
import com.example.login.manager.DBEnginePassword;

import java.util.List;

/**
 * This activity update or delete the password into the database
 * user can also copy and paste in this activity
 * The entire class is fully developed by Chi Zhang
 */
public class UpdatePassword extends AppCompatActivity {
    //declaration of element from UI
    EditText update_domain,update_account,update_password;
    Button update_button ,delete_button, autofill_button;
    String passwordId,domain, account,password;
    DBEnginePassword dbEnginePassword;
    DBEngineLogin dbEngineLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Update Password");
        setContentView(R.layout.activity_update_password);
        // get object from login table and password table from database
        dbEngineLogin = new DBEngineLogin(this);
        dbEnginePassword= new DBEnginePassword(this);

        // get all the element from the UI
        update_domain = findViewById(R.id.update_domain);
        update_account = findViewById(R.id.update_account);
        update_password = findViewById(R.id.update_password);
        update_button = findViewById(R.id.button_update);
        delete_button=findViewById(R.id.button_delete);
        autofill_button=findViewById(R.id.button_autofill);

        //get info from the Intent object from previous activities
        domain = getIntent().getStringExtra("domain");
        account=getIntent().getStringExtra("account");
        password=getIntent().getStringExtra("password");
        passwordId=getIntent().getStringExtra("passwordId");
        //set TextView to show data
        update_domain.setText(domain);
        update_account.setText(account);
        update_password.setText(password);


        //set the listener to the update button
             update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                // get info form the UI
                domain = update_domain.getText().toString();
                account = update_account.getText().toString();
                password = update_password.getText().toString();
                String encryptedPassword = null;
                try {
                    //encrypt the password
                    encryptedPassword = AESUtils.encrypt(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (domain.isEmpty()||account.isEmpty()||password.isEmpty()){
                    flag=false;
                    Toast.makeText(UpdatePassword.this, "Can not be empty", Toast.LENGTH_LONG).show();
                }
                if (flag){ // if everything correct.

                    //put all info in the Password object
                Password pass_password = new Password(Integer.parseInt(passwordId), domain, account, encryptedPassword, dbEngineLogin.getLogin().getAccount_id());
                dbEnginePassword.updatePassword(pass_password);
                //set up alert bar
                new AlertDialog.Builder(UpdatePassword.this)
                        .setTitle("Confirm")
                        .setMessage("Do you want update this data")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            //if check Yes it will updatePassword into password table in database
                            public void onClick(DialogInterface dialog, int which) {
                                dbEnginePassword.updatePassword(pass_password);
                                Intent intent = new Intent(UpdatePassword.this, PasswordManagementActivity.class);
                                startActivity(intent);
                            }
                        })

                        .setNegativeButton("Cancel", null)

                        .show();
                      }

            }
        });
             //set listener to the delete button
             delete_button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //create the object and only store the id or the password
                     Password pass_password = new Password(Integer.parseInt(passwordId));

                     //set up alert bar
                     new  AlertDialog.Builder(UpdatePassword.this)
                             .setTitle("Confirm" )
                             .setMessage("Do you want delete this data" )
                             .setPositiveButton("Yes" , new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     //delete the password base on the password ID
                                     dbEnginePassword.deletePassword(pass_password);
                                     Intent intent = new Intent(UpdatePassword.this,PasswordManagementActivity.class);
                                     startActivity(intent);
                                 }
                             })
                             .setNegativeButton("Cancel" , null)
                             .show();


                 }
             });
             // setup listener for the autofill function
             autofill_button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     //get the intent object and put info needed into it
                     Intent intent = new Intent(UpdatePassword.this, PasswordAutofillActivity.class);
                     intent.putExtra("url",domain);
                     intent.putExtra("account",account);
                     intent.putExtra("password",password);
                     startActivity(intent);
                 }
             });

    }


}