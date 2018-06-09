package technion.com.testapplication.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.PsukimRecyclerViewAdapter;
import technion.com.testapplication.async.FetchPsukimBySubstrTask;
import technion.com.testapplication.async.FetchPsukimTask;
import technion.com.testapplication.models.PasukModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class PsukimTab extends Fragment {

    private RecyclerView.Adapter mAdapter;
    OnMoveToMekorotTabListener mCallback;
    private static ArrayList<String> mCurrentPsukim = new ArrayList<>();
    private ArrayList<Pair<String, String>> mPrakimOrParashotPairs = new ArrayList<>();
    private int mCurPerekParashId = 0;

    // Defines a way for the Psukim tab to communicate with the Mekorot tab
    // via the MainActivity.
    public interface OnMoveToMekorotTabListener {
        void onMoveToMekorotTab(ArrayList<String> psukimUris);

        void onPsukimSelected(boolean areNewSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mCallback = (OnMoveToMekorotTabListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                                                 + " must implement OnMoveToMekorotTabListener");
        }
    }

    public void setRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        if (getView() != null)
        {
            getView().findViewById(R.id.choose_all).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.next_perek).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.prev_perek).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.psukim_actions_bar_layout).setVisibility(View.VISIBLE);
        }
        RecyclerView mRecyclerView = getView().findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(getView().getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

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
                if (psukimRecyclerViewAdapter.getAreAllItemsClicked())
                {
                    chooseAllImage.setImageResource(
                            R.drawable.ic_choose_all_unselected);
                    psukimRecyclerViewAdapter.clickOnAllItems(false);
                }
                else
                {
                    chooseAllImage.setImageResource(R.drawable.ic_choose_all_selected);
                    psukimRecyclerViewAdapter.clickOnAllItems(true);
                }
            }
        });

        final PsukimTab selfie = this;

        View nextPerek = getView().findViewById(R.id.next_perek);
        nextPerek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfie.onNextPerek();
            }
        });

        View prevPerek = getView().findViewById(R.id.prev_perek);
        prevPerek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfie.onPrevPerek();
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
        if (mAdapter != null)
        {
            psukimUris = ((PsukimRecyclerViewAdapter) mAdapter).getAllPsukimUris();
        }
        if (psukimUris.size() == 0)
        {
            mCallback.onPsukimSelected(false);
        }
        else
        {
            if (Arrays.asList(psukimUris).containsAll(
                    Arrays.asList(mCurrentPsukim)) && psukimUris.size() == mCurrentPsukim.size())
            {
                mCallback.onPsukimSelected(false);
            }
            else
            {
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
     * @param perekOrParashaName
     * @param perekOrParashaPairs
     */
    public void loadPuskim(String perekOrParashaName, ArrayList<Pair<String, String>> perekOrParashaPairs) {
        mPrakimOrParashotPairs = perekOrParashaPairs;
        for (int i = 0; i < mPrakimOrParashotPairs.size(); i++)
        {
            Pair<String, String> uriLabel = mPrakimOrParashotPairs.get(i);
            if (uriLabel.first.equals(perekOrParashaName))
            {
                mCurPerekParashId = i;
                break;
            }
        }
        loadPsukimByIndex(mCurPerekParashId);
    }

    public void loadPsukimByIndex(int index) {
        Pair<String, String> uriLabel = mPrakimOrParashotPairs.get(index);
        String perekOrParashaUri = uriLabel.second.substring(uriLabel.second.lastIndexOf("/") + 1);
        String psukimByParashaQuery = JBSQueries.getAllPsukimFromParashaQuery(perekOrParashaUri);
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(this);
        fetchPsukimTask.execute(psukimByParashaQuery);
    }

    public void loadPuskimBySubstr(String pasukSubstr) {
        String psukimBySubstrQuery = JBSQueries.getAllPsukimBySubstrQuery(pasukSubstr);
        FetchPsukimBySubstrTask fetchPsukimBySubstrTask = new FetchPsukimBySubstrTask(this);
        fetchPsukimBySubstrTask.execute(psukimBySubstrQuery);
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
        rootView.findViewById(R.id.next_perek).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.prev_perek).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.psukim_actions_bar_layout).setVisibility(View.INVISIBLE);
        return rootView;
    }

    public void onNextPerek() {
        // Prevent overflow
        if (mCurPerekParashId < mPrakimOrParashotPairs.size() - 1)
        {
            loadPsukimByIndex(++mCurPerekParashId);
        }
    }

    public void onPrevPerek() {
        // Prevent underflow
        if (mCurPerekParashId > 0)
        {
            loadPsukimByIndex(--mCurPerekParashId);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCurrentPsukim != null)
        {
            mCurrentPsukim.clear();
        }
    }
}
