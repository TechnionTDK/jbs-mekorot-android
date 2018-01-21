package technion.com.testapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by tomerlevinson on 21/01/2018.
 */
public class PreferencesUtils {
    private static final Object LOCK = new Object();

    /**
     * Retrieves a SharedPreference instance by the input file name.
     * If such instance doesn't exist, it will be created (with the desired file name)
     * after editions will be committed to this instance.
     *
     * @param fileName
     * @return shared preferences for specific fileName, or null if application context
     * is null.
     */
    public static SharedPreferences getSharedPreferencesByFileName(String fileName,
                                                                   Context context) {
        synchronized (LOCK) {
            if (context != null) {
                return context
                        .getSharedPreferences(fileName, Context.MODE_PRIVATE);
            }
            return null;
        }
    }

    /**
     * Stores a key-value pair inside SharedPreferences where the value is a String Set.
     *
     * @param preferencesFileName
     * @param key
     * @param value
     * @param override            - parameter to indicate if we should override the value in case the key already exists.
     */
    public static void storeStringSet(String preferencesFileName, String key, Set<String> value,
                                      boolean override, Context context) {
        synchronized (LOCK) {
            SharedPreferences sharedPrefs = getSharedPreferencesByFileName(preferencesFileName,
                    context);
            if (sharedPrefs != null) {
                if (override || !sharedPrefs.contains(key)) {
                    sharedPrefs.edit().putStringSet(key, value).apply();
                }
            }
        }
    }

    /**
     * Retrieves a String Set from SharedPreferences that was stored by the input key.
     *
     * @param preferencesFileName
     * @param key                 - the key which value is the String Set we wish to retrieve.
     * @return
     */
    public static Set<String> retrieveStoredStringSet(String preferencesFileName, String key,
                                                      Context context) {
        synchronized (LOCK) {
            SharedPreferences sharedPreferences = getSharedPreferencesByFileName(
                    preferencesFileName, context);
            if (sharedPreferences != null) {
                return sharedPreferences.getStringSet(key, null);
            }
            return null;
        }
    }

    /**
     * Deletes data from SharedPreferences
     *
     * @param preferencesFileName
     * @param key                 - the key of the data we wish to delete.
     */
    public static void deleteStoredDataByKey(String preferencesFileName, String key,
                                             Context context) {
        synchronized (LOCK) {
            if (key != null) {
                SharedPreferences sharedPreferences = getSharedPreferencesByFileName(
                        preferencesFileName, context);
                if (sharedPreferences != null) {
                    sharedPreferences
                            .edit()
                            .remove(key)
                            .apply();
                }
            }
        }
    }

    /**
     * Clears the contents of preferencesFileName.
     *
     * @param preferencesFileName
     */
    public static void clearStoredData(String preferencesFileName, Context context) {
        synchronized (LOCK) {
            SharedPreferences sharedPreferences = getSharedPreferencesByFileName(
                    preferencesFileName, context);
            if (sharedPreferences != null) {
                sharedPreferences
                        .edit()
                        .clear()
                        .apply();
            }
        }
    }
}
