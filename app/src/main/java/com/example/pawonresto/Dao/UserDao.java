package com.example.pawonresto.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pawonresto.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user WHERE email=(:email) AND password=(:password)")
    User login(String email, String password);

    @Query("UPDATE user SET nama_lengkap=(:name) WHERE email=(:email) AND password=(:password)")
    void updateName(String name, String email, String password);
}
