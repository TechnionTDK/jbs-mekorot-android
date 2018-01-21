package technion.com.testapplication.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.adapters.PsukimRecyclerViewAdapter;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchPsukimTask;
import technion.com.testapplication.models.PasukModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class PsukimTab extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    OnMoveToMekorotTabListener mCallback;
    private static ArrayList<String> mCurrentPsukim = new ArrayList<>();

    public interface OnMoveToMekorotTabListener {
        public void onMoveToMekorotTab(ArrayList<String> psukimUris);
        public void onPsukimSelected(boolean areNewSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnMoveToMekorotTabListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMoveToMekorotTabListener");
        }
    }

    public void setRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        if (getView() != null) {
            getView().findViewById(R.id.choose_all).setVisibility(View.VISIBLE);
        }
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(getView().getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        View chooseAll = getView().findViewById(R.id.choose_all);
        final PsukimRecyclerViewAdapter psukimRecyclerViewAdapter = ((PsukimRecyclerViewAdapter) mAdapter);
        final ImageView chooseAllImage = (ImageView) getView().findViewById(
                R.id.choose_all_image);
        chooseAllImage.setImageResource(
                R.drawable.ic_check_box_outline_blank_black_24dp);
        psukimRecyclerViewAdapter.clickOnAllItems(false);
        chooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psukimRecyclerViewAdapter.getAreAllItemsClicked()) {
                    chooseAllImage.setImageResource(
                            R.drawable.ic_check_box_outline_blank_black_24dp);
                    psukimRecyclerViewAdapter.clickOnAllItems(false);
                } else {
                    chooseAllImage.setImageResource(R.drawable.ic_check_box_black_24dp);
                    psukimRecyclerViewAdapter.clickOnAllItems(true);
                }
            }
        });
    }

    public PsukimTab() {
    }

    public void moveToMekorot() {
        ArrayList<String> psukimUris = new ArrayList<>();
        if (mAdapter != null) {
            psukimUris = ((PsukimRecyclerViewAdapter) mAdapter).getAllPsukimUris();
        }
        if (Arrays.asList(psukimUris).containsAll(
                Arrays.asList(mCurrentPsukim)) && psukimUris.size() == mCurrentPsukim.size()) {
            mCallback.onPsukimSelected(false);
        } else {
            mCallback.onPsukimSelected(true);
        }
        mCurrentPsukim.clear();
        mCurrentPsukim.addAll(psukimUris);
        mCallback.onMoveToMekorotTab(psukimUris);
    }

    public void loadPuskim(String perekOrParashaUri, String perekOrParashaName) {
        String psukimByParashaQuery = JBSQueries.getAllPsukimFromParashaQuery(perekOrParashaUri);
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(this);
        fetchPsukimTask.execute(psukimByParashaQuery);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.psukim_tab, container, false);
        rootView.findViewById(R.id.choose_all).setVisibility(View.INVISIBLE);

        return rootView;
    }

}
