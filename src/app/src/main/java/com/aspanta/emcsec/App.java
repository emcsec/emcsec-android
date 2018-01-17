package com.aspanta.emcsec;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.aspanta.emcsec.db.SharedPreferencesHelper;
import com.aspanta.emcsec.db.room.MainDatabase;

public class App extends Application {

    private static MainDatabase dbInstance;
    public static App INSTANCE;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        adjustFontScale(getResources().getConfiguration());
        SharedPreferencesHelper.getInstance().initialize(this);
        dbInstance = Room
                .databaseBuilder(getApplicationContext(), MainDatabase.class, "main_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public static MainDatabase getDbInstance() {
        return dbInstance;
    }

    public static App getAppInstance() {
        return INSTANCE;
    }

    //disabling font scaling
    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1) {
            Log.d(TAG, "fontScale=" + configuration.fontScale);
            configuration.fontScale = (float) 1;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            assert wm != null;
            {
                wm.getDefaultDisplay().getMetrics(metrics);
                metrics.scaledDensity = configuration.fontScale * metrics.density;
                getBaseContext().getResources().updateConfiguration(configuration, metrics);
            }
        }
    }

    //reload app if font scale or any configuration option change during app using
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.exit(0);
    }
}