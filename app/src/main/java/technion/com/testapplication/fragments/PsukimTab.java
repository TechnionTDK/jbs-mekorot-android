package technion.com.testapplication.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.PsukimRecyclerViewAdapter;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchPsukimTask;
import technion.com.testapplication.models.PasukModel;

/**
 * Created by tomerlevinson on 13/01/2018.
 */
public class PsukimTab extends Fragment {

    private String mPerekOrParashaName;
    private String mPerekOrParashaUri;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public void setRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(getView().getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        View chooseAll = getView().findViewById(R.id.choose_all);
        chooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsukimRecyclerViewAdapter psukimRecyclerViewAdapter = ((PsukimRecyclerViewAdapter) mAdapter);
                ImageView chooseAllImage = (ImageView) getView().findViewById(R.id.choose_all_image);
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
        // Required empty public constructor
    }

    public void loadPuskim(String perekOrParashaUri, String perekOrParashaName) {
        String psukimByParashaQuery = JBSQueries.getAllPsukimFromParashaQuery(perekOrParashaUri);
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(getActivity());
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
        View rootView =  inflater.inflate(R.layout.psukim_tab, container, false);

        return rootView;
    }

}
