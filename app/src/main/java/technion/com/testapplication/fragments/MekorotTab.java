package technion.com.testapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.MekorotRecyclerViewAdapter;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchMekorotByScoreTask;
import technion.com.testapplication.models.CategoryModel;
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class MekorotTab extends Fragment {
    private ArrayList<String> mPrefixedPsukimUris;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<CategoryModel> mMekorotCategories;
    private static final int CATEGORY_STRING_LENGTH = 9;
    private static ArrayList<Integer> mDialogSelectedItems = new ArrayList<>();
    private static ArrayList<String> mDialogSelectedItemsNames = new ArrayList<>();
    MekorotChangesListener mCallback;
    private static final String CATEGORY_NUM_OPEN_BRACE = " (";
    private static final String CATEGORY_NUM_CLOSE_BRACE = ")";

    public MekorotTab() {
    }

    public interface MekorotChangesListener {
        public void setFilterIcon(Dialog dialog);
        public void setTabResultsNum(int numOfResults);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MekorotChangesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MekorotChangesListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setFilterDialog() {
        final Fragment mekorotTabFrag = this;
        ArrayList<String> prettifiedCategories = new ArrayList<>();
        for (CategoryModel category : mMekorotCategories) {
            String prettifiedCategory = category.getCategoryName().substring(
                    CATEGORY_STRING_LENGTH).replace("_",
                    " ")
                    + CATEGORY_NUM_OPEN_BRACE
                    + category.getCategoryRefernceNum()
                    + CATEGORY_NUM_CLOSE_BRACE;
            prettifiedCategories.add(prettifiedCategory);
        }
        final CharSequence[] items = prettifiedCategories.toArray(
                new CharSequence[prettifiedCategories.size()]);
        boolean[] checkedItemsPositions = new boolean[mMekorotCategories.size()];
        for (Integer selectedItem : mDialogSelectedItems) {
            checkedItemsPositions[selectedItem] = true;
        }
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
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
                                            indexSelected).getCategoryName();
                                    mDialogSelectedItemsNames.add(prefixedSelection);
                                } else if (mDialogSelectedItems.contains(indexSelected)) {
                                    // Else, if the item is already in the array, remove it
                                    mDialogSelectedItems.remove(Integer.valueOf(indexSelected));
                                    String prefixedSelection = getResources().getString(
                                            R.string.jbr_prefix) + mMekorotCategories.get(
                                            indexSelected).getCategoryName();
                                    mDialogSelectedItemsNames.remove(prefixedSelection);
                                }
                            }
                        }).setPositiveButton(getResources().getString(R.string.choose_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String mekorotQuery;
                                boolean shouldFilter = false;
                                if (mDialogSelectedItemsNames.size() > 0) {
                                    mekorotQuery = JBSQueries.getMekorotFiltered(
                                            mDialogSelectedItemsNames, mPrefixedPsukimUris);
                                    shouldFilter = true;
                                } else {
                                    mekorotQuery = JBSQueries.getMekorotWithAllData(mPrefixedPsukimUris);
                                }
                                String categoriesQuery = JBSQueries.getCategoriesByPsukimWithReferenceNumber(
                                        mPrefixedPsukimUris);
                                FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(
                                        mekorotTabFrag, shouldFilter);
                                fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create();
        mCallback.setFilterIcon(dialog);
    }

    public void runMekorotAndCategoriesQueries(ArrayList<String> psukimUris) {
        mPrefixedPsukimUris = new ArrayList<>();
        for (int i = 0; i < psukimUris.size(); i++) {
            String pasukUri = psukimUris.get(i);
            pasukUri = pasukUri.substring(pasukUri.lastIndexOf("/") + 1);
            pasukUri = getResources().getString(R.string.jbr_prefix) + pasukUri;
            mPrefixedPsukimUris.add(i, pasukUri);
        }
        String mekorotQuery = JBSQueries.getMekorotWithAllData(mPrefixedPsukimUris);
        String categoriesQuery = JBSQueries.getCategoriesByPsukimWithReferenceNumber(
                mPrefixedPsukimUris);
        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(this, false);
        fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
    }

    public void setRecyclerViewAdapter(ArrayList<MakorModel> mekorot,
                                       ArrayList<CategoryModel> mekorotCategories) {
        if (getView() != null) {
            mMekorotCategories = mekorotCategories;
            mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
            mAdapter = new MekorotRecyclerViewAdapter(mPrefixedPsukimUris, mekorot, getContext());
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            setFilterDialog();
            mCallback.setTabResultsNum(mekorot.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mekorot_tab, container, false);
    }

}
