package technion.com.testapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import technion.com.testapplication.utils.IndexWrapper;
import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.utils.WholeWordIndexFinder;
import technion.com.testapplication.async.FetchHighlightsForMakorTask;
import technion.com.testapplication.utils.FontUtils;

/**
 * Created by tomerlevinson on 23/12/2017.
 */
public class MakorDetailView extends AppCompatActivity {
    private String mMakorTitle;
    private String mMakorAuthor;
    private String mMakorText;
    private String mMakorUri;
    private ArrayList<String> mMakorPsukim;
    private ArrayList<Integer> mScrollToList;
    private int mClickedIndex = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view_menu, menu);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_info:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void highlightPsukim(ArrayList<String> psukimSubstrings) {
        TextView makorTextView = (TextView) findViewById(R.id.makor_text);
        String makorText = makorTextView.getText().toString();
        String[] splitMakorText = makorText.split("\\s+");
        SpannableString spannableMakorText = new SpannableString(makorText);
        mScrollToList = new ArrayList<>();
        for (String subsetToHighlight : psukimSubstrings) {
            String[] splitSubset = subsetToHighlight.split("-");
            int startWord = Integer.parseInt(splitSubset[0]);
            int endWord = Integer.parseInt(splitSubset[1]);
            String keyword = "";
            for (int i = startWord; i <= endWord; i++) {
                if (i == endWord) {
                    keyword += splitMakorText[i];
                } else {
                    keyword += splitMakorText[i] + " ";
                }
            }
            List<IndexWrapper> indicesList = (new WholeWordIndexFinder(
                    makorText)).findIndexesForKeyword(keyword);
            for (IndexWrapper indexWrapper : indicesList) {
                int lineNum = makorTextView.getLayout().getLineForOffset(indexWrapper.getStart());
                mScrollToList.add(lineNum);
            }

            for (IndexWrapper indexWrapper : indicesList) {
                spannableMakorText.setSpan(new BackgroundColorSpan(
                                ContextCompat.getColor(getApplicationContext(), R.color.Highlight)),
                        indexWrapper.getStart(), indexWrapper.getEnd(),
                        0);
            }
        }
        Set<Integer> hs = new HashSet<>();
        hs.addAll(mScrollToList);
        mScrollToList.clear();
        mScrollToList.addAll(hs);
        Collections.sort(mScrollToList);
        makorTextView.setText(spannableMakorText);
        Button nextButton = (Button) findViewById(R.id.next);
        Button previousButton = (Button) findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedIndex > 0) {
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = (TextView) findViewById(R.id.makor_text);
                            int y = makorTextView.getLayout().getLineTop(
                                    mScrollToList.get(mClickedIndex));
                            scrollView.scrollTo(0, y);
                            mClickedIndex--;
                        }
                    });
                } else {
                    mClickedIndex = mScrollToList.size() - 1;
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = (TextView) findViewById(R.id.makor_text);
                            int y = makorTextView.getLayout().getLineTop(
                                    mScrollToList.get(mClickedIndex));
                            scrollView.scrollTo(0, y);
                            mClickedIndex--;
                        }
                    });
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedIndex < mScrollToList.size()) {
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = (TextView) findViewById(R.id.makor_text);
                            int y = makorTextView.getLayout().getLineTop(
                                    mScrollToList.get(mClickedIndex));
                            scrollView.scrollTo(0, y);
                            mClickedIndex++;
                        }
                    });
                } else {
                    mClickedIndex = 0;
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = (TextView) findViewById(R.id.makor_text);
                            int y = makorTextView.getLayout().getLineTop(
                                    mScrollToList.get(mClickedIndex));
                            scrollView.scrollTo(0, y);
                            mClickedIndex++;
                        }
                    });
                }
            }
        });
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
                ContextCompat.getColor(this, R.color.MakorDetailViewBG));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);

        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());

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
