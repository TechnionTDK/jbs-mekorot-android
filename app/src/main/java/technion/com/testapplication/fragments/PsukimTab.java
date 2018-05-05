package technion.com.testapplication.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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

    // Defines a way for the Psukim tab to communicate with the Mekorot tab
    // via the MainActivity.
    public interface OnMoveToMekorotTabListener {
        void onMoveToMekorotTab(ArrayList<String> psukimUris);

        void onPsukimSelected(boolean areNewSelected);
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
            getView().findViewById(R.id.unchoose_all).setVisibility(View.VISIBLE);
        }
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(getView().getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // Choose all view configuration:
        View chooseAll = getView().findViewById(R.id.choose_all);
        final PsukimRecyclerViewAdapter psukimRecyclerViewAdapter = ((PsukimRecyclerViewAdapter) mAdapter);
        final ImageView chooseAllImage = getView().findViewById(
                R.id.choose_all_image);
        chooseAllImage.setImageResource(
                R.drawable.ic_choose_all_unselected);
        psukimRecyclerViewAdapter.clickOnAllItems(false);
        chooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psukimRecyclerViewAdapter.getAreAllItemsClicked()) {
                    chooseAllImage.setImageResource(
                            R.drawable.ic_choose_all_unselected);
                    psukimRecyclerViewAdapter.clickOnAllItems(false);
                } else {
                    chooseAllImage.setImageResource(R.drawable.ic_choose_all_selected);
                    psukimRecyclerViewAdapter.clickOnAllItems(true);
                }
            }
        });

        // Unchoose all view configuration
        View unchooseAll = getView().findViewById(R.id.unchoose_all);
        unchooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psukimRecyclerViewAdapter.clickOnAllItems(false);
                chooseAllImage.setImageResource(
                        R.drawable.ic_choose_all_unselected);
            }
        });
    }

    public PsukimTab() {
    }

    /**
     * Used from the main activity in order to notify the psukim tab
     * that we are about to move to the mekorot tab.
     */
    public void moveToMekorot() {
        ArrayList<String> psukimUris = new ArrayList<>();
        if (mAdapter != null) {
            psukimUris = ((PsukimRecyclerViewAdapter) mAdapter).getAllPsukimUris();
        }
        if (psukimUris.size() == 0) {
            mCallback.onPsukimSelected(false);
        } else {
            if (Arrays.asList(psukimUris).containsAll(
                    Arrays.asList(mCurrentPsukim)) && psukimUris.size() == mCurrentPsukim.size()) {
                mCallback.onPsukimSelected(false);
            } else {
                mCallback.onPsukimSelected(true);
            }
        }
        mCurrentPsukim.clear();
        mCurrentPsukim.addAll(psukimUris);
        mCallback.onMoveToMekorotTab(psukimUris);
    }

    /**
     * The FAB dialog notifies the tab that it should load relevant psukim.
     *
     * @param perekOrParashaUri
     */
    public void loadPuskim(String perekOrParashaUri) {
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
        rootView.findViewById(R.id.unchoose_all).setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCurrentPsukim != null) {
            mCurrentPsukim.clear();
        }
    }
}
