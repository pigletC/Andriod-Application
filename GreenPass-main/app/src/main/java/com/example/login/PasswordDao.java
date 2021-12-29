package com.example.login;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
/**
 * by using room framework it will automatic to have the SQL to change the
 * database
 * The entire class is fully developed by Chi Zhang
 */
@Dao
public interface PasswordDao {
    // insert the user in to database

    @Insert
    void insertPassword(Password...passwords);

    @Update
    void updatePassword(Password...passwords);

    @Query("select * from password")
    List<Password> getAllPassword();

    @Delete
    void deletePassword(Password...passwords);
}
