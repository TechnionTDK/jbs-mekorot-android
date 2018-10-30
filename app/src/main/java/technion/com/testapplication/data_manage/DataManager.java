package technion.com.testapplication.data_manage;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DataManager {
    private static final String DATA_PREF_NAME = "sulamot_data_local_cache";
    private static final String DATA_AGE_SET = "sulamot_data_age_set";
    private SharedPreferences mLocalCache;
    private Context mContext;

    public DataManager(Context context) {
        mContext = context;
        this.mLocalCache = context.getSharedPreferences(DATA_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void getData(final Cacheable cacheable, final Runnable onComplete) {
        boolean cacheValid = validateCache(cacheable.getKey());
        if (cacheValid && mLocalCache.getBoolean(cacheable.getKey(), false))
        {
            // DATA IS IN CACHE
            try
            {
                cacheable.setData(InternalStorage.readObject(mContext, cacheable.getKey()));
                if (onComplete != null)
                {
                    onComplete.run();
                }
                return;
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        // DATA NOT IN CACHE
        final DataManager selfie = this;
        Runnable onDataFetchComplete = new Runnable() {
            @Override
            public void run() {
                selfie.onDataFetchComplete(cacheable, onComplete);
            }
        };
        cacheable.FetchDataAsync(onDataFetchComplete);
    }

    public boolean cacheData(String key, Object data) {
        try
        {
            InternalStorage.writeObject(mContext, key, data);
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        String dateKey = key + "_date";
        Set<String> ageSet = mLocalCache.getStringSet(DATA_AGE_SET, null);
        if (ageSet == null)
        {
            ageSet = new HashSet<>();
        }
        ageSet.add(dateKey);
        SharedPreferences.Editor editor = mLocalCache.edit();
        editor.putStringSet(DATA_AGE_SET, ageSet);
        editor.putBoolean(key, true);
        Date date = new Date();
        editor.putString(dateKey, String.valueOf(date.getTime()));
        editor.apply();
        return true;
    }

    public void validateAllCache() {
        Set<String> ageSet = mLocalCache.getStringSet(DATA_AGE_SET, null);
        if (ageSet == null)
        {
            return;
        }
        Set<String> updatedAgeSet = new HashSet<>(ageSet);
        SharedPreferences.Editor editor = mLocalCache.edit();
        for (String dateKey : ageSet)
        {
            Date dateFromCache = new Date(Long.valueOf(mLocalCache.getString(dateKey, "")));
            Date nowDate = new Date();
            long diffInMillies = Math.abs(nowDate.getTime() - dateFromCache.getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diffDays > 14)
            {
                String key = dateKey.replace("_date", "");
                InternalStorage.deleteObject(mContext, key);
                updatedAgeSet.remove(dateKey);
                editor.remove(dateKey);
                editor.remove(key);
            }
        }
        editor.putStringSet(DATA_AGE_SET, ageSet);
        editor.apply();
    }

    private boolean validateCache(String key) {
        String dateKey = key + "_date";
        if (!mLocalCache.contains(dateKey))
        {
            return false;
        }
        Date dateFromCache = new Date(Long.valueOf(mLocalCache.getString(dateKey, "")));
        Date nowDate = new Date();
        long diffInMillies = Math.abs(nowDate.getTime() - dateFromCache.getTime());
        long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diffDays > 14)
        {
            InternalStorage.deleteObject(mContext, key);
            Set<String> ageSet = mLocalCache.getStringSet(DATA_AGE_SET, null);
            if (ageSet != null)
            {
                ageSet.remove(dateKey);
            }
            SharedPreferences.Editor editor = mLocalCache.edit();
            editor.putStringSet(DATA_AGE_SET, ageSet);
            editor.remove(dateKey);
            editor.remove(key);
            editor.apply();
            return false;
        }
        return true;
    }

    public Object getCachedData(String key) {
        boolean cacheValid = validateCache(key);
        if (cacheValid && mLocalCache.getBoolean(key, false))
        {
            // DATA IS IN CACHE
            try
            {
                String dateKey = key + "_date";
                Object res = InternalStorage.readObject(mContext, key);
                SharedPreferences.Editor editor = mLocalCache.edit();
                Date date = new Date();
                editor.putString(dateKey, String.valueOf(date.getTime()));
                editor.apply();
                return res;
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void onDataFetchComplete(Cacheable cacheable, Runnable onComplete) {
        cacheData(cacheable.getKey(), cacheable.getData());
        if (onComplete != null)
        {
            onComplete.run();
        }
    }

}
