package com.paypad.vuk507.db;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmMigrationNeededException;

public class CustomRealmHelper {

    public static void deleteRealmDB(){
        Realm.deleteRealm(Realm.getDefaultConfiguration());
    }
}
