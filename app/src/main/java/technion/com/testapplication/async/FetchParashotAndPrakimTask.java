package technion.com.testapplication.async;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.activities.MainActivity;
import technion.com.testapplication.models.ParashotAndPrakim;
import technion.com.testapplication.utils.OneButtonAlert;

/**
 * Created by tomerlevinson on 15/12/2017.
 * Used in order to fetch all parashot and all prakim.
 */
public class FetchParashotAndPrakimTask extends AsyncTask<String, Void, ParashotAndPrakim> {
    private Activity mActivity;
    private OneButtonAlert oneButtonAlert;
    private boolean isError = false;
    public static final String MAGIC_SEPERATOR = "$$$";

    public FetchParashotAndPrakimTask(Activity activity) {
        mActivity = activity;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Runnable finishCallingActivity = new Runnable() {
            @Override
            public void run() {
                mActivity.finish();
            }
        };
        oneButtonAlert = new OneButtonAlert(mActivity,
                mActivity.getResources().getString(R.string.alert_connection_error_info),
                mActivity.getResources().getString(R.string.alert_connection_error_btn),
                finishCallingActivity);
    }

    @Override
    protected ParashotAndPrakim doInBackground(String... params) {
        ArrayList<String> queryResultsParashot = new ArrayList<>();
        ArrayList<String> queryResultsPrakim = new ArrayList<>();
        try {
            // Create parashot query
            com.hp.hpl.jena.query.Query parashotQuery = com.hp.hpl.jena.query.QueryFactory.create(
                    params[0]);
            com.hp.hpl.jena.query.QueryExecution parashotQueryExec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, parashotQuery);

            // Create prakim query
            com.hp.hpl.jena.query.Query prakimQuery = com.hp.hpl.jena.query.QueryFactory.create(
                    params[1]);
            com.hp.hpl.jena.query.QueryExecution prakimQueryExec = com.hp.hpl.jena.query.QueryExecutionFactory.sparqlService(
                    JBSQueries.JBS_ENDPOINT, prakimQuery);

            // Run parashot query
            try {
                ResultSet rs = parashotQueryExec.execSelect();

                while (rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution();
                    String label = rb.get(JBSQueries.JBS_LABEL).toString();
                    String uri = rb.get(JBSQueries.JBS_URI).toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResultsParashot.add(labelAndUri);
                }
            } finally {
                parashotQueryExec.close();
            }

            // Run prakim query
            try {
                ResultSet rs = prakimQueryExec.execSelect();

                while (rs.hasNext()) {
                    QuerySolution rb = rs.nextSolution();
                    String label = rb.get(JBSQueries.JBS_LABEL).toString();
                    String uri = rb.get(JBSQueries.JBS_URI).toString();
                    String labelAndUri = label + MAGIC_SEPERATOR + uri;
                    queryResultsPrakim.add(labelAndUri);
                }
            } finally {
                parashotQueryExec.close();
            }
        } catch (Exception err) {
            isError = true;
            err.printStackTrace();
        }
        return new ParashotAndPrakim(queryResultsParashot, queryResultsPrakim);
    }

    @Override
    protected void onPostExecute(ParashotAndPrakim parashotAndPrakim) {
        super.onPostExecute(parashotAndPrakim);
        if (isError)
        {
            oneButtonAlert.show();
            return;
        }
        Intent startMainActivityIntent = new Intent(mActivity, MainActivity.class);
        ArrayList<String> parashotArray = parashotAndPrakim.getParashot();
        ArrayList<String> prakimArray = parashotAndPrakim.getPrakim();
        startMainActivityIntent.putStringArrayListExtra(
                mActivity.getResources().getString(R.string.parashot_and_uri_extra), parashotArray);
        startMainActivityIntent.putStringArrayListExtra(
                mActivity.getResources().getString(R.string.prakim_and_uri_extra), prakimArray);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mActivity.startActivity(startMainActivityIntent);
        mActivity.finish();
    }
}
