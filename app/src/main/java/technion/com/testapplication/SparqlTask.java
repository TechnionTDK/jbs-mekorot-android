package technion.com.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class SparqlTask extends AsyncTask<String, Void, ArrayList<String>> {
    private static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";
    private static final String TECHNION_ENDPOINT = "http://tdk3.csf.technion.ac.il:8890/sparql";
    private Activity mActivity;
    public static final String MAGIC_SEPERATOR = "$$$";

    public SparqlTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> queryResults = new ArrayList<>();
        try {
            com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(params[0]);
            com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(TECHNION_ENDPOINT, query);

            try {
                ResultSet rs = qexec.execSelect();

                while(rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution() ;
                    String label = rb.get("label").toString();
                    String uri = rb.get("uri").toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResults.add(labelAndUri);
                }
            }
            finally {
                qexec.close();
            }


        }catch( Exception err){
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        Intent startMainActivityIntent = new Intent(mActivity, MainActivity.class);
        startMainActivityIntent.putStringArrayListExtra("queryResults", strings);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mActivity.startActivity(startMainActivityIntent);
        mActivity.finish();
    }
}
