package technion.com.testapplication.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;

import java.util.ArrayList;

import technion.com.testapplication.PsukimListDialog;
import technion.com.testapplication.R;
import technion.com.testapplication.adapters.ViewPagerAdapter;
import technion.com.testapplication.async.FetchParashotAndPrakimTask;
import technion.com.testapplication.fragments.FavoritesTab;
import technion.com.testapplication.fragments.MekorotTab;
import technion.com.testapplication.fragments.PsukimTab;
import technion.com.testapplication.utils.PreferencesUtils;

public class MainActivity extends AppCompatActivity
        implements PsukimTab.OnMoveToMekorotTabListener,
        MekorotTab.MekorotChangesListener,
        FavoritesTab.FavoritesChangeListener{

    private ArrayList<String> mParashotAndUris;
    private ArrayList<String> mPrakimAndUris;
    private final ArrayList<String> mParashot = new ArrayList<>();
    private final ArrayList<String> mPrakim = new ArrayList<>();
    private ArrayList<Pair<String, String>> parashotURILabelPairs = new ArrayList<>();
    private ArrayList<Pair<String, String>> prakimURILabelPairs = new ArrayList<>();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private static final int PSUKIM_FRAG_POSITION = 0;
    private static final int MEKOROT_FRAG_POSITION = 1;
    private static boolean mIsNewQuerySubmitted = false;
    private PsukimTab mPsukimTab;
    private MekorotTab mMekorotTab;
    private FavoritesTab mFavoritesTab;
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
     * Sets the filter to be either clickable or not clickable depending
     * on what we pass.
     * @param isClickable
     */
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
        mFavoritesTab = new FavoritesTab();
        mViewPagerAdapter.addFragment(mPsukimTab, getResources().getString(R.string.psukim));
        mViewPagerAdapter.addFragment(mMekorotTab, getResources().getString(R.string.mekorot));
        mViewPagerAdapter.addFragment(mFavoritesTab, getResources().getString(R.string.favorites));
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
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
                } else if (tab.getPosition() == 2) {
                    // Favorites tab
                    mFab.hide();
                    setFilterIconClickable(false);
                    PreferencesUtils.getSharedPreferencesByFileName("favorites", getApplicationContext());
                    FavoritesTab favoritesTab = (FavoritesTab) mViewPagerAdapter.getItem(2);
                    favoritesTab.setRecyclerViewAdapter();
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
        mekorotTabFrag.notifyFromFavorites();
    }

    @Override
    public void onPsukimSelected(boolean areNewSelected) {
        mIsNewQuerySubmitted = areNewSelected;
    }

    /**
     * Sets the onClick listener for the filter icon.
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
     * Sets the number of results to be seen at the mekorot tab.
     * @param numOfResults
     */
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
    public void updateFavoritesNum(int numOfFavorites) {
        setFavoriteTabResultsNum(numOfFavorites);
    }

    /**
     * This method takes care of back pressing between tabs in the view pager.
     */
    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
        } else {
            finish();
        }

    }

    @Override
    public void setFavoriteTabResultsNum(int numOfResults) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        TabLayout.Tab favoritesTab = tabLayout.getTabAt(2);
        if (favoritesTab != null) {
            if (numOfResults == 0) {
                favoritesTab.setText(getResources().getString(R.string.favorites));
            } else {
                favoritesTab.setText(getResources().getString(R.string.favorites) + " (" + Integer.toString(
                        numOfResults) + ")");
            }

        }
    }
}
