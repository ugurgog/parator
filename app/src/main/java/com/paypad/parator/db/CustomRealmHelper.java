package com.paypad.parator.db;

import io.realm.Realm;

public class CustomRealmHelper {

    public static void deleteRealmDB(){
        Realm.deleteRealm(Realm.getDefaultConfiguration());
    }
}
