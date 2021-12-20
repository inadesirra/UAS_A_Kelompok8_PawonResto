package com.example.pawonresto.Database;

import android.content.Context;

import androidx.room.Room;

public class UserDatabaseClient {
    private Context context;
    private static UserDatabaseClient databaseClient;

    private UserDatabase database;

    private UserDatabaseClient(Context context){
        this.context = context;
        database = Room.databaseBuilder(context, UserDatabase.class, "user").build();
    }

    public static synchronized UserDatabaseClient getInstance(Context context) {
        if (databaseClient==null) {
            databaseClient = new UserDatabaseClient(context);
        }
        return databaseClient;
    }

    public UserDatabase getDatabase() {
        return database;
    }
}
