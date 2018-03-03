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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import technion.com.testapplication.R;
import technion.com.testapplication.adapters.FavoritesRecyclerViewAdapter;
import technion.com.testapplication.utils.PreferencesUtils;

/**
 * Created by tomerlevinson on 03/03/2018.
 */
public class FavoritesTab extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    FavoritesChangeListener mCallback;

    public FavoritesTab() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FavoritesChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoritesChangeListener");
        }
    }

    public interface FavoritesChangeListener {
        public void setFavoriteTabResultsNum(int numOfResults);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.favorites_tab, container, false);
        Map favoritesMap = null;
        if (PreferencesUtils.getSharedPreferencesByFileName(getResources().getString(R.string.favorites), getContext()) != null) {
            favoritesMap = PreferencesUtils.getSharedPreferencesByFileName(
                    getResources().getString(R.string.favorites_file_name), getContext()).getAll();
        }
        mCallback.setFavoriteTabResultsNum(favoritesMap.size());
        return rootView;
    }

    public void setRecyclerViewAdapter() {
        if (getView() != null) {
            mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
            HashMap<String, Set<String>> favorites = (HashMap<String, Set<String>>) PreferencesUtils.getSharedPreferencesByFileName(
                    "favorites", getContext()).getAll();
            mAdapter = new FavoritesRecyclerViewAdapter(getContext(), favorites, mCallback);
            LinearLayoutManager manager = new LinearLayoutManager(getView().getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(
                    new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

    }
}