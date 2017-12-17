package technion.com.testapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.async.FetchPsukimTask;
import technion.com.testapplication.models.PasukModel;
import technion.com.testapplication.PsukimRecyclerViewAdapter;
import technion.com.testapplication.R;

/**
 * Created by tomerlevinson on 13/12/2017.
 */
public class PsukimActivity extends AppCompatActivity {

    private String mParashaName;
    private String mParashaUri;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_favorite).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psukim_list);
        // Get extras
        Intent intent = getIntent();
        mParashaName = intent.getStringExtra(getResources().getString(R.string.perek_or_parasha_name_extra));
        String uri = intent.getStringExtra(getResources().getString(R.string.perek_or_parasha_uri_extra));
        mParashaUri = uri.substring(uri.lastIndexOf("/") + 1);
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mParashaName);
        String psukimByParashaQuery = JBSQueries.getAllPsukimFromParashaQuery(mParashaUri);
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(this);
        fetchPsukimTask.execute(psukimByParashaQuery);

    }

    public void setRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        View chooseAll = findViewById(R.id.choose_all);
        chooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsukimRecyclerViewAdapter psukimRecyclerViewAdapter = ((PsukimRecyclerViewAdapter) mAdapter);
                ImageView chooseAllImage = (ImageView) findViewById(R.id.choose_all_image);
                if (psukimRecyclerViewAdapter.getAreAllItemsClicked()) {
                    chooseAllImage.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                    psukimRecyclerViewAdapter.clickOnAllItems(false);
                } else {
                    chooseAllImage.setImageResource(R.drawable.ic_check_box_black_24dp);
                    psukimRecyclerViewAdapter.clickOnAllItems(true);
                }
            }
        });
    }

}
