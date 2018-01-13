package technion.com.testapplication.activities;

import android.app.Activity;
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
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 18/12/2017.
 */
public class MekorotActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mMekorotCategories;
    private static final int CATEGORY_STRING_LENGTH = 9;
    private ArrayList<String> mPrefixedPsukimUris;
    private static ArrayList<Integer> mDialogSelectedItems = new ArrayList<>();
    private static ArrayList<String> mDialogSelectedItemsNames = new ArrayList<>();

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
                return true;

            case R.id.action_favorite:
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * set the filter dialog that will have inside of it all the
     * relavent categories deducted from the psukim selected
     * in the psukim activity.
     * @param activity
     */
    private void setFilterDialog(final Activity activity) {
        ArrayList<String> prettifiedCategories = new ArrayList<>();
        for (String category : mMekorotCategories) {
            String prettifiedCategory = category.substring(CATEGORY_STRING_LENGTH).replace("_",
                    " ");
            prettifiedCategories.add(prettifiedCategory);
        }
        final CharSequence[] items = prettifiedCategories.toArray(
                new CharSequence[prettifiedCategories.size()]);
        boolean[] checkedItemsPositions = new boolean[mMekorotCategories.size()];
        for (Integer selectedItem : mDialogSelectedItems) {
            checkedItemsPositions[selectedItem] = true;
        }
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.choose_category_to_filter))
                .setMultiChoiceItems(items, checkedItemsPositions,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mDialogSelectedItems.add(indexSelected);
                                    String prefixedSelection = getResources().getString(
                                            R.string.jbr_prefix) + mMekorotCategories.get(
                                            indexSelected);
                                    mDialogSelectedItemsNames.add(prefixedSelection);
                                } else if (mDialogSelectedItems.contains(indexSelected)) {
                                    // Else, if the item is already in the array, remove it
                                    mDialogSelectedItems.remove(Integer.valueOf(indexSelected));
                                    String prefixedSelection = getResources().getString(
                                            R.string.jbr_prefix) + mMekorotCategories.get(
                                            indexSelected);
                                    mDialogSelectedItemsNames.remove(prefixedSelection);
                                }
                            }
                        }).setPositiveButton(getResources().getString(R.string.choose_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String mekorotQuery;
                                if (mDialogSelectedItemsNames.size() > 0) {
                                    mekorotQuery = JBSQueries.getMekorotFiltered(
                                            mDialogSelectedItemsNames, mPrefixedPsukimUris);
                                } else {
                                    mekorotQuery = JBSQueries.getMekorot(mPrefixedPsukimUris);
                                }
                                String categoriesQuery = JBSQueries.getCategoriesByPsukim(
                                        mPrefixedPsukimUris);
//                                FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(
//                                        activity);
//                                fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
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

    private void setToolbar() {
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(getResources().getString(R.string.mekorot_reference));
    }

    /**
     * Run queries to receive:
     * 1) Mekorot list.
     * 2) Mekorot categories.
     */
    private void runMekorotAndCategoriesQueries(Intent receivedIntent) {
        ArrayList<String> psukimUris = (ArrayList<String>) receivedIntent.getExtras().get(
                getResources().getString(R.string.psukim_uris_extra));
        mPrefixedPsukimUris = new ArrayList<>();
        for (int i = 0; i < psukimUris.size(); i++) {
            String pasukUri = psukimUris.get(i);
            pasukUri = pasukUri.substring(pasukUri.lastIndexOf("/") + 1);
            pasukUri = getResources().getString(R.string.jbr_prefix) + pasukUri;
            mPrefixedPsukimUris.add(i, pasukUri);
        }
        String mekorotQuery = JBSQueries.getMekorot(mPrefixedPsukimUris);
        String categoriesQuery = JBSQueries.getCategoriesByPsukim(mPrefixedPsukimUris);
//        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(this);
//        fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mekorot_activity);
        setToolbar();
        Intent receivedIntent = getIntent();
        runMekorotAndCategoriesQueries(receivedIntent);
    }

    public void setHeader() {
        TextView numOfResults = (TextView) findViewById(R.id.num_of_results);
        int mekorotSize = ((MekorotRecyclerViewAdapter) mAdapter).getMekorotSize();
        numOfResults.setText(
                getResources().getString(R.string.num_of_mekorot) + Integer.toString(mekorotSize));

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
        setFilterDialog(this);
    }
}
