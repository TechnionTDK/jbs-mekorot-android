package technion.com.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 13/12/2017.
 */
public class PsukimActivity extends AppCompatActivity {

    private String mParashaName;
    private String mParashaUri;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_favorite).setVisible(false);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psukim_list);
        // Get extras
        Intent intent = getIntent();
        mParashaName = intent.getStringExtra("extraMessage");
        String uri = intent.getStringExtra("extraUri");
        mParashaUri = uri.substring(uri.lastIndexOf("/") + 1);
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mParashaName);
        String psukimByParashaQuery =
                " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        " PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                        " PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                        " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                        " PREFIX jbr: <http://jbs.technion.ac.il/resource/>\n" +
                        " PREFIX jbo: <http://jbs.technion.ac.il/ontology/>\n" +
                        " SELECT distinct ?pasuk ?position ?pasuk_text WHERE {\n" +
                        " ?pasuk a jbo:Pasuk; jbo:within jbr:" + mParashaUri + "; jbo:position ?position; jbo:text ?pasuk_text.\n" +
                        " ?perush jbo:interprets ?pasuk; jbo:text ?text.\n" +
                        "} ORDER BY ASC(xsd:integer(?position))";
        FetchPsukimTask fetchPsukimTask = new FetchPsukimTask(this);
        fetchPsukimTask.execute(psukimByParashaQuery);
        // Initialize recycler view.
    }

    public void setRecyclerViewAdapter(ArrayList<PasukModel> psukim) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new PsukimRecyclerViewAdapter(psukim);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

}
