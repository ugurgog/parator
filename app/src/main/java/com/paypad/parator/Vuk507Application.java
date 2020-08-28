package com.paypad.parator;

import android.app.Application;

import com.paypad.parator.utils.sunmiutils.SunmiPrintHelper;

import io.realm.Realm;

public class Vuk507Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();

        /*final RealmConfiguration config = new RealmConfiguration.Builder()
                .name(REALM_DB_NAME)
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigrations()).build();

        Realm.setDefaultConfiguration(config);
        Realm.getInstance(config);*/
    }


    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }

    private void init(){
        Realm.init(this);
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this);
    }
}