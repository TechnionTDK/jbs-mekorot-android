package technion.com.testapplication.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ViewPagerAdapter;
import technion.com.testapplication.async.FetchParashotAndPrakimTask;
import technion.com.testapplication.dialogs.PrakimParashotListDialog;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.fragments.PsukimTab;
import technion.com.testapplication.utils.PreferencesUtils;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PsukimTab.OnMoveToMekorotTabListener,
        MekorotTab.MekorotChangesListener,
        FavoritesActivity.FavoritesChangeListener {

    private static final int PSUKIM_FRAG_POSITION = 0;
    private static final int MEKOROT_FRAG_POSITION = 1;
    private static final String SELECTED_TAB_INDICATOR_COLOR = "#000000";
    private static final int PSUKIM_TAB_INDEX = 0;
    private static final int MEKOROT_TAB_INDEX = 1;
    private static final int OFF_SCREEN_PAGE_LIMIT = 2;
    private static boolean mIsNewQuerySubmitted = false;
    private final ArrayList<String> mParashot = new ArrayList<>();
    private final ArrayList<String> mPrakim = new ArrayList<>();
    private ArrayList<String> mParashotAndUris;
    private ArrayList<String> mPrakimAndUris;
    private HashMap<String, ArrayList<String>> mBookToCategories;
    private HashMap<String, ArrayList<String>> mCategoryToBooks;
    private ArrayList<Pair<String, String>> parashotURILabelPairs = new ArrayList<>();
    private ArrayList<Pair<String, String>> prakimURILabelPairs = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private PsukimTab mPsukimTab;
    private MekorotTab mMekorotTab;
    private FloatingActionButton mFab;
    private NavigationView mNavView;

    // ##########################
    // ### Activity Methods #####
    // ##########################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            Intent intent = new Intent(NavActivity.this, NavActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        mParashotAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.parashot_and_uri_extra));
        mPrakimAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.prakim_and_uri_extra));
        mBookToCategories = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("bookToCategories");
        mCategoryToBooks = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("categoryToBooks");
        populateUriLabelPairs();
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MainActivityBG));
        setToolbar();
        setTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, Set<String>> favorites = (HashMap<String, Set<String>>) PreferencesUtils.getSharedPreferencesByFileName(
                "favorites", this).getAll();
        updateFavoritesNum(favorites.size());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager()
                .putFragment(outState, PsukimTab.class.getName(), mPsukimTab);
        getSupportFragmentManager()
                .putFragment(outState, MekorotTab.class.getName(), mMekorotTab);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (mViewPager.getCurrentItem() != 0)
        {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
        }
        else
        {
            finish();
        }
    }

    // #######################################
    // ##### Nav Drawer And Menu Methods #####
    // #######################################
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites)
        {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START))
            {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        else if (id == R.id.nav_settings)
        {
            Intent settingsActivityIntent = new Intent(getApplicationContext(),
                                                       SettingsActivity.class);
            startActivity(settingsActivityIntent);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START))
            {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        else if (id == R.id.nav_help)
        {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Set toolbar for this activity.
     * This will also set the spinner.
     */
    private void setToolbar() {
        final Toolbar myToolbar = findViewById(R.id.toolbar);
        ActionMenuView amvMenu = myToolbar.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setFilterIconClickable(false);
    }

    // ############################################
    // ##### Interface Methods Implementation #####
    // ############################################

    /**
     * Favorites tab communicates with the main activity via this method in order
     * to update the num of results shown in the tab.
     *
     * @param numOfFavorites
     */
    @Override
    public void updateFavoritesNum(int numOfFavorites) {
        String newTitle = getString(R.string.nav_item_favorites) + " (" + String.valueOf(numOfFavorites) + ")";
        mNavView.getMenu().getItem(0).setTitle(newTitle);
    }

    /**
     * Psukim tab communicates with main activity when psukim are selected
     * via this method.
     *
     * @param areNewSelected
     */
    @Override
    public void onPsukimSelected(boolean areNewSelected) {
        mIsNewQuerySubmitted = areNewSelected;
    }

    @Override
    public void setTitleBySpinnerText(String headingText) {
        TextView heading = findViewById(R.id.heading);
        heading.setText(headingText);
    }

    /**
     * Sets the onClick listener for the filter icon.
     *
     * @param dialog
     */
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
        if (mIsNewQuerySubmitted)
        {
            mekorotTabFrag.setBookToCategories(mBookToCategories);
            mekorotTabFrag.setCategoryToBooks(mCategoryToBooks);
            mekorotTabFrag.runMekorotAndCategoriesQueries(psukimUris);
            mIsNewQuerySubmitted = false;
            setFilterIconClickable(true);
            mekorotTabFrag.clearFilterDialogSelections();
        }
        else
        {
            if (psukimUris.size() == 0)
            {
                mekorotTabFrag.clear();
                setMekorotTabResultsNum(0);
                setFilterIconClickable(false);
            }
            else
            {
                setFilterIconClickable(true);
            }
        }
        mekorotTabFrag.notifyFromFavorites();
    }

    @Override
    public void setPsukimTabNumResults(int numResults) {
        TabLayout tabLayout = findViewById(R.id.tabs);
        TabLayout.Tab mekorotTab = tabLayout.getTabAt(0);
        if (mekorotTab != null)
        {
            if (numResults == 0)
            {
                mekorotTab.setText(getResources().getString(R.string.psukim));
            }
            else
            {
                mekorotTab.setText(
                        getResources().getString(R.string.psukim) + " (" + Integer.toString(
                                numResults) + ")");
            }
        }
    }

    /**
     * Sets the number of results to be seen at the mekorot tab.
     *
     * @param numOfResults
     */
    @Override
    public void setMekorotTabResultsNum(int numOfResults) {
        TabLayout tabLayout = findViewById(R.id.tabs);
        TabLayout.Tab mekorotTab = tabLayout.getTabAt(1);
        if (mekorotTab != null)
        {
            if (numOfResults == 0)
            {
                mekorotTab.setText(getResources().getString(R.string.mekorot));
            }
            else
            {
                mekorotTab.setText(
                        getResources().getString(R.string.mekorot) + " (" + Integer.toString(
                                numOfResults) + ")");
            }
        }
    }

    // ############################
    // ##### Internal Methods #####
    // ############################

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void setTabs() {
        mViewPager = findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPsukimTab = new PsukimTab();
        mMekorotTab = new MekorotTab();
        mViewPagerAdapter.addFragment(mPsukimTab, getResources().getString(R.string.psukim));
        mViewPagerAdapter.addFragment(mMekorotTab, getResources().getString(R.string.mekorot));
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrakimParashotListDialog prakimParashotListDialog = new PrakimParashotListDialog(
                        NavActivity.this, mPrakim,
                        mParashot, parashotURILabelPairs, prakimURILabelPairs, mViewPagerAdapter,
                        NavActivity.this);
                prakimParashotListDialog.show();
                setMekorotTabResultsNum(0);
                mIsNewQuerySubmitted = true;
            }
        });
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //tabLayout.setSelectedTabIndicatorColor(Color.parseColor(SELECTED_TAB_INDICATOR_COLOR));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == PSUKIM_TAB_INDEX)
                {
                    setFilterIconClickable(false);
                    mFab.show();
                }
                else if (tab.getPosition() == MEKOROT_TAB_INDEX)
                {
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

    /**
     * Populate prakim and parashot pairs.
     */
    private void populateUriLabelPairs() {
        for (String queryResult : mParashotAndUris)
        {
            int firstMagicIndex = queryResult.indexOf(FetchParashotAndPrakimTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mParashot.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            parashotURILabelPairs.add(new Pair<>(label, uri));
        }
        for (String queryResult : mPrakimAndUris)
        {
            int firstMagicIndex = queryResult.indexOf(FetchParashotAndPrakimTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mPrakim.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            prakimURILabelPairs.add(new Pair<>(label, uri));
        }
    }

    /**
     * Sets the filter to be either clickable or not clickable depending
     * on what we pass.
     *
     * @param isClickable
     */
    public void setFilterIconClickable(boolean isClickable) {
        ImageView filterIcon = findViewById(R.id.filter_icon);
        if (!isClickable)
        {
            filterIcon.setColorFilter(R.color.FilterIconUnavailable);
            filterIcon.setVisibility(View.INVISIBLE);
        }
        else
        {
            filterIcon.setColorFilter(null);
            filterIcon.setVisibility(View.VISIBLE);
        }
    }
}
