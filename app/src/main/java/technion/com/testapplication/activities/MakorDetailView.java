package technion.com.testapplication.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.utils.FontUtils;
import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchHighlightsForMakorTask;

/**
 * Created by tomerlevinson on 23/12/2017.
 */
public class MakorDetailView extends AppCompatActivity {
    private String mMakorTitle;
    private String mMakorAuthor;
    private String mMakorText;
    private String mMakorUri;
    private ArrayList<String> mMakorPsukim;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view_menu, menu);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent settingsActivityIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivityIntent);
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.action_share:
                return true;

            case R.id.action_info:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void highlightPsukim(ArrayList<String> psukimSubstrings) {
        TextView makorTextView = (TextView) findViewById(R.id.makor_text);
        String makorText = makorTextView.getText().toString();
        String[] splitMakorText = makorText.split("\\s+");
        SpannableString spannableMakorText = new SpannableString(makorText);
        for (String subsetToHighlight : psukimSubstrings) {
            String[] splitSubset = subsetToHighlight.split("-");
            int startWord = Integer.parseInt(splitSubset[0]);
            int endWord = Integer.parseInt(splitSubset[1]);
            int wordCount = 0;
            int startIndex = 0;
            while (wordCount < startWord) {
                startIndex += splitMakorText[wordCount].length() + 1;
                wordCount++;
            }
            wordCount = 0;
            int endIndex = startIndex;
            while (wordCount <= (endWord - startWord)) {
                endIndex += splitMakorText[startWord + wordCount].length() + 1;
                wordCount++;
            }
            spannableMakorText.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex,
                    0);
        }
        makorTextView.setText(spannableMakorText);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makor_detail_view);
        Intent receivedIntent = getIntent();
        mMakorText = receivedIntent.getStringExtra(getResources().getString(R.string.makor_text));
        mMakorAuthor = receivedIntent.getStringExtra(
                getResources().getString(R.string.makor_author));
        mMakorTitle = receivedIntent.getStringExtra(getResources().getString(R.string.makor_title));
        mMakorUri = receivedIntent.getStringExtra(getResources().getString(R.string.makor_uri));
        mMakorUri = mMakorUri.substring(mMakorUri.lastIndexOf("/") + 1);
        mMakorUri = getResources().getString(R.string.jbr_prefix) + mMakorUri;
        mMakorPsukim = (ArrayList<String>) receivedIntent.getExtras().get(
                getResources().getString(R.string.psukim_uris_extra));
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);

        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());

        makorText.setMovementMethod(new ScrollingMovementMethod());
        String fetchHighlightsForMakor = JBSQueries.getPsukimToHighlightFromMakor(mMakorUri,
                mMakorPsukim);
        FetchHighlightsForMakorTask fetchHighlightsForMakorTask = new FetchHighlightsForMakorTask(
                this, MakorDetailView.this);
        fetchHighlightsForMakorTask.execute(fetchHighlightsForMakor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());
    }
}
