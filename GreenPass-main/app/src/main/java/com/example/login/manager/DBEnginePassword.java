package com.example.login.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.login.DataBase;
import com.example.login.Password;
import com.example.login.PasswordDao;
import com.example.login.User;
import com.example.login.UserDao;

import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 * This entire class is to manage the passwords and it is fully developed by Chi Zhang
 */
public class DBEnginePassword {

    private PasswordDao passwordDao;

    public DBEnginePassword(Context context){
        DataBase dataBase= DataBase.getInstance(context);
        passwordDao = dataBase.getPassword();
    }
    // insert password in password table into database
    public void insertPassword(Password...passwords){
        new insertAsyncTack(passwordDao).execute(passwords);
    }
    // update password in password table into database
    public void updatePassword(Password...passwords){
        new updateAsyncTack(passwordDao).execute(passwords);
    }
    // delete password in password table into database
    public void deletePassword(Password...passwords){
        new deleteAsyncTack(passwordDao).execute(passwords);
    }
    //get all password from password table from database
    public List<Password> queryAllPassword() {
        try {
            return new getAllAsyncTack(passwordDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    // asynchronous thread class for inserting
    static class insertAsyncTack extends AsyncTask<Password,Void,Void>{
        private PasswordDao dao;
        public insertAsyncTack(PasswordDao passwordDao){
                dao=passwordDao;
        }

        @Override
        protected Void doInBackground(Password... passwords) {
            dao.insertPassword(passwords);
            return null;
        }
    }
    // asynchronous thread class for updating
    static class updateAsyncTack extends AsyncTask<Password,Void,Void>{
        private PasswordDao dao;
        public updateAsyncTack(PasswordDao passwordDao){
            dao=passwordDao;
        }
        @Override
        protected Void doInBackground(Password... passwords) {
            dao.updatePassword(passwords);
            return null;
        }
    }
    // asynchronous thread class for deleting
    static class deleteAsyncTack extends AsyncTask<Password,Void,Void>{
        private PasswordDao dao;
        public deleteAsyncTack(PasswordDao passwordDao){
            dao=passwordDao;
        }
        @Override
        protected Void doInBackground(Password... passwords) {
            dao.deletePassword(passwords);
            return null;
        }
    }
    // asynchronous thread class for get all password
    static class getAllAsyncTack extends AsyncTask<Void,List<Password>,List<Password>> {
        private PasswordDao dao;

        public getAllAsyncTack(PasswordDao PasswordDao) {
            dao = PasswordDao;
        }

        @Override
        protected List<Password> doInBackground(Void...Void) {
            List<Password> allPassword =dao.getAllPassword();

            for ( Password password : allPassword) {
                Log.e("test","doInBackground"+password.toString());
            }
            return allPassword;
        }

        @Override
        protected void onPostExecute(List<Password> passwords) {
            super.onPostExecute(passwords);
        }
    }

}
