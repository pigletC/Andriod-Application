package com.example.login;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
/**
 * This is the projection form the User table from database
 * and it is all the blue print for room framework to create the table in database
 * The entire class is fully developed by Yang Zhan and Chun Jiang
 */
@Entity
public class User {


    @PrimaryKey(autoGenerate = true)
    private int id;

    public User(){

    }
    @Ignore
    public User(String account,String password, String answer) {

        this.account = account;
        this.password = password;
        this.answer = answer;
    }
    @Ignore
    public User(int id,String account,String password, String answer) {
        this.id=id;
        this.account = account;
        this.password = password;
        this.answer = answer;
    }


    private String account;
    private String password;
    private String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
