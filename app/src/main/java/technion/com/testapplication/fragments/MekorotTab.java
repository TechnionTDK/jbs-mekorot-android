package technion.com.testapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import technion.com.testapplication.models.MakorModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class MekorotTab extends Fragment {
    private ArrayList<String> mPrefixedPsukimUris;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mMekorotCategories;
    private static final int CATEGORY_STRING_LENGTH = 9;
    private static ArrayList<Integer> mDialogSelectedItems = new ArrayList<>();
    private static ArrayList<String> mDialogSelectedItemsNames = new ArrayList<>();

    public MekorotTab() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void runMekorotAndCategoriesQueries(ArrayList<String> psukimUris) {
        mPrefixedPsukimUris = new ArrayList<>();
        for (int i = 0; i < psukimUris.size(); i++) {
            String pasukUri = psukimUris.get(i);
            pasukUri = pasukUri.substring(pasukUri.lastIndexOf("/") + 1);
            pasukUri = getResources().getString(R.string.jbr_prefix) + pasukUri;
            mPrefixedPsukimUris.add(i, pasukUri);
        }
        String mekorotQuery = JBSQueries.getMekorot(mPrefixedPsukimUris);
        String categoriesQuery = JBSQueries.getCategoriesByPsukim(mPrefixedPsukimUris);
        FetchMekorotByScoreTask fetchMekorotByScoreTask = new FetchMekorotByScoreTask(this);
        fetchMekorotByScoreTask.execute(mekorotQuery, categoriesQuery);
    }

    public void setRecyclerViewAdapter(ArrayList<MakorModel> mekorot,
                                       ArrayList<String> mekorotCategories) {
        if (getView() != null) {
            mMekorotCategories = mekorotCategories;
            mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
            mAdapter = new MekorotRecyclerViewAdapter(mekorot, getContext());
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            //setHeader();
            //setFilterDialog(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mekorot_tab, container, false);
    }

}
