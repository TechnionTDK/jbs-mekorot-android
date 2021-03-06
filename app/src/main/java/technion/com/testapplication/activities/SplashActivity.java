package technion.com.testapplication.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchParashotAndPrakimTask;
import technion.com.testapplication.data_manage.DataManager;

/**
 * Created by tomerlevinson on 15/12/2017.
 * Shows until the get all parashot and prakim query finishes.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        DataManager dataManager = new DataManager(this);
        dataManager.validateAllCache();
        FetchParashotAndPrakimTask task = new FetchParashotAndPrakimTask(this);
        task.execute(JBSQueries.GET_ALL_PARASHOT, JBSQueries.GET_ALL_PRAKIM, JBSQueries.GET_BOOKS_AND_CATEGORIES);
    }
}
