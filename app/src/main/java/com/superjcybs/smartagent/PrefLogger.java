package com.superjcybs.smartagent;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
public class PrefLogger {
    public static void logAllPrefs(Context context, String prefName) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        Log.d("JERRYSEE PREF_LOGGER", "----- SharedPreferences: " + prefName + " -----");
        if (allEntries.isEmpty()) {
            Log.d("JERRYSEE PREF_LOGGER", "No entries found.");
        }

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("JERRYSEE PREF_LOGGER", entry.getKey() + " = " + entry.getValue());
        }
        Log.d("JERRYSEE PREF_LOGGER", "---------------------------------------------");
    }
}
