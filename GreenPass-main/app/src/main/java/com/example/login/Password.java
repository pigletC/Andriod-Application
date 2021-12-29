package com.example.login;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
/**
 * This is the projection form the Password table from database
 * and it is all the blue print for room framework to create the table in database
 * The entire class is fully developed by Chi Zhang
 */
@Entity
public class Password {
    @PrimaryKey(autoGenerate = true)
    private int password_id;
    private String password_domain;
    private String password_account;
    private String password_password;
    private int account_id;
    @Ignore
    public Password() {
    }
    public Password(int password_id) {
        this.password_id=password_id;
    }
    @Ignore
    public Password(int password_id,String password_domain, String password_account, String password_password, int account_id) {
        this.password_id=password_id;
        this.password_domain = password_domain;
        this.password_account = password_account;
        this.password_password = password_password;
        this.account_id = account_id;
    }
    @Ignore
    public Password(String password_account, String password_password, int account_id) {
        this.password_account = password_account;
        this.password_password = password_password;
        this.account_id = account_id;
    }
    @Ignore
    public Password(String password_domain, String password_account, String password_password, int account_id) {
        this.password_domain = password_domain;
        this.password_account = password_account;
        this.password_password = password_password;
        this.account_id = account_id;
    }

    @Override
    public String toString() {
        return "Password{" +
                "password_id=" + password_id +
                ", password_domain='" + password_domain + '\'' +
                ", password_account='" + password_account + '\'' +
                ", password_password='" + password_password + '\'' +
                ", account_id=" + account_id +
                '}';
    }

    public int getPassword_id() {
        return password_id;
    }

    public void setPassword_id(int password_id) {
        this.password_id = password_id;
    }

    public String getPassword_domain() {
        return password_domain;
    }

    public void setPassword_domain(String password_domain) {
        this.password_domain = password_domain;
    }

    public String getPassword_account() {
        return password_account;
    }

    public void setPassword_account(String password_account) {
        this.password_account = password_account;
    }

    public String getPassword_password() {
        return password_password;
    }

    public void setPassword_password(String password_password) {
        this.password_password = password_password;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }


}
