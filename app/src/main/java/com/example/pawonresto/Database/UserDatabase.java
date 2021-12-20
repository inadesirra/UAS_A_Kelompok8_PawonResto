package com.example.pawonresto.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pawonresto.Dao.UserDao;
import com.example.pawonresto.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String dbname = "users";
    private static UserDatabase userDatabase;

    public static synchronized UserDatabase getUserDatabase(Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserDatabase.class, dbname)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return userDatabase;
    }
    public abstract UserDao userDao();
}
