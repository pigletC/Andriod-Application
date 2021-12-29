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
 * This activity can help user to retrieve an account and change password in database by giving
 * matched account name and secure question
 * The entire class is fully developed by Chun Jiang and Yang Zhan
 */
public class RetrieveActivity extends AppCompatActivity {
    //declaration of element from UI
    public static final int RESULT_CODE_RETRIEVE = 0;
    private Button change;
    private EditText reAccount, rePassword, reConfirmPassword, reAnswer;
    private DBEngine dbEngine;
    private String userName = "";
    private String securityAnswer = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        getSupportActionBar().setTitle("Retrieve");
        //get User data from database
        dbEngine = new DBEngine(this);
        // get all the element from the UI
        reAccount = findViewById(R.id.reAccount);
        rePassword = findViewById(R.id.rePassword);
        reConfirmPassword = findViewById(R.id.reConfirmPassword);
        reAnswer = findViewById(R.id.reAnswer);
        change = findViewById(R.id.change);

        //set listener for change button
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                // get info form UI
                String account = reAccount.getText().toString();
                String password = rePassword.getText().toString();
                String confirmPassword = reConfirmPassword.getText().toString();
                String answer = reAnswer.getText().toString();
                // get all User info from database
                List<User> allUser = dbEngine.getAllUser();
                if (account.isEmpty()){
                    flag=true;
                    Toast.makeText(RetrieveActivity.this, "Account name can not be empty", Toast.LENGTH_LONG).show();
                }
                else if(password.isEmpty()||confirmPassword.isEmpty()){
                    flag=true;
                    Toast.makeText(RetrieveActivity.this, "Password can not be empty", Toast.LENGTH_LONG).show();
                }else if(answer.isEmpty()){
                    flag=true;
                    Toast.makeText(RetrieveActivity.this, "Security answer can not be empty", Toast.LENGTH_LONG).show();
                }

                if(!flag){
                for (User user : allUser) { //loop for all user
                    if (user.getAccount().equals(account)) { //find the matched user
                        try {
                            //check the secure answer is correct or not if it correct go next
                            if (!user.getAnswer().equals(AESUtils.encrypt(answer))) {
                                Toast.makeText(RetrieveActivity.this, "Secure answer is not match", Toast.LENGTH_LONG).show();
                            }else if(!password.equals(confirmPassword)){
                                Toast.makeText(RetrieveActivity.this, "Password and confirm password is not match", Toast.LENGTH_LONG).show();
                            }
                            else{
                                //replace the info to the User table in database
                                dbEngine.updateUser(new User(user.getId(),user.getAccount(),AESUtils.encrypt(password),user.getAnswer()));

                                Log.e("RE", "success");
                                Intent intent = new Intent(RetrieveActivity.this, LoginActivity.class);
                                Toast.makeText(RetrieveActivity.this, "Success", Toast.LENGTH_LONG).show();
                                startActivity(intent);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    Toast.makeText(RetrieveActivity.this, "Account is not exist", Toast.LENGTH_LONG).show();
                }

                }
            }

        });
    }
}
