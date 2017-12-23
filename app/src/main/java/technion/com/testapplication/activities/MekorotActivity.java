package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.MekorotRecyclerViewAdapter;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchMekorotByScoreTask;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 18/12/2017.
 */
public class MekorotActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mMekorotCategories;
    private static final int CATEGORY_STRING_LENGTH = 9;

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

    private void setFilterDialog() {
        ArrayList<String> prettifiedCategories = new ArrayList<>();
        for (String category : mMekorotCategories) {
            String prettifiedCategory = category.substring(CATEGORY_STRING_LENGTH).replace("_", " ");
            prettifiedCategories.add(prettifiedCategory);
        }
        final CharSequence[] items = prettifiedCategories.toArray(
                new CharSequence[prettifiedCategories.size()]);
        final ArrayList selectedItems = new ArrayList();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.choose_category_to_filter))
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton(getResources().getString(R.string.choose_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
        View header = findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mekorot_activity);
        Intent receivedIntent = getIntent();
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(getResources().getString(R.string.mekorot_reference));
        ArrayList<String> psukimUris = (ArrayList<String>) receivedIntent.getExtras().get(
                getResources().getString(R.string.psukim_uris_extra));
        ArrayList<String> prefixedPsukimUris = new ArrayList<>();
        for (int i = 0; i < psukimUris.size(); i++) {
            String pasukUri = psukimUris.get(i);
            pasukUri = pasukUri.substring(pasukUri.lastIndexOf("/") + 1);
            pasukUri = "jbr:" + pasukUri;
            prefixedPsukimUris.add(i, pasukUri);
        }
        String mekorotQuery = JBSQueries.getMekorot(prefixedPsukimUris);
        String categoriesQuery = JBSQueries.getCategoriesByPsukim(prefixedPsukimUris);
        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(this);
        fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
    }

    public void setHeader() {
        TextView numOfResults = (TextView) findViewById(R.id.num_of_results);
        int mekorotSize = ((MekorotRecyclerViewAdapter) mAdapter).getMekorotSize();
        numOfResults.setText("תוצאות: " + Integer.toString(mekorotSize));

    }

    public void setRecyclerViewAdapter(ArrayList<MakorModel> mekorot,
                                       ArrayList<String> mekorotCategories) {
        mMekorotCategories = mekorotCategories;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MekorotRecyclerViewAdapter(mekorot, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        setHeader();
        setFilterDialog();
    }
}
