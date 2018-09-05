package technion.com.testapplication.data_manage;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

public class DataManager {
    private static final String DATA_PREF_NAME = "sulamot_data_local_cache";
    private SharedPreferences mLocalCache;
    private Context mContext;

    public DataManager(Context context) {
        mContext = context;
        this.mLocalCache = context.getSharedPreferences(DATA_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void getData(final Cacheable cacheable, final Runnable onComplete) {
        if (mLocalCache.getBoolean(cacheable.getKey(), false))
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
                return InternalStorage.readObject(mContext, key);
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
            InternalStorage.writeObject(mContext, cacheable.getKey(), cacheable.getData());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = mLocalCache.edit();
        editor.putBoolean(cacheable.getKey(), true);
        editor.apply();
        if (onComplete != null)
        {
            onComplete.run();
        }
    }

}
