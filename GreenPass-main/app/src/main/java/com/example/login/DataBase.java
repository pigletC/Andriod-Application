package com.example.login;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
/**
    This is the class that use the room framework to create the table in the database called
    DataBase.
    The entire class is fully developed by Chi Zhang
 */
@Database(entities = {User.class,Password.class,Login.class},version = 1,exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public abstract UserDao getUserDao();

    public abstract PasswordDao getPassword();

    public abstract LoginDao getLogin();

    private static DataBase INSTANCE;
    public static synchronized DataBase getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),DataBase.class,"DataBase").build();
        }
        return INSTANCE;
    }
}
