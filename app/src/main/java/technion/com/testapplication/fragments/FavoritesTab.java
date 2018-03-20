package technion.com.testapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import technion.com.testapplication.JBSQueries;
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
    private Intent mSharedIntent;
    private static final String MAKOR_URI_DELIMITER = "/";

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

    /**
     * Used in order to communicate with the main activity upon changes in tab number of results.
     */
    public interface FavoritesChangeListener {
        public void setFavoriteTabResultsNum(int numOfResults);
        public void setShareIcon(Dialog dialog);
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

    private Intent createShareIntent(boolean fullText) {
        List<Pair<String, Pair<String, String>>> favoritesPairs =  ((FavoritesRecyclerViewAdapter) mAdapter).getFavoritesPairs();
        String allMekorotTextsAndTitles = "";
        String allMekorotUris = "";
        for (Pair<String, Pair<String, String>> triplet: favoritesPairs) {
            String makorUri = triplet.first;
            Pair<String, String> pair = triplet.second;
            String makorTitle = pair.first;
            String makorText = pair.second;
            allMekorotTextsAndTitles += makorTitle + "\n\n" + makorText + "\n\n";
            makorUri = makorUri.substring(makorUri.lastIndexOf(MAKOR_URI_DELIMITER) + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            allMekorotUris+= JBSQueries.READ_URL + makorUri + "\n\n";
        }
        mSharedIntent = new Intent(Intent.ACTION_SEND);
        mSharedIntent.setType("text/plain");
        if (fullText) {
            mSharedIntent.putExtra(Intent.EXTRA_TEXT, allMekorotTextsAndTitles);
        } else {
            mSharedIntent.putExtra(Intent.EXTRA_TEXT,
                    allMekorotUris);
        }
        return mSharedIntent;
    }

    /**
     * Sets the sharing dialog for all the favorites.
     */
    private void setShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] options = new String[]{getResources().getString(
                R.string.full_text_share_option), getResources().getString(
                R.string.link_to_text_share_option)};
        int selectedFont = 0;
        builder.setSingleChoiceItems(options, selectedFont, null);
        builder.setCancelable(true);
        builder.setTitle(getContext().getResources().getString(
                R.string.choose_sharing_option));
        builder.setPositiveButton(
                getContext().getResources().getString(R.string.choose_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0) {
                            startActivity(createShareIntent(true));
                        } else {
                            startActivity(createShareIntent(false));
                        }
                    }
                });

        builder.setNegativeButton(
                getContext().getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        mCallback.setShareIcon(dialog);
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
            setShareDialog();
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