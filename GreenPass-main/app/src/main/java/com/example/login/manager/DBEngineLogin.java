package com.example.login.manager;



import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.login.DataBase;
import com.example.login.Login;
import com.example.login.LoginDao;

import java.util.concurrent.ExecutionException;
/**
 * This entire class is to manage the login information and it is fully developed by Chi Zhang
 */
public class DBEngineLogin {
    private LoginDao loginDao;

    public DBEngineLogin(Context context){
        DataBase dataBase= DataBase.getInstance(context);
        loginDao = dataBase.getLogin();
    }
    // insert info to login table into database
    public void insert(Login...logins){
        new insertAsyncTack(loginDao).execute(logins);
    }
    // update info to login table into database
    public void updateLogin(Login...logins){
        new updateAsyncTack(loginDao).execute(logins);
    }
    //get the login object
    public Login getLogin(){
        try {
            return new getAllAsyncTack(loginDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    // asynchronous thread class for updating get all login object
    static class getAllAsyncTack extends AsyncTask<Void,Login,Login> {
        private LoginDao dao;

        public getAllAsyncTack(LoginDao loginDao) {
            dao = loginDao;
        }


        @Override
        protected Login doInBackground(Void...Void) {
           Login login =dao.getLogin();


                Log.e("test","doInBackground"+login.toString());

            return login;
        }

        @Override
        protected void onPostExecute(Login login) {
            super.onPostExecute(login);
        }
    }
    // asynchronous thread class for updating get all login object
    static class updateAsyncTack extends AsyncTask<Login,Void,Void> {
        private  LoginDao dao;
        public updateAsyncTack(LoginDao loginDao) {
            dao =loginDao ;
        }

        @Override
        protected Void doInBackground(Login... logins) {
            dao.updateLogin(logins);
            return null;
        }
    }
    // asynchronous thread class for inserting the info
    static class insertAsyncTack extends AsyncTask<Login,Void,Void> {
        private  LoginDao dao;
        public insertAsyncTack(LoginDao loginDao) {
            dao = loginDao;
        }

        @Override
        protected Void doInBackground(Login...logins) {
            dao.insertLogin(logins);
            return null;
        }
    }
}
