package com.paypad.vuk507;

import android.app.Application;

import io.realm.Realm;

public class Vuk507Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}