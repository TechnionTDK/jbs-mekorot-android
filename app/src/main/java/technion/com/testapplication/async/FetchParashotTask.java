package technion.com.testapplication.async;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.activities.MainActivity;

/**
 * Created by tomerlevinson on 15/12/2017.
 */
public class FetchParashotTask extends AsyncTask<String, Void, ArrayList<String>> {
    private Activity mActivity;
    public static final String MAGIC_SEPERATOR = "$$$";

    public FetchParashotTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> queryResults = new ArrayList<>();
        try {
            com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(
                    params[0]);
            com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, query);

            try {
                ResultSet rs = qexec.execSelect();

                while (rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution();
                    String label = rb.get(JBSQueries.JBS_LABEL).toString();
                    String uri = rb.get(JBSQueries.JBS_URI).toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResults.add(labelAndUri);
                }
            } finally {
                qexec.close();
            }


        } catch (Exception err) {
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<String> parashotAndUris) {
        super.onPostExecute(parashotAndUris);
        Intent startMainActivityIntent = new Intent(mActivity, MainActivity.class);
        startMainActivityIntent.putStringArrayListExtra(
                mActivity.getResources().getString(R.string.parashot_and_uri_extra), parashotAndUris);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mActivity.startActivity(startMainActivityIntent);
        mActivity.finish();
    }
}
