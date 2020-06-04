package ru.mkedonsky.lesson5;

import android.app.Application;

import androidx.room.Room;

public class OrmApp extends Application {
    private static final String DATABASE_NAME = "DATA_BASE_USER_GIT";
    public static MyDatabase database;
    protected static OrmApp INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
//        SugarContext.init(this);
        database = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,DATABASE_NAME)
                .build();
        INSTANCE = this;
    }

    public static MyDatabase getDB() {
        return database;
    }

    public static OrmApp get() {
        return INSTANCE;
    }
}
