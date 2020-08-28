package com.paypad.parator.utils;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class RealmMigrations implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();

        /*if (oldVersion == 1) {
            final RealmObjectSchema userSchema = schema.get("User");
            userSchema.addField("age", int.class);
        }*/
    }
}