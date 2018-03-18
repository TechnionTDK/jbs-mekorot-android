package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.async.FetchHighlightsForMakorTask;
import technion.com.testapplication.utils.FontUtils;
import technion.com.testapplication.utils.IndexWrapper;
import technion.com.testapplication.utils.WholeWordIndexFinder;

/**
 * Created by tomerlevinson on 23/12/2017.
 * Shows the chosen Makor from the makor tab.
 */
public class MakorDetailView extends AppCompatActivity {
    private String mMakorTitle;
    private String mMakorAuthor;
    private String mMakorText;
    private String mMakorUri;
    private ArrayList<String> mMakorPsukim;
    private ArrayList<Integer> mScrollToList;
    private int mClickedIndex = 0;
    private Intent mShareIntent;
    private ActionMenuView amvMenu;
    private static final String INDICES_DELIMITER = "-";
    private static final String SPLIT_BY_SPACES_REGEX = "\\s+";
    private static final String MAKOR_URI_DELIMITER = "/";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view_menu, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                AlertDialog.Builder builder = new AlertDialog.Builder(MakorDetailView.this);
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
                                if (selectedPosition == 0) {
                                    startActivity(createShareIntent(true));
                                } else {
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
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
        String partialPasukText = "";
        for (int i = startWord; i <= endWord; i++) {
            if (i == endWord) {
                partialPasukText += splitMakorText[i];
            } else {
                partialPasukText += splitMakorText[i] + " ";
            }
        }
        return partialPasukText;
    }

    /**
     * Removes duplicates from the scroll list and sorts it.
     */
    private void removeDuplicatesAndSortScrollList() {
        Set<Integer> hs = new HashSet<>();
        // Remove duplicates from scroll list and sort it.
        hs.addAll(mScrollToList);
        mScrollToList.clear();
        mScrollToList.addAll(hs);
        Collections.sort(mScrollToList);
    }

    private Intent createShareIntent(boolean fullText) {
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        if (fullText) {
            mShareIntent.putExtra(Intent.EXTRA_TITLE, mMakorTitle);
            mShareIntent.putExtra(Intent.EXTRA_TEXT, mMakorText);
        } else {
            String makorUri = mMakorUri;
            makorUri = makorUri.substring(makorUri.lastIndexOf(MAKOR_URI_DELIMITER) + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            mShareIntent.putExtra(Intent.EXTRA_TEXT,
                    JBSQueries.READ_URL + makorUri);
        }
        return mShareIntent;
    }

    public View getActionBarView() {
        Window window = getWindow();
        View v = window.getDecorView();
        int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
    }

    /**
     * Sets the next and prev buttons for the highlights.
     */
    private void setPrevNextButtons() {
        if (getWindow().getDecorView() != null) {
            View prevAction = getWindow().getDecorView().findViewById(R.id.action_prev);
            View nextAction = getWindow().getDecorView().findViewById(R.id.action_next);
            prevAction.setOnClickListener(new View.OnClickListener() {
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
            nextAction.setOnClickListener(new View.OnClickListener() {
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
    }

    /**
     * Sets the background color and toolbar options.
     */
    private void setToolbarAndColors() {
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MakorDetailViewBG));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        amvMenu = (ActionMenuView) myToolbar.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
    }

    /**
     * Sets makor text according to chosen settings.
     */
    private void setMakorText() {
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);

        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());
    }

    /**
     * Executes the get highlights for makor query.
     */
    private void executeGetHighlightsForMakorQuery() {
        String fetchHighlightsForMakor = JBSQueries.getPsukimToHighlightFromMakor(mMakorUri,
                mMakorPsukim);
        FetchHighlightsForMakorTask fetchHighlightsForMakorTask = new FetchHighlightsForMakorTask(
                this, MakorDetailView.this);
        fetchHighlightsForMakorTask.execute(fetchHighlightsForMakor);
    }

    /**
     * 1) Go over each index range in the psukimSubstrings.
     * 2) Highlight each index-range.
     * 3) Calculate scroll position for next-prev buttons.
     *
     * @param psukimSubstrings - psukim substrings of the following format: ["X-Y", "A-B",..]
     *                         For example: ["1-4", "50-65",...]
     */
    public void highlightPsukim(ArrayList<String> psukimSubstrings) {
        TextView makorTextView = (TextView) findViewById(R.id.makor_text);
        String makorText = makorTextView.getText().toString();
        String[] splitMakorText = makorText.split(SPLIT_BY_SPACES_REGEX);
        SpannableString spannableMakorText = new SpannableString(makorText);
        mScrollToList = new ArrayList<>();
        for (String subsetToHighlight : psukimSubstrings) {
            String successivePsukim = calculatePasukReferenceFromGivenIndices(subsetToHighlight,
                    splitMakorText);

            // Find all indices inside makorText for the given psukim String.
            List<IndexWrapper> indicesList = (new WholeWordIndexFinder(
                    makorText)).findIndexesForKeyword(successivePsukim);

            // Calculate for each indicesList the scroll position.
            for (IndexWrapper indexWrapper : indicesList) {
                int lineNum = makorTextView.getLayout().getLineForOffset(indexWrapper.getStart());
                mScrollToList.add(lineNum);
            }

            // Highlight each indicesList members.
            for (IndexWrapper indexWrapper : indicesList) {
                spannableMakorText.setSpan(new BackgroundColorSpan(
                                ContextCompat.getColor(getApplicationContext(), R.color.Highlight)),
                        indexWrapper.getStart(), indexWrapper.getEnd(),
                        0);
            }
        }
        removeDuplicatesAndSortScrollList();

        // Set highlights in makor text
        makorTextView.setText(spannableMakorText);
        setPrevNextButtons();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makor_detail_view);
        Intent receivedIntent = getIntent();
        // Get extras from intent.
        mMakorText = receivedIntent.getStringExtra(getResources().getString(R.string.makor_text));
        mMakorAuthor = receivedIntent.getStringExtra(
                getResources().getString(R.string.makor_author));
        mMakorTitle = receivedIntent.getStringExtra(getResources().getString(R.string.makor_title));
        mMakorUri = receivedIntent.getStringExtra(getResources().getString(R.string.makor_uri));
        mMakorUri = mMakorUri.substring(mMakorUri.lastIndexOf("/") + 1);
        mMakorUri = getResources().getString(R.string.jbr_prefix) + mMakorUri;
        mMakorPsukim = (ArrayList<String>) receivedIntent.getExtras().get(
                getResources().getString(R.string.psukim_uris_extra));

        setToolbarAndColors();
        setMakorText();
        executeGetHighlightsForMakorQuery();
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
