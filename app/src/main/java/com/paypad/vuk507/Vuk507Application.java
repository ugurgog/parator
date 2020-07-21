package com.paypad.vuk507;

import android.app.Application;

import com.paypad.vuk507.db.CustomRealmHelper;
import com.paypad.vuk507.utils.RealmMigrations;
import com.paypad.vuk507.utils.sunmiutils.SunmiPrintHelper;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

import static com.paypad.vuk507.constants.CustomConstants.REALM_DB_NAME;
import static com.paypad.vuk507.constants.CustomConstants.REALM_SCHEMA_VERSION;

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