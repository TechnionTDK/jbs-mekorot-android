package technion.com.testapplication.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.async.FetchParashotTask;
import technion.com.testapplication.R;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        FetchParashotTask task = new FetchParashotTask(this);
        task.execute(JBSQueries.GET_ALL_PARASHOT);
    }
}
