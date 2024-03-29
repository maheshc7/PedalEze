package com.smazee.product.pedaleze;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "splashPref";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_LOGIN = "IsFirstTimeLogin";
    private static final String PHONE_NUMBER = "PhoneNumber";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLogin(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LOGIN, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLogin() {

        return pref.getBoolean(IS_FIRST_TIME_LOGIN, true);
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString(PHONE_NUMBER, phoneNumber);
        editor.commit();
    }

    public String getPhoneNumber() {
        return pref.getString(PHONE_NUMBER, "1236547890");
    }

    public void setName(String name) {
        editor.putString("Name", name);
        editor.commit();
    }

    public String getName() {
        return pref.getString("Name", "PedalEze User");
    }
}
