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
 * The entire class is fully developed by Yang Zhan and Chun Jiang
 */
@Dao
public interface UserDao {
    // insert the user in to database

    @Insert
    void insertUser(User... users);

    @Update
    void updateStudents(User...Users);

    @Query("select * from user")
    List<User> getAllUser();

    @Delete
    void deleteUser(User...users);
}
