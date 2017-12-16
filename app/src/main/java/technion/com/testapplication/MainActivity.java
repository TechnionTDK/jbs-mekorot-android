package technion.com.testapplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mQueryResults;
    private ArrayList<String> mLabels = new ArrayList<>();
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
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        spinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.White), PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout,
                getResources().getStringArray(R.array.spinner_items));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                TextView currentTv = (TextView) view;
                if (((TextView) view).getText().equals("פרק")){
                    actv.setHint("אנא הכנס/י פרק");
                } else {
                    actv.setHint("אנא הכנס/י פרשה");
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
        String[] languages={"Android ","java","IOS","SQL","JDBC","Web services"};
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, mLabels);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PsukimActivity.class);
                String message = (String) ((TextView) view).getText();
                String uriToSend = "";
                for (Pair<String,String> uriLabel:URILabelPairs) {
                    if (uriLabel.first.equals(message)) {
                        uriToSend = uriLabel.second;
                    }
                }
                intent.putExtra("extraMessage", message);
                intent.putExtra("extraUri", uriToSend);
                startActivity(intent);
            }
        });
    }

    private void populateUriLabelPairs() {
        for (String queryResult: mQueryResults) {
            int firstMagicIndex = queryResult.indexOf(SparqlTask.MAGIC_SEPERATOR);
            String label = queryResult.substring(0, firstMagicIndex);
            mLabels.add(label);
            String uri = queryResult.substring(firstMagicIndex + 3);
            URILabelPairs.add(new Pair(label, uri));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mQueryResults = (ArrayList<String>) intent.getExtras().get("queryResults");
        populateUriLabelPairs();
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.LightBlue));
        forceRTLIfSupported();
        setToolbar();
        setAutoCompleteTextView();
        final TextView btnJSON = (TextView) findViewById(R.id.btnJSON);

        final String dbpediaQuery =
                "PREFIX p: <http://dbpedia.org/property/>"+
                        "PREFIX dbpedia: <http://dbpedia.org/resource/>"+
                        "PREFIX category: <http://dbpedia.org/resource/Category:>"+
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
                        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
                        "PREFIX geo: <http://www.georss.org/georss/>"+
                        "PREFIX geo-pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
                        "SELECT ?label ?lat ?long ?comment "+
                        "WHERE {"+
                        " ?subject geo-pos:lat ?lat ."+
                        " ?subject geo-pos:long ?long ."+
                        " ?subject rdfs:label ?label ."+
                        " ?subject rdfs:comment ?comment ."+
                        " FILTER( (?lat > 48.23185544 && ?lat < 49.23185844 && ?long > 16.31660208 && ?long < 17.31660208) && (lang(?comment) = 'en') && (lang(?label) = 'en') )"+
                        " .} ORDER BY ?lat ?long LIMIT 15 ";
        final String technionQuery = "PREFIX jbr: <http://jbs.technion.ac.il/resource/>" +
                "PREFIX jbo: <http://jbs.technion.ac.il/ontology/>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+
                "PREFIX dco: <http://purl.org/dc/terms/>"+
                "SELECT ?uri ?label ?position "+
                "WHERE {" +
                " ?uri a jbo:ParashaTorah ." +
                " ?uri rdfs:label ?label ."+
                " ?uri jbo:position ?position" +
                " .} ORDER BY ASC(xsd:integer(?position))";

        final String dbpediaEndpoint = "http://dbpedia.org/sparql";
        final String technionEndpoint = "http://tdk3.csf.technion.ac.il:8890/sparql";
        HandlerThread handlerThread = new HandlerThread("URLConnection");
        handlerThread.start();
        Handler mainHandler = new Handler(handlerThread.getLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(technionQuery);
                    com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(technionEndpoint, query);

                    try {
                        ResultSet rs = qexec.execSelect();

                        while(rs.hasNext()) {

                            QuerySolution rb = rs.nextSolution() ;
                            RDFNode x = rb.get("label");
                            //Resource xx = rb.getResource("label");

                            if ( x.isLiteral() )
                            {
                                //Literal titleStr = (Literal)xx  ;
//                                btnJSON.setText("    " + x ) ;
                            }
//                            else
//                                btnJSON.setText("Nope! : "+ x) ;
                        }
                    }
                    finally {
                        qexec.close();
                    }


                }catch( Exception err){
                    err.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);


//        com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(queryString);
//        com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
//
//        try {
//            ResultSet rs = qexec.execSelect();
//
//            while(rs.hasNext()) {
//
//                QuerySolution rb = rs.nextSolution() ;
//                RDFNode x = rb.get("label");
//                //Resource xx = rb.getResource("label");
//
//                if ( x.isLiteral() )
//                {
//                    //Literal titleStr = (Literal)xx  ;
//                    btnJSON.setText("    " + x ) ;
//                }
//                else
//                    btnJSON.setText("Nope! : "+ x) ;
//            }
//        }
//        finally {
//            qexec.close();
//        }
    }
}
