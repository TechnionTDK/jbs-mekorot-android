package technion.com.testapplication.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import technion.com.testapplication.async.FetchParashotAndPrakimTask;
import technion.com.testapplication.R;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mParashotAndUris;
    private ArrayList<String> mPrakimAndUris;
    private ArrayList<String> mAdapterArrayList = new ArrayList<>();
    private final ArrayList<String> mParashot = new ArrayList<>();
    private final ArrayList<String> mPrakim = new ArrayList<>();
    private ArrayList<Pair<String, String>> parashotURILabelPairs = new ArrayList<>();
    private ArrayList<Pair<String, String>> prakimURILabelPairs = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private int mSpinnerCheck = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_favorite:
                return true;
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
     *  1) Perek.
     *  2) Parasha.
     *  Each choice will trigger invalidation of the autocomplete text view adapter.
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
     * Set toolbar for this activity.
     * This will also set the spinner.
     */
    public void setToolbar() {
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setSpinner();
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
                startActivity(intent);
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
        setContentView(R.layout.activity_main);
        // Get Parashot
        // TODO: Add get prakim.
        Intent intent = getIntent();
        mParashotAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.parashot_and_uri_extra));
        mPrakimAndUris = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.prakim_and_uri_extra));
        populateUriLabelPairs();
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        forceRTLIfSupported();
        setToolbar();
        setAutoCompleteTextView();
    }
}
