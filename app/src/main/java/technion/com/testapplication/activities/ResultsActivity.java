package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ResultsCollectionPagerAdapter;
import technion.com.testapplication.async.FetchHighlightsForMakorTask;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.models.MakorModel;
import technion.com.testapplication.models.ResultModel;
import technion.com.testapplication.utils.IndexWrapper;
import technion.com.testapplication.utils.WholeWordIndexFinder;

public class ResultsActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener {
    public static final String EXTRA_MAKOR_INDEX = "extra_makor_index";
    public static final String EXTRA_PSUKIM_URIS = "extra_psukim_uris";
    private ArrayList<ResultModel> mResults;
    private ArrayList<String> mPsukimUris;
    private int mCurrentResult = 0;
    private int mClickedIndex = 0;
    private ArrayList<Integer> mScrollToList;
    private Intent mShareIntent;
    private static final String INDICES_DELIMITER = "-";
    private static final String SPLIT_BY_SPACES_REGEX = "\\s+";
    private static final String MAKOR_URI_DELIMITER = "/";
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    ResultsCollectionPagerAdapter mResultsCollectionPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        setToolbarAndColors();
        setFab();

        final ResultsActivity selfie = this;
        mViewPager = findViewById(R.id.pager_results);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                mClickedIndex = 0;
                selfie.setTitleByPosition(position);
                selfie.mCurrentResult = position;
                selfie.executeGetHighlightsForMakorQuery();
            }
        });
    }

    private void setTitleByPosition(int position) {
        final TextView titleView = findViewById(R.id.toolbar_title);
        titleView.setText(mResults.get(position).Title);
    }

    private void editTitleWithHighlights(int num) {
        final TextView titleView = findViewById(R.id.toolbar_title);
        titleView.setText(titleView.getText() + " (" + String.valueOf(num) + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResults = new ArrayList<>();
        for (MakorModel makor : MekorotTab.MekorotModels)
        {
            String makorUri = makor.getMakorUri();
            makorUri = makorUri.substring(makorUri.lastIndexOf("/") + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            mResults.add(new ResultModel(makor.getMakorText(), makor.getMakorName(), makorUri));
        }
        Intent intent = getIntent();
        mPsukimUris = intent.getStringArrayListExtra(EXTRA_PSUKIM_URIS);
        mResultsCollectionPagerAdapter = new ResultsCollectionPagerAdapter(getSupportFragmentManager(), mResults);
        mViewPager.setAdapter(mResultsCollectionPagerAdapter);
        mCurrentResult = intent.getIntExtra(EXTRA_MAKOR_INDEX, 0);
        mViewPager.setCurrentItem(mCurrentResult);
        setTitleByPosition(mCurrentResult);
        executeGetHighlightsForMakorQuery();
    }

    private void setFab() {
        FloatingActionButton fab = findViewById(R.id.fab_next);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedIndex < mScrollToList.size())
                {
                    final ScrollView scrollView = findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = findViewById(R.id.makor_text);
                            int num = makorTextView.getLayout().getLineCount();
                            int y = makorTextView.getLayout().getLineTop(
                                    mScrollToList.get(mClickedIndex));
                            scrollView.scrollTo(0, y);
                            mClickedIndex++;
                        }
                    });
                }
                else
                {
                    mClickedIndex = 0;
                    final ScrollView scrollView = findViewById(R.id.scroll_view);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = findViewById(R.id.makor_text);
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

    /**
     * Sets the background color and toolbar options.
     */
    private void setToolbarAndColors() {
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MakorDetailViewBG));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        View backButton = findViewById(R.id.go_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_single_result);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_share:
                onShareClick();
                return true;
            case R.id.action_report_error:
                return true;
            default:
                return false;
        }
    }

    private void onShareClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
        String[] options = new String[]{getResources().getString(
                R.string.full_text_share_option), getResources().getString(
                R.string.link_to_text_share_option)};
        int selectedFont = 0;
        builder.setSingleChoiceItems(options, selectedFont, null);
        builder.setCancelable(true);
        builder.setTitle(getApplicationContext().getResources().getString(
                R.string.choose_sharing_option));
        builder.setPositiveButton(
                getApplicationContext().getResources().getString(R.string.choose_button),
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
                getApplicationContext().getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Intent createShareIntent(boolean fullText) {
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        if (fullText)
        {
            mShareIntent.putExtra(Intent.EXTRA_TITLE, mResults.get(mCurrentResult).Title);
            mShareIntent.putExtra(Intent.EXTRA_TEXT, mResults.get(mCurrentResult).Text);
        }
        else
        {
            String makorUri = mResults.get(mCurrentResult).URI;
            makorUri = makorUri.substring(makorUri.lastIndexOf(MAKOR_URI_DELIMITER) + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            mShareIntent.putExtra(Intent.EXTRA_TEXT,
                                  JBSQueries.READ_URL + makorUri);
        }
        return mShareIntent;
    }

    /**
     * Executes the get highlights for makor query.
     */
    private void executeGetHighlightsForMakorQuery() {
        String fetchHighlightsForMakor = JBSQueries.getPsukimToHighlightFromMakor(mResults.get(mCurrentResult).URI,
                                                                                  mPsukimUris);
        FetchHighlightsForMakorTask fetchHighlightsForMakorTask = new FetchHighlightsForMakorTask(
                this, ResultsActivity.this);
        fetchHighlightsForMakorTask.execute(fetchHighlightsForMakor);
    }

    /**
     * Given an indices pair of the following format:
     * "X-Y" returns the words corresponding it from the splitMakorText.
     *
     * @param indicesPair    - String of the format "X-Y" that represents the indices of the words in the text.
     * @param splitMakorText - Makor array of words.
     * @return String represting successive words in the given text between the indices X and Y.
     */
    private String calculatePasukReferenceFromGivenIndices(String indicesPair,
                                                           String[] splitMakorText) {
        String[] splitSubset = indicesPair.split(INDICES_DELIMITER);
        int startWord = Integer.parseInt(splitSubset[0]);
        int endWord = Integer.parseInt(splitSubset[1]);
        StringBuilder partialPasukText = new StringBuilder("");
        for (int i = startWord; i <= endWord; i++)
        {
            if (i == endWord)
            {
                partialPasukText.append(splitMakorText[i]);
            }
            else
            {
                partialPasukText.append(splitMakorText[i] + " ");
            }
        }
        return partialPasukText.toString();
    }

    /**
     * Removes duplicates from the scroll list and sorts it.
     */
    private void removeDuplicatesAndSortScrollList() {
        // Remove duplicates from scroll list and sort it.
        Set<Integer> hs = new HashSet<>(mScrollToList);
        mScrollToList.clear();
        mScrollToList.addAll(hs);
        Collections.sort(mScrollToList);
    }

    /**
     * 1) Go over each index range in the psukimSubstrings.
     * 2) Highlight each index-range.
     * 3) Calculate scroll position for next-prev buttons.
     *
     * @param psukimSubstrings - A pair <span, pasuk_text>
     *                         span: psukim substrings of the following format: ["X-Y", "A-B",..]
     *                         For example: ["1-4", "50-65",...]
     */
    public void highlightPsukim(ArrayList<Pair<String, String>> psukimSubstrings) {
        editTitleWithHighlights(psukimSubstrings.size());
        ResultsCollectionPagerAdapter.ResultObjectFragment fragment =
                (ResultsCollectionPagerAdapter.ResultObjectFragment) mResultsCollectionPagerAdapter.getRegisteredFragment(mCurrentResult);
        TextView makorTextView = fragment.MakorTextView;
        String makorText = makorTextView.getText().toString();
        String[] splitMakorText = makorText.split(SPLIT_BY_SPACES_REGEX);
        SpannableString spannableMakorText = new SpannableString(makorText);
        final ResultsActivity selfie = this;
        mScrollToList = new ArrayList<>();
        for (final Pair<String, String> subsetToHighlight : psukimSubstrings)
        {
            String successivePsukim = calculatePasukReferenceFromGivenIndices(subsetToHighlight.first,
                                                                              splitMakorText);

            // Find all indices inside makorText for the given psukim String.
            List<IndexWrapper> indicesList = (new WholeWordIndexFinder(
                    makorText)).findIndexesForKeyword(successivePsukim);

            // Calculate for each indicesList the scroll position.
            for (IndexWrapper indexWrapper : indicesList)
            {
                int lineNum = makorTextView.getLayout().getLineForOffset(indexWrapper.getStart());
                mScrollToList.add(lineNum);
            }

            // Highlight each indicesList members.
            for (IndexWrapper indexWrapper : indicesList)
            {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        AlertDialog alert = new AlertDialog.Builder(selfie).create();
                        alert.setTitle("");
                        alert.setMessage(subsetToHighlight.second);
                        alert.show();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.MAGENTA);
                    }
                };
                spannableMakorText.setSpan(clickableSpan, indexWrapper.getStart(), indexWrapper.getEnd(),
                                           Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        removeDuplicatesAndSortScrollList();

        // Set highlights in makor text
        makorTextView.setText(spannableMakorText);
        makorTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
