package com.paypad.vuk507.db;

import io.realm.Realm;

public class CustomRealmHelper {

    public static void deleteRealmDB(){
        Realm.deleteRealm(Realm.getDefaultConfiguration());
    }
}
