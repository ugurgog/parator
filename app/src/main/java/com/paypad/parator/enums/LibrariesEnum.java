package com.paypad.parator.enums;

public enum  LibrariesEnum {

    APACHE_LICENSE("Apache License 2.0", "https://www.apache.org/licenses/LICENSE-2.0", null, true),
    BUTTERKNIFE("Butter Knife", "https://github.com/JakeWharton/butterknife", "JakeWharton",false),
    GLIDE("Glide", "https://github.com/bumptech/glide", "Bump Technologies",false),
    EVENTBUS("EventBus", "https://github.com/greenrobot/EventBus", "Markus Junginger",false),
    OKHTTP("OkHttp", "https://square.github.io/okhttp/", "Square",false),
    LIB_PHONE_NUMBER("libphonenumber", "https://github.com/MichaelRocks/libphonenumber-android", "Michael Rozumyanskiy",false),
    JODA_TIME("joda-time", "https://github.com/JodaOrg/joda-time", "Joda.org",false),
    AND_TIMES_SQUARE("android-times-square", "https://github.com/square/android-times-square", "Square",false),
    MP_ANDROID_CHART("MPAndroidChart", "https://github.com/PhilJay/MPAndroidChart", "Philipp Jahoda",false),
    DATE_TIME_PICKER("MaterialDateTimePicker", "https://github.com/wdullaer/MaterialDateTimePicker", "wdullaer",false),

    MIT_LICENSE("MIT License", "https://opensource.org/licenses/MIT", null, true),
    AND_GIF_DRAWABLE("android-gif-drawable", "https://github.com/koral--/android-gif-drawable", "Karol Wrótniak",false),
    EXP_RECYCLERVIEW("expandable-recycler-view", "https://github.com/thoughtbot/expandable-recycler-view", "thoughtbot, inc.",false),

    GNU_LICENSE("GNU General Public License v3.0", "https://choosealicense.com/licenses/gpl-3.0/", null, true),
    TOUCHY("touchy", "https://github.com/fatih-iver/touchy", "Fatih İver",false);


    private final String label;
    private final String link;
    private final String owner;
    private final boolean isTitle;

    LibrariesEnum(String label, String link, String owner, boolean isTitle) {
        this.label = label;
        this.link = link;
        this.owner = owner;
        this.isTitle = isTitle;
    }

    public String getLabel() {
        return label;
    }

    public String getLink() {
        return link;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public String getOwner() {
        return owner;
    }
}
