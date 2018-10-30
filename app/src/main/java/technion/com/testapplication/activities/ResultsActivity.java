package technion.com.testapplication.activities;

import android.app.ProgressDialog;
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
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ResultsCollectionPagerAdapter;
import technion.com.testapplication.async.FetchHighlightsForMakorTask;
import technion.com.testapplication.async.PostRequester;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.models.ErrorModel;
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
    private ProgressDialog mProgressDialog;
    private ArrayList<Integer> mScrollToList;
    private android.app.AlertDialog alert;
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

        final ResultsActivity selfie = this;
        mViewPager = findViewById(R.id.pager_results);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                selfie.unsetFab();
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

    private void unsetFab() {
        FloatingActionButton fab = findViewById(R.id.fab_next);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setFab() {
        FloatingActionButton fab = findViewById(R.id.fab_next);
        final ResultsActivity selfie = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScrollToList.size() == 0)
                {
                    return;
                }
                if (mClickedIndex < mScrollToList.size())
                {
                    final ResultsCollectionPagerAdapter.ResultObjectFragment fragment =
                            (ResultsCollectionPagerAdapter.ResultObjectFragment) selfie.mResultsCollectionPagerAdapter.getCurrentFragment();
                    final ScrollView scrollView = fragment.MakorScrollView;

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView makorTextView = fragment.MakorTextView;
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
        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
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
                onReportErrorClicked(mResults.get(mCurrentResult).URI, "", "", true);
                return true;
            default:
                return false;
        }
    }

    private void onShareClick() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ResultsActivity.this);
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
                        int selectedPosition = ((android.app.AlertDialog) dialog).getListView().getCheckedItemPosition();
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

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Intent createShareIntent(boolean fullText) {
        Intent mShareIntent = new Intent(Intent.ACTION_SEND);
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
                partialPasukText.append(splitMakorText[i]).append(" ");
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

    private void reportError(ErrorModel error) {
        final ResultsActivity selfie = this;
        Map<String, String> params = new HashMap<>();
        params.put("makor_uri", error.makorUri);
        params.put("makor_range", error.makorRange);
        params.put("issue_text", error.issueText);
        params.put("free_text", error.freeText);
        params.put("report_type", error.reportType.toString());

        Runnable onResponse = new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(selfie, "דיווח נשלח בהצלחה", Toast.LENGTH_SHORT).show();
            }
        };

        Runnable onError = new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "דיווח שגיאה נכשל", Toast.LENGTH_SHORT).show();
            }
        };

        PostRequester postRequester = new PostRequester(this);
        postRequester.SendRequest("http://haggaidev.com/mekorot/html/error_report.php", params, onResponse, onError);
    }

    public void onReportErrorClicked(final String makorUri, final String makorRange, final String issueText, boolean isFreeTextOnly) {
        final ResultsActivity selfie = this;
        mProgressDialog = new ProgressDialog(selfie);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View errorReportView = inflater.inflate(R.layout.alert_report_error, null);
        TextView txtMsg = errorReportView.findViewById(R.id.txt_error_report_msg);
        final RadioGroup radioGroup = errorReportView.findViewById(R.id.rad_err_report_group);
        final Button btnSend = errorReportView.findViewById(R.id.btn_err_report_send);
        final ErrorModel errorModel = new ErrorModel(makorUri, makorRange, issueText, "", ErrorModel.ReportType.FREE_TEXT);

        btnSend.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnSend.setEnabled(true);
                switch (checkedId)
                {
                    case R.id.rad_bad_ident:
                        errorModel.reportType = ErrorModel.ReportType.BAD_IDENTIFICATION;
                        break;
                    case R.id.rad_part_ident:
                        errorModel.reportType = ErrorModel.ReportType.PARTIAL_IDENTIFICATION;
                        break;
                }
            }
        });

        EditText errRepTxt = errorReportView.findViewById(R.id.err_rep_free_text);
        errRepTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (count > 0)
                {
                    btnSend.setEnabled(true);
                }
                else
                {
                    if (radioGroup.getCheckedRadioButtonId() == -1)
                    {
                        btnSend.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (isFreeTextOnly)
        {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
        }
        txtMsg.setText("דיווח שגיאה");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String freeText = ((TextView) errorReportView.findViewById(R.id.err_rep_free_text)).getText().toString();
                errorModel.issueText = issueText;
                errorModel.freeText = freeText;
                selfie.mProgressDialog.setMessage(
                        selfie.getResources().getString(R.string.please_wait_he));
                selfie.mProgressDialog.show();
                selfie.reportError(errorModel);
                alert.dismiss();
            }
        });

        builder.setView(errorReportView);
        alert = builder.create();
        alert.show();
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
        final ResultsCollectionPagerAdapter.ResultObjectFragment fragment =
                (ResultsCollectionPagerAdapter.ResultObjectFragment) mResultsCollectionPagerAdapter.getRegisteredFragment(mCurrentResult);
        final TextView makorTextView = fragment.MakorTextView;
        final String makorText = makorTextView.getText().toString();
        String[] splitMakorText = makorText.split(SPLIT_BY_SPACES_REGEX);
        SpannableString spannableMakorText = new SpannableString(makorText);
        final ResultsActivity selfie = this;
        mScrollToList = new ArrayList<>();
        mClickedIndex = 0;
        final String makorUri = mResults.get(mCurrentResult).URI;

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
                final String spanRange = String.valueOf(indexWrapper.getStart()) + "-" + String.valueOf(indexWrapper.getEnd());
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        selfie.onReportErrorClicked(makorUri, spanRange, subsetToHighlight.second, false);
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
        setFab();
        makorTextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.removeItem(android.R.id.selectAll);
                // Remove the "cut" option
                menu.removeItem(android.R.id.cut);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                menu.add(0, 0, 0, "דווח אי-זיהוי").setIcon(R.drawable.ic_error);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId())
                {
                    case 0:
                        int min = 0;
                        int max = makorTextView.getText().length();
                        if (makorTextView.isFocused())
                        {
                            final int selStart = makorTextView.getSelectionStart();
                            final int selEnd = makorTextView.getSelectionEnd();

                            min = Math.max(0, Math.min(selStart, selEnd));
                            max = Math.max(0, Math.max(selStart, selEnd));
                        }
                        final int finalMin = min;
                        final int finalMax = max;
                        // Perform your definition lookup with the selected text
                        final CharSequence selectedText = makorTextView.getText().subSequence(min, max);
                        AlertDialog.Builder builder = new AlertDialog.Builder(selfie);
                        builder.setCancelable(true);
                        builder.setTitle("דיווח שגיאה");
                        builder.setMessage("\"" + selectedText + "\"");
                        builder.setPositiveButton(
                                selfie.getApplicationContext().getResources().getString(R.string.choose_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selfie.onReportErrorClicked(makorUri, String.format("%s-%s", finalMin, finalMax),
                                                                    selectedText.toString(), false);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // Finish and close the ActionMode
                        mode.finish();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
