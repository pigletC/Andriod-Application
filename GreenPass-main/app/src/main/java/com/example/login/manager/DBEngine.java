package com.example.login.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.login.DataBase;
import com.example.login.User;
import com.example.login.UserDao;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This entire class is developed by Chi Zhang
 */
public class DBEngine {
    // Data Access Object which can access the User database
    private UserDao userDao;

    public DBEngine(Context context){
        DataBase dataBase= DataBase.getInstance(context);
        userDao = dataBase.getUserDao();
    }

    // insert User into database
    public void insertUser(User...users){
        new insertAsyncTack(userDao).execute(users);
    }
    // update the User into database
    public void updateUser(User...users){
        new updateAsyncTack(userDao).execute(users);
    }
    // get all the User from database
    public List<User> getAllUser(){
        try {
            return new getAllAsyncTack(userDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    //delete User from the database
    public void deleteUser(User...users){
        new deleteAsyncTack(userDao).execute(users);
    }

    // asynchronous thread class for updating
    static class updateAsyncTack extends AsyncTask<User,Void,Void> {
        private  UserDao dao;
        public updateAsyncTack(UserDao userDao) {
            dao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.updateStudents(users);
            return null;
        }
    }
    // asynchronous thread class for deleting
    static class deleteAsyncTack extends AsyncTask<User,Void,Void> {
        private  UserDao dao;
        public deleteAsyncTack(UserDao userDao) {
            dao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.deleteUser();
            return null;
        }
    }

    // asynchronous thread class for inserting
    static class insertAsyncTack extends AsyncTask<User,Void,Void> {
        private  UserDao dao;
        public insertAsyncTack(UserDao userDao) {
            dao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.insertUser(users);
            return null;
        }
    }
    // asynchronous thread class for getting all users
    static class getAllAsyncTack extends AsyncTask<Void,List<User>,List<User>> {
        private UserDao dao;

        public getAllAsyncTack(UserDao userDao) {
            dao = userDao;
        }


        @Override
        protected List<User> doInBackground(Void...Void) {
            List<User> allUser =dao.getAllUser();

            for ( User user : allUser) {
                Log.e("test","doInBackground"+user.toString());
            }
            return allUser;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
        }
    }


}
