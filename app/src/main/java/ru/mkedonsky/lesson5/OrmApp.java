package ru.mkedonsky.lesson5;

import android.app.Activity;
import android.app.Application;

import androidx.room.Room;

import java.util.List;

public class OrmApp extends Application {
    private static final String DATABASE_NAME = "DATA_BASE_USER_GIT";
    public static MyDatabase database;
    protected static OrmApp INSTANCE;
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, DATABASE_NAME).build();
        INSTANCE = this;
        component = DaggerAppComponent.create();
    }

    public MyDatabase getDB() {
        return database;
    }

    public static OrmApp get() {
        return INSTANCE;
    }

    public static AppComponent getComponent() {
        return component;
    }

    public static SugarComponent makeSugarComponent(List<RetrofitModel> models) {
        return component.getSugarComponent(new DaggerSugar(models));
    }
}
