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

import technion.com.testapplication.async.FetchParashotTask;
import technion.com.testapplication.R;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mQueryResults;
    private ArrayList<String> mParashot = new ArrayList<>();
    private ArrayList<Pair<String, String>> URILabelPairs = new ArrayList<>();

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
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

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
                } else {
                    actv.setHint(getResources().getString(R.string.enter_parasha));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setToolbar() {
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setSpinner();
    }

    public void setAutoCompleteTextView() {
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, mParashot);
        //TODO: change adapter in case of prakim.
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PsukimActivity.class);
                String perekOrParashaName = (String) ((TextView) view).getText();
                String perekOrParashaUri = "";
                for (Pair<String, String> uriLabel : URILabelPairs) {
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

    private void populateUriLabelPairs() {
        for (String queryResult : mQueryResults) {
            int firstMagicIndex = queryResult.indexOf(FetchParashotTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mParashot.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            URILabelPairs.add(new Pair(label, uri));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Parashot
        // TODO: Add get prakim.
        Intent intent = getIntent();
        mQueryResults = (ArrayList<String>) intent.getExtras().get(
                getResources().getString(R.string.parashot_and_uri_extra));
        populateUriLabelPairs();
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        forceRTLIfSupported();
        setToolbar();
        setAutoCompleteTextView();
    }
}
