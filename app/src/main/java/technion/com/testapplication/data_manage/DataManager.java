package technion.com.testapplication.data_manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TimingLogger;

import java.io.IOException;

public class DataManager {
    private static final String DATA_PREF_NAME = "sulamot_data_local_cache";
    private SharedPreferences mLocalCache;
    private Context mContext;
    private TimingLogger mTimings;
    private String DATA_TAG = "DataManager";

    public DataManager(Context context) {
        mContext = context;
        this.mLocalCache = context.getSharedPreferences(DATA_PREF_NAME, Context.MODE_PRIVATE);
        mTimings = new TimingLogger(DATA_TAG, "DM:");
    }

    public void getData(final Cacheable cacheable, final Runnable onComplete) {
        if (mLocalCache.getBoolean(cacheable.getKey(), false))
        {
            // DATA IS IN CACHE
            try
            {
                mTimings.addSplit("getData: reading from cache...");
                cacheable.setData(InternalStorage.readObject(mContext, cacheable.getKey()));
                mTimings.addSplit("getData: finished reading from cache.");
                if (onComplete != null)
                {
                    mTimings.addSplit("getData: Running onComplete...");
                    onComplete.run();
                    mTimings.addSplit("getData: onComplete finished.");
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
        mTimings.addSplit("getData: Data not in cache, fetching async...");
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
            mTimings.addSplit("cacheData: writing to cache...");
            InternalStorage.writeObject(mContext, key, data);
            mTimings.addSplit("cachedata: finished writing to cache.");
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        SharedPreferences.Editor editor = mLocalCache.edit();
        editor.putBoolean(key, true);
        editor.apply();
        return true;
    }

    public Object getCachedData(String key) {
        if (mLocalCache.getBoolean(key, false))
        {
            // DATA IS IN CACHE
            try
            {
                mTimings.addSplit("getData: reading from cache...");
                Object res = InternalStorage.readObject(mContext, key);
                mTimings.addSplit("getData: finished writing to cache.");
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
        try
        {
            mTimings.addSplit("onDataFetchComplete: writing to cache...");
            InternalStorage.writeObject(mContext, cacheable.getKey(), cacheable.getData());
            mTimings.addSplit("onDataFetchComplete: finished writing to cache.");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = mLocalCache.edit();
        editor.putBoolean(cacheable.getKey(), true);
        editor.apply();
        if (onComplete != null)
        {
            mTimings.addSplit("onDataFetchComplete: Running onComplete...");
            onComplete.run();
        }
        mTimings.addSplit("onDataFetchComplete: onComplete finished.");
        mTimings.dumpToLog();
    }

}
