package technion.com.testapplication.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.PsukimListDialog;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ViewPagerAdapter;
import technion.com.testapplication.async.FetchParashotAndPrakimTask;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.fragments.PsukimTab;

public class MainActivity extends AppCompatActivity
        implements PsukimTab.OnMoveToMekorotTabListener,
        MekorotTab.MekorotChangesListener {

    private ArrayList<String> mParashotAndUris;
    private ArrayList<String> mPrakimAndUris;
    private ArrayList<String> mAdapterArrayList = new ArrayList<>();
    private final ArrayList<String> mParashot = new ArrayList<>();
    private final ArrayList<String> mPrakim = new ArrayList<>();
    private ArrayList<Pair<String, String>> parashotURILabelPairs = new ArrayList<>();
    private ArrayList<Pair<String, String>> prakimURILabelPairs = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private int mSpinnerCheck = 0;
    private static final String HEADING_FONT_PATH = "fonts/shofarregular-webfont.ttf";
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private static final int PSUKIM_FRAG_POSITION = 0;
    private static final int MEKOROT_FRAG_POSITION = 1;
    private static boolean mIsNewQuerySubmitted = false;
    private PsukimTab mPsukimTab;
    private MekorotTab mMekorotTab;
    private FloatingActionButton mFab;
    private ActionMenuView amvMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivityIntent = new Intent(getApplicationContext(),
                        SettingsActivity.class);
                startActivity(settingsActivityIntent);
                return true;
            case R.id.action_favorite:
                return false;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    /**
     * Set the spinner for this activity.
     * Spinner will have two values:
     * 1) Perek.
     * 2) Parasha.
     * Each choice will trigger invalidation of the autocomplete text view adapter.
     */
    public void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        spinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.White),
                PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout,
                getResources().getStringArray(R.array.spinner_items));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(
                        R.id.autoCompleteTextView);
                if (((TextView) view).getText().equals(getResources().getString(R.string.perek))) {
                    actv.setHint(getResources().getString(R.string.enter_perek));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty()) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mPrakim);
                    mAdapter.notifyDataSetChanged();
                } else {
                    actv.setHint(getResources().getString(R.string.enter_parasha));
                    if (++mSpinnerCheck > 1 && !mAdapter.isEmpty()) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(mParashot);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Set fonts for the title and other elements in the activity.
     */
    public void setHeadingFonts() {
        TextView tx = (TextView) findViewById(R.id.heading);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), HEADING_FONT_PATH);
        tx.setTypeface(custom_font);
    }

    public void setFilterIconClickable(boolean isClickable) {
        ImageView filterIcon = (ImageView) findViewById(R.id.filter_icon);
        if (!isClickable) {
            filterIcon.setColorFilter(R.color.FilterIconUnavailable);
            filterIcon.setEnabled(false);
        } else {
            filterIcon.setColorFilter(null);
            filterIcon.setEnabled(true);
        }
    }

    /**
     * Set toolbar for this activity.
     * This will also set the spinner.
     */
    public void setToolbar() {
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        amvMenu = (ActionMenuView) myToolbar.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setFilterIconClickable(false);
    }

    /**
     * Set the tabs for the view pager.
     */
    public void setTabs() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPsukimTab = new PsukimTab();
        mMekorotTab = new MekorotTab();
        mViewPagerAdapter.addFragment(mPsukimTab, getResources().getString(R.string.psukim));
        mViewPagerAdapter.addFragment(mMekorotTab, getResources().getString(R.string.mekorot));
        mViewPager.setAdapter(mViewPagerAdapter);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsukimListDialog psukimListDialog = new PsukimListDialog(MainActivity.this, mPrakim,
                        mParashot, parashotURILabelPairs, prakimURILabelPairs, mViewPagerAdapter,
                        MainActivity.this);
                psukimListDialog.show();
                setTabResultsNum(0);
                mIsNewQuerySubmitted = true;
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    //Psukim tab
                    setFilterIconClickable(false);
                    mFab.show();
                } else if (tab.getPosition() == 1) {
                    //Mekorot tab
                    PsukimTab psukimTabFrag = (PsukimTab) mViewPagerAdapter.getItem(
                            PSUKIM_FRAG_POSITION);
                    psukimTabFrag.moveToMekorot();
                    mFab.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager()
                .putFragment(outState, PsukimTab.class.getName(), mPsukimTab);
        getSupportFragmentManager()
                .putFragment(outState, MekorotTab.class.getName(), mMekorotTab);
    }

    /**
     * Used in order to hide the keyboard.
     *
     * @param activity
     * @param view
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Sets the autocomplete text view and listens to item clicks
     * in order to start a new activity.
     */
    public void setAutoCompleteTextView() {
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, mAdapterArrayList);
        //TODO: change adapter in case of prakim.
        actv.setAdapter(mAdapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String spinnerText = ((Spinner) findViewById(
                        R.id.spinner_nav)).getSelectedItem().toString();
                Intent intent = new Intent(getApplicationContext(), PsukimActivity.class);
                String perekOrParashaName = (String) ((TextView) view).getText();
                String perekOrParashaUri = "";
                ArrayList<Pair<String, String>> perekOrParashaPairs;
                if (spinnerText.equals(getResources().getString(R.string.parasha))) {
                    perekOrParashaPairs = parashotURILabelPairs;
                } else {
                    perekOrParashaPairs = prakimURILabelPairs;
                }
                for (Pair<String, String> uriLabel : perekOrParashaPairs) {
                    if (uriLabel.first.equals(perekOrParashaName)) {
                        perekOrParashaUri = uriLabel.second;
                    }
                }
                intent.putExtra(getResources().getString(R.string.perek_or_parasha_name_extra),
                        perekOrParashaName);
                intent.putExtra(getResources().getString(R.string.perek_or_parasha_uri_extra),
                        perekOrParashaUri);
                PsukimTab psukimTabFrag = (PsukimTab) mViewPagerAdapter.getItem(
                        PSUKIM_FRAG_POSITION);
                perekOrParashaUri = perekOrParashaUri.substring(
                        perekOrParashaUri.lastIndexOf("/") + 1);
                TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
                TabLayout.Tab tab = tabs.getTabAt(PSUKIM_FRAG_POSITION);
                if (tab != null) {
                    tab.select();
                    setTabResultsNum(0);
                }
                psukimTabFrag.loadPuskim(perekOrParashaUri, perekOrParashaName);
                hideSoftKeyboard(MainActivity.this,
                        view);
                mIsNewQuerySubmitted = true;
            }
        });
    }

    /**
     * Populate prakim and parashot pairs.
     */
    private void populateUriLabelPairs() {
        for (String queryResult : mParashotAndUris) {
            int firstMagicIndex = queryResult.indexOf(FetchParashotAndPrakimTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mParashot.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            parashotURILabelPairs.add(new Pair(label, uri));
        }
        for (String queryResult : mPrakimAndUris) {
            int firstMagicIndex = queryResult.indexOf(FetchParashotAndPrakimTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mPrakim.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            prakimURILabelPairs.add(new Pair(label, uri));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);
        // Get Parashot
        Intent intent = getIntent();
        mParashotAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.parashot_and_uri_extra));
        mPrakimAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.prakim_and_uri_extra));
        populateUriLabelPairs();
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MainActivityBG));
        forceRTLIfSupported();
        setToolbar();
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        spinner.setVisibility(View.GONE);
        actv.setVisibility(View.GONE);
//        setAutoCompleteTextView();
        setTabs();
    }

    /**
     * Used in order to move from the current psukim activity to the mekorot activity.
     * This is called from the psukim fragment, after the psukim fragment is called by the
     * main activity's on tab change listener (after we move to the mekorot fragment)
     *
     * @param psukimUris
     */
    @Override
    public void onMoveToMekorotTab(ArrayList<String> psukimUris) {
        MekorotTab mekorotTabFrag = (MekorotTab) mViewPagerAdapter.getItem(
                MEKOROT_FRAG_POSITION);
        if (mIsNewQuerySubmitted) {
            mekorotTabFrag.runMekorotAndCategoriesQueries(psukimUris);
            mIsNewQuerySubmitted = false;
            setFilterIconClickable(true);
        } else {
            if (psukimUris.size() == 0) {
                mekorotTabFrag.clear();
                setTabResultsNum(0);
                setFilterIconClickable(false);
            } else {
                setFilterIconClickable(true);
            }
        }
    }

    @Override
    public void onPsukimSelected(boolean areNewSelected) {
        mIsNewQuerySubmitted = areNewSelected;
    }

    @Override
    public void setFilterIcon(final Dialog dialog) {
        View filterIcon = findViewById(R.id.filter_icon);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    @Override
    public void setTabResultsNum(int numOfResults) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab mekorotTab = tabLayout.getTabAt(1);
        if (mekorotTab != null) {
            if (numOfResults == 0) {
                mekorotTab.setText(getResources().getString(R.string.mekorot));
            } else {
                mekorotTab.setText(getResources().getString(R.string.mekorot) + " (" + Integer.toString(
                        numOfResults) + ")");
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
        } else {
            finish();
        }

    }
}
