package technion.com.testapplication.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import java.util.ArrayList;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.activities.ResultsActivity;

/**
 * Created by tomerlevinson on 16/01/2018.
 */
public class FetchHighlightsForMakorTask extends AsyncTask<String, Void, ArrayList<Pair<String, String>>> {
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private Context mContext;

    public FetchHighlightsForMakorTask(Activity activity, Context context) {
        mActivity = activity;
        mContext = context;
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected ArrayList<Pair<String, String>> doInBackground(String... params) {
        ArrayList<Pair<String, String>> queryResults = new ArrayList<>();
        try
        {
            QueryEngineHTTP queryEngineHTTP = new QueryEngineHTTP(JBSQueries.JBS_ENDPOINT,
                                                                  params[0]);
            try
            {
                ResultSet rs = queryEngineHTTP.execSelect();

                while (rs.hasNext())
                {
                    QuerySolution rb = rs.nextSolution();
                    String span = rb.get("span").toString();
                    String pasukText = rb.get("pasuk_text").toString();
                    queryResults.add(new Pair<>(span, pasukText));
                }
            } finally
            {
                queryEngineHTTP.close();
            }


        } catch (Exception err)
        {
            err.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(ArrayList<Pair<String, String>> highlightedPsukimIndices) {
        super.onPostExecute(highlightedPsukimIndices);
        if (mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
        if (mActivity instanceof ResultsActivity)
        {
            ((ResultsActivity) mActivity).highlightPsukim(highlightedPsukimIndices);
        }
    }
}
