package com.paypad.vuk507;

import android.app.Application;

import com.paypad.vuk507.db.CustomRealmHelper;
import com.paypad.vuk507.utils.RealmMigrations;

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
        Realm.init(this);

        final RealmConfiguration config = new RealmConfiguration.Builder()
                .name(REALM_DB_NAME)
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigrations()).build();

        Realm.setDefaultConfiguration(config);
        Realm.getInstance(config);
    }


    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}