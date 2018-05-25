package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.FavoritesRecyclerViewAdapter;
import technion.com.testapplication.utils.PreferencesUtils;

public class FavoritesActivity extends AppCompatActivity {

    private static final String MAKOR_URI_DELIMITER = "/";
    private RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoritesViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MakorDetailViewBG));
        setRecyclerViewAdapter();
        setToolbar();
    }

    public void setRecyclerViewAdapter() {
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        HashMap<String, Set<String>> favorites = (HashMap<String, Set<String>>) PreferencesUtils.getSharedPreferencesByFileName(
                "favorites", this).getAll();
        mAdapter = new FavoritesRecyclerViewAdapter(this, favorites);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        setShareDialog();
    }

    private void setToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar_fav);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        View goBack = findViewById(R.id.go_back);
        final FavoritesActivity selfie = this;
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfie.onBackPressed();
            }
        });
        TextView toolbarTitleTV = findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(getString(R.string.title_activity_favorites));
    }

    public void onBackPressed() {
        finish();
    }

    private void setShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] options = new String[]{getResources().getString(
                R.string.full_text_share_option), getResources().getString(
                R.string.link_to_text_share_option)};
        int selectedFont = 0;
        builder.setSingleChoiceItems(options, selectedFont, null);
        builder.setCancelable(true);
        builder.setTitle(this.getResources().getString(
                R.string.choose_sharing_option));
        builder.setPositiveButton(
                this.getResources().getString(R.string.choose_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition == 0)
                        {
                            startActivity(createShareIntent(true));
                        }
                        else
                        {
                            startActivity(createShareIntent(false));
                        }
                    }
                });

        builder.setNegativeButton(
                this.getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

    private Intent createShareIntent(boolean fullText) {
        List<Pair<String, Pair<String, String>>> favoritesPairs = ((FavoritesRecyclerViewAdapter) mAdapter).getFavoritesPairs();
        String allMekorotTextsAndTitles = "";
        String allMekorotUris = "";
        for (Pair<String, Pair<String, String>> triplet : favoritesPairs)
        {
            String makorUri = triplet.first;
            Pair<String, String> pair = triplet.second;
            String makorTitle = pair.first;
            String makorText = pair.second;
            allMekorotTextsAndTitles += makorTitle + "\n\n" + makorText + "\n\n";
            makorUri = makorUri.substring(makorUri.lastIndexOf(MAKOR_URI_DELIMITER) + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            allMekorotUris += JBSQueries.READ_URL + makorUri + "\n\n";
        }
        Intent mSharedIntent = new Intent(Intent.ACTION_SEND);
        mSharedIntent.setType("text/plain");
        if (fullText)
        {
            mSharedIntent.putExtra(Intent.EXTRA_TEXT, allMekorotTextsAndTitles);
        }
        else
        {
            mSharedIntent.putExtra(Intent.EXTRA_TEXT,
                                   allMekorotUris);
        }
        return mSharedIntent;
    }

    public interface FavoritesChangeListener {
        void updateFavoritesNum(int numOfFavorites);
    }
}
