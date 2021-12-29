package com.example.login;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * This is the projection form the Login table from database
 * and it is all the blue print for room framework to create the table in database
 * The entire class is fully developed by Yang Zhan and Chun Jiang
 */
@Entity
public class Login {


    @PrimaryKey(autoGenerate = true)
    private int id;
    private int login;
    private int account_id;
    public Login() {

    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", login=" + login +
                ", account_id=" + account_id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
    @Ignore
    public Login(int id, int login, int account_id) {
        this.id = id;
        this.login = login;
        this.account_id = account_id;
    }
}